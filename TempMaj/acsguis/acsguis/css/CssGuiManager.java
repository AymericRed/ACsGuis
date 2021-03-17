package fr.aym.acsguis.cssengine;

import fr.aym.acsguis.CssHudHandler;
import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.cssengine.parsing.DnxCssParser;
import fr.aym.acsguis.gui.GuiCssError;
import fr.aym.acsguis.utils.BaseReloadOrigin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.client.resource.VanillaResourceType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ProgressManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

/**
 * Provides a method to async load a css gui
 */
public class CssGuiManager implements ISelectiveResourceReloadListener
{
    public static final String RES_LOC = "acsguis";
    public static final Logger log = LogManager.getLogger("ACsGuis");

    /** The local tread id */
    private static int id;
    private static final List<ResourceLocation> CSS_SHEETS = new ArrayList<>();

    static
    {
        registerStyleSheetToPreload(DnxCssParser.DEFAULT_STYLE_SHEET);
        MinecraftForge.EVENT_BUS.register(new CssHudHandler());
    }

    /**
     * Register a css style sheet to (re)load when resources packs are loaded <br>
     *     Register all the sheets that you are using here, before (of during) mod initialization
     *
     * @param location The style sheet to load
     */
    public static void registerStyleSheetToPreload(ResourceLocation location) {
        if(!CSS_SHEETS.contains(location))
            CSS_SHEETS.add(location);
    }

    /**
     * Heavy method used for debug or resource pack reload
     * <br><br>
     * NOTE : Prefer implementing needsCssReload in your gui
     *
     * @param origin Handles loading errors
     */
    public static void reloadAllCssSheets(BaseReloadOrigin origin) throws Exception {
        log.info("Loading CSS sheets...");
        origin.handlePreLoad();
        ProgressManager.ProgressBar bar = ProgressManager.push("Load CSS sheets", CSS_SHEETS.size()+1);
        for(ResourceLocation r : CSS_SHEETS) {
            bar.step(r.toString());
            try {
                DnxCssParser.parseCssSheet(r);
            } catch (Exception e) {
                origin.handleException(r, e);
            }
        }
        bar.step("Post loading...");
        origin.handlePostLoad(false);
        ProgressManager.pop(bar);
    }

    /**
     * Loads a GuiFrame in another thread, then shows it <br>
     *     Note : the css fonts are loaded in the client thread (needs open gl)
     *
     * @param guiName The gui name, used for log messages
     * @param guiInstance A function returning the gui, called by the external thread
     */
    public static void asyncLoadThenShowGui(String guiName, Callable<GuiFrame> guiInstance) {
        Minecraft.getMinecraft().ingameGUI.setOverlayMessage("Loading CSS gui "+guiName+"...", true);
        new Thread(() -> {
            try {
                Minecraft.getMinecraft().ingameGUI.setOverlayMessage("Loading CSS gui "+guiName+"...", true);
                GuiFrame gui = guiInstance.call();
                boolean reloadCss = gui.needsCssReload();
                if(reloadCss) {
                    Minecraft.getMinecraft().ingameGUI.setOverlayMessage("Loading css...", true);
                    reloadAllCssSheets(new BaseReloadOrigin.HotReloadOrigin(gui));
                }
                Minecraft.getMinecraft().ingameGUI.setOverlayMessage("Loading CSS gui "+guiName+"...", true);
                Minecraft.getMinecraft().addScheduledTask( () -> {
                    if(reloadCss)
                        DnxCssParser.loadFonts((r, e) -> {
                            log.error("Error while loading css font "+r.toString(), e);
                            //DynamXErrorTracker.addError(ErrorType.CSS, "Css font "+r.toString(), "Error : "+e.getMessage(), 1);
                        });
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
    public static void asyncLoadThenShowHudGui(String guiName, Callable<GuiFrame> guiInstance) {
        Minecraft.getMinecraft().ingameGUI.setOverlayMessage("Loading CSS hud "+guiName+"...", true);
        new Thread(() -> {
            try {
                Minecraft.getMinecraft().ingameGUI.setOverlayMessage("Loading CSS hud "+guiName+"...", true);
                GuiFrame gui = guiInstance.call();
                boolean reloadCss = gui.needsCssReload();
                if(reloadCss) {
                    Minecraft.getMinecraft().ingameGUI.setOverlayMessage("Loading css...", true);
                    reloadAllCssSheets(new BaseReloadOrigin.HotReloadOrigin(gui));
                }
                Minecraft.getMinecraft().ingameGUI.setOverlayMessage("Loading CSS hud "+guiName+"...", true);
                Minecraft.getMinecraft().addScheduledTask( () -> {
                    if(reloadCss)
                        DnxCssParser.loadFonts((r, e) -> {
                            log.error("Error while loading css font "+r.toString(), e);
                            //DynamXErrorTracker.addError(ErrorType.CSS, "Css font "+r.toString(), "Error : "+e.getMessage(), 1);
                        });
                    Minecraft.getMinecraft().ingameGUI.setOverlayMessage("", false);
                    CssHudHandler.setCurrentHUD(gui);
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
            BaseReloadOrigin origin = new BaseReloadOrigin()/* {
                @Override
                public void handlePostLoad() {}
            }*/;
            //ThreadedModLoader.INSTANCE.addTask(ModLoadingSteps.FINISH_LOAD, "css_load", () -> {
                try {
                    reloadAllCssSheets(origin);
                } catch (Exception e) { //This should not happen with our reload origin
                    throw new RuntimeException("Fatal error while loading css sheets", e);
                }
                long time = System.currentTimeMillis();
                while(Minecraft.getMinecraft().fontRenderer == null) { //Don't listen idea, it can be null
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Waited for font renderer during "+(System.currentTimeMillis()-time)+" ms");
            //}, () -> DnxCssParser.loadFonts(origin));
        }
    }
}
