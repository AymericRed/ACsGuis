package fr.aym.acsguis.cssengine;

import fr.aym.acsguis.api.ACsGuiApi;
import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.cssengine.parsing.ACsGuisCssParser;
import fr.aym.acsguis.event.CssReloadEvent;
import fr.aym.acsguis.utils.CssReloadOrigin;
import fr.aym.acsguis.utils.GuiCssError;
import fr.aym.acslib.ACsLib;
import fr.aym.acslib.api.services.ThreadedLoadingService;
import fr.aym.acslib.api.services.error.ErrorLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.client.resource.VanillaResourceType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ProgressManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static fr.aym.acsguis.api.ACsGuiApi.log;

/**
 * Provides a method to async load a css gui
 */
public class CssGuisManager implements ISelectiveResourceReloadListener {
    private final List<ResourceLocation> CSS_SHEETS = new ArrayList<>();
    private final CssHudHandler hud = new CssHudHandler();
    private final InWorldGuisManager inWorldGuisManager = new InWorldGuisManager();
    private final AtomicBoolean isReloading = new AtomicBoolean(false);

    public CssGuisManager() {
        registerStyleSheetToPreload(ACsGuisCssParser.DEFAULT_STYLE_SHEET);
        MinecraftForge.EVENT_BUS.register(hud);
        MinecraftForge.EVENT_BUS.register(inWorldGuisManager);
    }

    public CssHudHandler getHud() {
        return hud;
    }

    public InWorldGuisManager getInWorldGuisManager() {
        return inWorldGuisManager;
    }

    /**
     * Register a css style sheet to (re)load when resources packs are loaded <br>
     * Register all the sheets that you are using here, before (of during) mod initialization
     *
     * @param location The style sheet to load
     */
    public void registerStyleSheetToPreload(ResourceLocation location) {
        if (isReloading.get())
            throw new IllegalStateException("Cannot register css sheets while reloading");
        if (!CSS_SHEETS.contains(location))
            CSS_SHEETS.add(location);
    }

    /**
     * Heavy method that reloads all css styles, used for debug or resource pack reload
     * <br><br>
     * NOTE : Prefer implementing needsCssReload in your gui
     * <strong>DON'T CALL THIS, USE METHOD OF {@link ACsGuiApi} !</strong>
     *
     * @param origin Handles loading errors
     */
    public void reloadAllCssSheets(CssReloadOrigin origin) {
        log.info("Loading CSS sheets...");
        ProgressManager.ProgressBar bar = ProgressManager.push("Load CSS sheets", CSS_SHEETS.size());
        isReloading.set(true);
        for (ResourceLocation r : CSS_SHEETS) {
            bar.step(r.toString());
            try {
                ACsGuisCssParser.parseCssSheet(r);
            } catch (Exception e) {
                origin.handleException(r, e);
            }
        }
        ;
        isReloading.set(false);
        ProgressManager.pop(bar);
    }

    private void loadGui(GuiFrame.GuiType guiType, String loadingName, Callable<GuiFrame> guiInstance, Consumer<GuiFrame> displayGui) {
        try {
            Minecraft.getMinecraft().ingameGUI.setOverlayMessage("Loading " + loadingName + "...", true);
            GuiFrame gui = guiInstance.call();
            gui.setGuiType(guiType);
            boolean reloadCss = gui.needsCssReload();
            CssReloadEvent.Pre event = null;
            if (reloadCss) {
                Minecraft.getMinecraft().ingameGUI.setOverlayMessage("Loading css...", true);
                event = new CssReloadEvent.Pre(new CssReloadOrigin.HotCssReloadOrigin(this, gui));
                if (MinecraftForge.EVENT_BUS.post(event)) return;
                event.getReloadOrigin().loadStyles();
            }
            Minecraft.getMinecraft().ingameGUI.setOverlayMessage("Loading " + loadingName + "...", true);
            CssReloadEvent.Pre finalEvent = event;

            //Don't use ThreadedLoader scheduler : it's not updated after mc launch
            Minecraft.getMinecraft().addScheduledTask(() -> {
                if (reloadCss) {
                    ACsGuisCssParser.loadFonts((r, e) -> {
                        log.error("Error while loading css font " + r.toString(), e);
                        ACsGuiApi.getErrorTracker().addError("ACsGuis reload", ACsGuiApi.getCssErrorType(), "css_font_load_error", ErrorLevel.LOW, r.toString(), null, e);
                    });
                    finalEvent.getReloadOrigin().postLoad();
                }
                Minecraft.getMinecraft().ingameGUI.setOverlayMessage("", false);
                displayGui.accept(gui);
            });
        } catch (Throwable e) {
            log.fatal("Cannot show " + loadingName, e);
            Minecraft.getMinecraft().addScheduledTask(() -> {
                Minecraft.getMinecraft().displayGuiScreen(new GuiCssError(loadingName, e).getGuiScreen());
            });
        }
    }

