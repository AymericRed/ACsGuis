package fr.aym.acsguis.cssengine;

import fr.aym.acsguis.api.ACsGuiApi;
import fr.aym.acsguis.api.ACsGuisErrorTracker;
import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.cssengine.parsing.ACsGuisCssParser;
import fr.aym.acsguis.event.CssReloadEvent;
import fr.aym.acsguis.utils.GuiCssError;
import fr.aym.acsguis.utils.CssReloadOrigin;
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
import java.util.function.Predicate;

import static fr.aym.acsguis.api.ACsGuiApi.log;

/**
 * Provides a method to async load a css gui
 */
public class CssGuiManager implements ISelectiveResourceReloadListener
{
    /** The local tread id */
    private int id;
    private final List<ResourceLocation> CSS_SHEETS = new ArrayList<>();
    private final CssHudHandler hud = new CssHudHandler();

    public CssGuiManager()
    {
        registerStyleSheetToPreload(ACsGuisCssParser.DEFAULT_STYLE_SHEET);
        MinecraftForge.EVENT_BUS.register(new CssHudHandler());
    }

    public CssHudHandler getHud() {
        return hud;
    }

    /**
     * Register a css style sheet to (re)load when resources packs are loaded <br>
     *     Register all the sheets that you are using here, before (of during) mod initialization
     *
     * @param location The style sheet to load
     */
    public void registerStyleSheetToPreload(ResourceLocation location) {
        if(!CSS_SHEETS.contains(location))
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
        for(ResourceLocation r : CSS_SHEETS) {
            bar.step(r.toString());
            try {
                ACsGuisCssParser.parseCssSheet(r);
            } catch (Exception e) {
                origin.handleException(r, e);
            }
        }
        ProgressManager.pop(bar);
    }

    /**
     * Loads a GuiFrame in another thread, then shows it <br>
     *     Note : the css fonts are loaded in the client thread (needs open gl)
     *
     * @param guiName The gui name, used for log messages
     * @param guiInstance A function returning the gui, called by the external thread
     */
    public void asyncLoadThenShowGui(String guiName, Callable<GuiFrame> guiInstance) {
        Minecraft.getMinecraft().ingameGUI.setOverlayMessage("Loading CSS gui "+guiName+"...", true);
        new Thread(() -> {
            try {
                Minecraft.getMinecraft().ingameGUI.setOverlayMessage("Loading CSS gui "+guiName+"...", true);
                GuiFrame gui = guiInstance.call();
                boolean reloadCss = gui.needsCssReload();
                CssReloadEvent.Pre event = null;
                if(reloadCss) {
                    Minecraft.getMinecraft().ingameGUI.setOverlayMessage("Loading css...", true);
                    event = new CssReloadEvent.Pre(new CssReloadOrigin.HotCssReloadOrigin(this, gui));
                    if(MinecraftForge.EVENT_BUS.post(event)) return;
                    event.getReloadOrigin().loadStyles();
                }
                Minecraft.getMinecraft().ingameGUI.setOverlayMessage("Loading CSS gui "+guiName+"...", true);
                CssReloadEvent.Pre finalEvent = event;
                Minecraft.getMinecraft().addScheduledTask( () -> {
                    if(reloadCss) {
                        ACsGuisCssParser.loadFonts((r, e) -> {
                            log.error("Error while loading css font " + r.toString(), e);
                            ACsGuisErrorTracker.addError("Css fonts", "Font " + r.toString(), e, ACsGuisErrorTracker.ErrorLevel.LOW);
                        });
                        finalEvent.getReloadOrigin().postLoad();
                    }
                    Minecraft.getMinecraft().ingameGUI.setOverlayMessage("", false);
                    Minecraft.getMinecraft().displayGuiScreen(gui.getGuiScreen());
                });
            } catch (Throwable e) {
                log.fatal("Cannot show css gui "+guiName, e);
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    Minecraft.getMinecraft().displayGuiScreen(new GuiCssError(guiName, e).getGuiScreen());
                });
            }
        }, "CssGuiLoader#"+id
        ).start();
        id++;
    }

    /**
     * Loads a GuiFrame in another thread, then shows it on the HUD <br>
     *     Note : the css fonts are loaded in the client thread (needs open gl)
     *
     * @param guiName The gui name, used for log messages
     * @param guiInstance A function returning the gui, called by the external thread
     * @see CssHudHandler
     */
    public void asyncLoadThenShowHudGui(String guiName, Callable<GuiFrame> guiInstance) {
        Minecraft.getMinecraft().ingameGUI.setOverlayMessage("Loading CSS hud "+guiName+"...", true);
        new Thread(() -> {
            try {
                Minecraft.getMinecraft().ingameGUI.setOverlayMessage("Loading CSS hud "+guiName+"...", true);
                GuiFrame gui = guiInstance.call();
                boolean reloadCss = gui.needsCssReload();
                CssReloadEvent.Pre event = null;
                if(reloadCss) {
                    Minecraft.getMinecraft().ingameGUI.setOverlayMessage("Loading css...", true);
                    event = new CssReloadEvent.Pre(new CssReloadOrigin.HotCssReloadOrigin(this, gui));
                    if(MinecraftForge.EVENT_BUS.post(event)) return;
                    event.getReloadOrigin().loadStyles();
                }
                Minecraft.getMinecraft().ingameGUI.setOverlayMessage("Loading CSS hud "+guiName+"...", true);
                CssReloadEvent.Pre finalEvent = event;
                Minecraft.getMinecraft().addScheduledTask( () -> {
                    if(reloadCss) {
                        ACsGuisCssParser.loadFonts((r, e) -> {
                            log.error("Error while loading css font " + r.toString(), e);
                            ACsGuisErrorTracker.addError("Css fonts", "Font " + r.toString(), e, ACsGuisErrorTracker.ErrorLevel.LOW);
                        });
                        finalEvent.getReloadOrigin().postLoad();
                    }
                    Minecraft.getMinecraft().ingameGUI.setOverlayMessage("", false);
                    getHud().setCurrentHUD(gui);
                });
            } catch (Throwable e) {
                log.fatal("Cannot show css gui "+guiName, e);
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    Minecraft.getMinecraft().displayGuiScreen(new GuiCssError(guiName, e).getGuiScreen());
                });
            }
        }, "CssGuiLoader#"+id
        ).start();
        id++;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
        if(resourcePredicate.test(VanillaResourceType.TEXTURES))
        {
            ACsGuiApi.reloadCssStyles(null);
        }
    }
}
