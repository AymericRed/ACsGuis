package fr.aym.acsguis.api;

import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.cssengine.CssGuisManager;
import fr.aym.acsguis.cssengine.CssHudHandler;
import fr.aym.acsguis.cssengine.InWorldGuisManager;
import fr.aym.acsguis.event.CssReloadEvent;
import fr.aym.acsguis.sqript.NoSqriptSupport;
import fr.aym.acsguis.sqript.SqriptCompatiblity;
import fr.aym.acsguis.sqript.SqriptSupport;
import fr.aym.acsguis.utils.CssReloadOrigin;
import fr.aym.acslib.ACsLib;
import fr.aym.acslib.api.ACsRegisteredService;
import fr.aym.acslib.api.services.ThreadedLoadingService;
import fr.aym.acslib.api.services.error.ErrorCategory;
import fr.aym.acslib.api.services.error.ErrorManagerService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * ACsGuiApi main class <br>
 * You should register you css sheets here, during the fml pre-initialization <br>
 * Useful function to show the guis are also provided here
 */
@SideOnly(Side.CLIENT)
@ACsRegisteredService(name = ACsGuiApi.RES_LOC_ID, version = ACsGuiApi.VERSION, sides = Side.CLIENT, interfaceClass = ACsGuiApiService.class, initOnStartup = true)
public class ACsGuiApi implements ACsGuiApiService {
    public static final String RES_LOC_ID = ACsGuiApiService.RES_LOC_ID;
    public static final String VERSION = "1.3.0-dev9";
    public static final Logger log = LogManager.getLogger("ACsGuis");

    private static ErrorManagerService errorTracker;
    private static ErrorCategory CSS_ERROR_TYPE;

    /**
     * Styles registry and gui helper
     */
    private static final CssGuisManager manager = new CssGuisManager();

    private static SqriptSupport sqriptSupport = new NoSqriptSupport();

