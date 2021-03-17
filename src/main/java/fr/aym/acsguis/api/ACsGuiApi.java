package fr.aym.acsguis.api;

import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.cssengine.CssGuiManager;
import fr.aym.acsguis.cssengine.CssHudHandler;
import fr.aym.acsguis.event.CssReloadEvent;
import fr.aym.acsguis.utils.CssReloadOrigin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;

@SideOnly(Side.CLIENT)
public class ACsGuiApi
{
    public static final String RES_LOC_ID = "acsguis";
    public static final String VERSION = "1.0.0";
    public static final Logger log = LogManager.getLogger("ACsGuis");

    private static final CssGuiManager manager = new CssGuiManager();
    private static boolean INITIALIZED;

    @SideOnly(Side.CLIENT)
    public static void init() {
        if(INITIALIZED) {
            log.warn("ACsGuis was already initialized by another mod, if you added style sheets, call the reload function manually to load them");
            return;
        }
        log.info("Initializing ACsGuis API by Aym', version "+VERSION);
        INITIALIZED = true;
        MinecraftForge.EVENT_BUS.register(ACsGuiApi.getHudHandler()); //FIXME FICSIT IN API
        ((SimpleReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new CssGuiManager());
    }

    /**
     * Register a css style sheet to (re)load when resources packs are loaded <br>
     *     Register all the sheets that you are using here, before api initialization
     *
     * @param location The style sheet to load
     */
    public static void registerStyleSheetToPreload(ResourceLocation location) {
        manager.registerStyleSheetToPreload(location);
    }

    /**
     * Loads a GuiFrame in another thread, then shows it <br>
     *     Note : the css fonts are loaded in the client thread (needs open gl)
     *
     * @param guiName The gui name, used for log messages
     * @param guiInstance A function returning the gui, called by the external thread
     */
    public static void asyncLoadThenShowGui(String guiName, Callable<GuiFrame> guiInstance) {
        manager.asyncLoadThenShowGui(guiName, guiInstance);
    }

    /**
     * Loads a GuiFrame in another thread, then shows it on the HUD <br>
     *     Note : the css fonts are loaded in the client thread (needs open gl)
     *
     * @param guiName The gui name, used for log messages
     * @param guiInstance A function returning the gui, called by the external thread
     * @see CssHudHandler
     */
    public static void asyncLoadThenShowHudGui(String guiName, Callable<GuiFrame> guiInstance) {
        manager.asyncLoadThenShowHudGui(guiName, guiInstance);
    }

    public static void reloadCssStyles(@Nullable GuiFrame frame) {
        CssReloadEvent.Pre event = new CssReloadEvent.Pre(frame != null ? new CssReloadOrigin.HotCssReloadOrigin(manager, frame) : new CssReloadOrigin(manager, false));
        if(MinecraftForge.EVENT_BUS.post(event)) return;
        event.getReloadOrigin().loadStyles();
        event.getReloadOrigin().loadFonts();
        event.getReloadOrigin().postLoad();
    }

    public static CssHudHandler getHudHandler() {
        return manager.getHud();
    }
}