    /**
     * Loads a GuiFrame in another thread, then shows it <br>
     * Note : the css fonts are loaded in the client thread (needs open gl)
     *
     * @param guiName     The gui name, used for log messages
     * @param guiInstance A function returning the gui, called by the external thread
     */
    public void asyncLoadThenShowGui(String guiName, Callable<GuiFrame> guiInstance) {
        Minecraft.getMinecraft().ingameGUI.setOverlayMessage("Loading CSS gui " + guiName + "...", true);

        ACsLib.getPlatform().provideService(ThreadedLoadingService.class).addTask(ThreadedLoadingService.ModLoadingSteps.NEVER, "css_load",
                () -> loadGui(GuiFrame.GuiType.ON_SCREEN, "css gui " + guiName, guiInstance, gui -> Minecraft.getMinecraft().displayGuiScreen(gui.getGuiScreen())));
    }

    /**
     * Show the given GuiFrame on the screen <br>
     * This doesn't support css reloading when the gui is shown. Use asyncLoadThenShowGui if you want to reload the css when displaying the gui.
     *
     * @param guiInstance The gui to display as interface
     * @throws IllegalArgumentException If the gui wants to reload the css code when displayed
     * @see CssHudHandler
     */
    public void showGui(GuiFrame guiInstance) {
        guiInstance.setGuiType(GuiFrame.GuiType.ON_SCREEN);
        if (guiInstance.needsCssReload()) {
            throw new IllegalArgumentException("Disable the css reload in your gui, or use asyncLoadThenShowGui method if you want to reload the css styles when displaying your gui.");
        }
        Minecraft.getMinecraft().displayGuiScreen(guiInstance.getGuiScreen());
    }

    /**
     * Loads a GuiFrame in another thread, then shows it on the HUD <br>
     * Note : the css fonts are loaded in the client thread (needs open gl)
     *
     * @param guiName     The gui name, used for log messages
     * @param guiInstance A function returning the gui, called by the external thread
     * @see CssHudHandler
     */
    public void asyncLoadThenShowHudGui(String guiName, Callable<GuiFrame> guiInstance) {
        asyncLoadThenShowHudGui(hud.getDisplayedHuds().size(), guiName, guiInstance);
    }

    /**
     * Loads a GuiFrame in another thread, then shows it on the HUD <br>
     * Note : the css fonts are loaded in the client thread (needs open gl)
     *
     * @param hudIndex    The index of the hud, used to change the display order of the huds
     * @param guiName     The gui name, used for log messages
     * @param guiInstance A function returning the gui, called by the external thread
     * @see CssHudHandler
     */
    public void asyncLoadThenShowHudGui(int hudIndex, String guiName, Callable<GuiFrame> guiInstance) {
        Minecraft.getMinecraft().ingameGUI.setOverlayMessage("Loading CSS hud " + guiName + "...", true);
        ACsLib.getPlatform().provideService(ThreadedLoadingService.class).addTask(ThreadedLoadingService.ModLoadingSteps.NEVER, "css_load",
                () -> loadGui(GuiFrame.GuiType.OVERLAY, "css hud " + guiName, guiInstance, gui -> getHud().showHudGui(hudIndex, gui)));
    }

    /**
     * Show the given GuiFrame on the HUD <br>
     * This doesn't support css reloading when the gui is shown. Use asyncLoadThenShowHudGui if you want to reload the css when displaying the gui.
     *
     * @param guiInstance The gui to display on hud
     * @throws IllegalArgumentException If the gui wants to reload the css code when displayed
     * @see CssHudHandler
     */
    public void showHudGui(GuiFrame guiInstance) {
        showHudGui(hud.getDisplayedHuds().size(), guiInstance);
    }

    /**
     * Show the given GuiFrame on the HUD <br>
     * This doesn't support css reloading when the gui is shown. Use asyncLoadThenShowHudGui if you want to reload the css when displaying the gui.
     *
     * @param hudIndex    The index of the hud, used to change the display order of the huds
     * @param guiInstance The gui to display on hud
     * @throws IllegalArgumentException If the gui wants to reload the css code when displayed
     * @see CssHudHandler
     */
    public void showHudGui(int hudIndex, GuiFrame guiInstance) {
        guiInstance.setGuiType(GuiFrame.GuiType.OVERLAY);
        if (guiInstance.needsCssReload()) {
            throw new IllegalArgumentException("Disable the css reload in your gui, or use asyncLoadThenShowHudGui method if you want to reload the css styles when displaying your gui.");
        }
        getHud().showHudGui(hudIndex, guiInstance);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
        if (resourcePredicate.test(VanillaResourceType.TEXTURES))
            ACsGuiApi.reloadCssStyles(null);
    }
}