    public ACsGuiApi() {
        log.info("Initializing ACsGuis API by Aym', version " + VERSION);
        MinecraftForge.EVENT_BUS.register(manager);

        errorTracker = ACsLib.getPlatform().provideService(ErrorManagerService.class);
        CSS_ERROR_TYPE = errorTracker.createErrorCategory(new ResourceLocation(RES_LOC_ID, "css"), "Css");
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public void onFMLStateEvent(FMLStateEvent event) {
        if (event instanceof FMLPreInitializationEvent) {
            if (Loader.isModLoaded("sqript")) {
                log.info("Sqript detected, loading compatibility");
                sqriptSupport = new SqriptCompatiblity();
            }
        } else if (event instanceof FMLInitializationEvent) {
            ((SimpleReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(manager);
        }
    }

    /**
     * Registers a css style sheet to (re)load when resources packs are loaded <br>
     * Register all the sheets that you are using here, before fml initialization
     *
     * @param location The style sheet to load
     */
    public static void registerStyleSheetToPreload(ResourceLocation location) {
        manager.registerStyleSheetToPreload(location);
    }

    /**
     * Loads a GuiFrame in another thread, then shows it <br>
     * Note : the css fonts are loaded in the client thread (it needs open gl)
     *
     * @param guiName     The gui name, used for log messages
     * @param guiInstance A function returning the gui, called by the external thread
     */
    public static void asyncLoadThenShowGui(String guiName, Callable<GuiFrame> guiInstance) {
        manager.asyncLoadThenShowGui(guiName, guiInstance);
    }

    /**
     * Immediately shows the given GuiFrame on the screen <br>
     * This doesn't support css reloading when the gui is shown. Use asyncLoadThenShowGui if you want to reload the css when displaying the gui.
     *
     * @param guiInstance The gui to display as interface
     * @throws IllegalArgumentException If the gui wants to reload the css code when displayed
     * @see CssHudHandler
     */
    public static void showGui(GuiFrame guiInstance) {
        manager.showGui(guiInstance);
    }

    /**
     * Loads a GuiFrame in another thread, then shows it on the HUD <br>
     * A hud gui is only a visual gui, you can't interact with it <br>
     * Note : the css fonts are loaded in the client thread (it needs open gl)
     *
     * @param guiName     The gui name, used for log messages
     * @param guiInstance A function returning the gui, called by the external thread
     */
    public static void asyncLoadThenShowHudGui(String guiName, Callable<GuiFrame> guiInstance) {
        manager.asyncLoadThenShowHudGui(guiName, guiInstance);
    }

    /**
     * Immediately shows the given GuiFrame on the HUD <br>
     * A hud gui is only a visual gui, you can't interact with it <br>
     * This doesn't support css reloading when the gui is shown. Use asyncLoadThenShowHudGui if you want to reload the css when displaying the gui.
     *
     * @param guiInstance The gui to display on hud
     * @throws IllegalArgumentException If the gui wants to reload the css code when displayed
     * @see CssHudHandler
     */
    public static void showHudGui(GuiFrame guiInstance) {
        manager.showHudGui(guiInstance);
    }

    /**
     * Loads a GuiFrame in another thread, then shows it on the HUD <br>
     * A hud gui is only a visual gui, you can't interact with it <br>
     * Note : the css fonts are loaded in the client thread (it needs open gl)
     *
     * @param hudIndex    The index of the hud, used to change the display order of the huds
     * @param guiName     The gui name, used for log messages
     * @param guiInstance A function returning the gui, called by the external thread
     */
    public static void asyncLoadThenShowHudGui(int hudIndex, String guiName, Callable<GuiFrame> guiInstance) {
        manager.asyncLoadThenShowHudGui(hudIndex, guiName, guiInstance);
    }

    /**
     * Immediately shows the given GuiFrame on the HUD <br>
     * A hud gui is only a visual gui, you can't interact with it <br>
     * This doesn't support css reloading when the gui is shown. Use asyncLoadThenShowHudGui if you want to reload the css when displaying the gui.
     *
     * @param hudIndex    The index of the hud, used to change the display order of the huds
     * @param guiInstance The gui to display on hud
     * @throws IllegalArgumentException If the gui wants to reload the css code when displayed
     * @see CssHudHandler
     */
    public static void showHudGui(int hudIndex, GuiFrame guiInstance) {
        manager.showHudGui(hudIndex, guiInstance);
    }

    /**
     * @return the list of currently displayed hud guis
     */
    public static List<GuiFrame.APIGuiScreen> getDisplayHudGuis() {
        return manager.getHud().getDisplayedHuds();
    }

    /**
     * Closes the given hud gui
     */
    public static void closeHudGui(GuiFrame hudFrame) {
        manager.getHud().closeHudGui(hudFrame);
    }

    /**
     * Closes all the hud guis of the given class
     *
     * @return true if a gui was closed
     */
    public static boolean closeHudGui(Class<? extends GuiFrame> hudFrameClass) {
        return manager.getHud().closeHudGui(hudFrameClass);
    }

    /**
     * Closes all the currently displayed hud guis
     */
    public static void closeAllHudGuis() {
        manager.getHud().closeAllHudGuis();
    }

    public static InWorldGuisManager getInWorldGuisManager() {
        return manager.getInWorldGuisManager();
    }

    /**
     * Forces reload of all styles and fonts
     *
     * @param frame If not null, will handle error gui
     */
    public static void reloadCssStyles(@Nullable GuiFrame frame) {
        getSqriptSupport().onCssInit();
        CssReloadEvent.Pre event = new CssReloadEvent.Pre(frame != null ? new CssReloadOrigin.HotCssReloadOrigin(manager, frame) : new CssReloadOrigin(manager, false));
        if (MinecraftForge.EVENT_BUS.post(event)) return;

        ACsLib.getPlatform().provideService(ThreadedLoadingService.class).addTask(ThreadedLoadingService.ModLoadingSteps.FINISH_LOAD, "css_load", () -> {
            try {
                event.getReloadOrigin().loadStyles();
            } catch (Exception e) { //This should not happen with our reload origin
                throw new RuntimeException("Fatal error while loading css sheets", e);
            }
            long time = System.currentTimeMillis();
            while (Minecraft.getMinecraft().fontRenderer == null) { //Don't listen idea, it can be null because we do this in pre initialization
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug("Font renderer wait took " + (System.currentTimeMillis() - time) + " ms");
        }, () -> {
            event.getReloadOrigin().loadFonts();
            event.getReloadOrigin().postLoad();
        });
    }

    /**
     * @return The Sqript support for ACsGuis
     */
    public static SqriptSupport getSqriptSupport() {
        return sqriptSupport;
    }

    public static ErrorManagerService getErrorTracker() {
        return errorTracker;
    }

    public static ErrorCategory getCssErrorType() {
        return CSS_ERROR_TYPE;
    }
}
