package fr.aym.acsguis.utils;

import fr.aym.acsguis.api.ACsGuiApi;
import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.cssengine.CssGuisManager;
import fr.aym.acsguis.cssengine.font.ICssFont;
import fr.aym.acsguis.cssengine.parsing.ACsGuisCssParser;
import fr.aym.acsguis.event.CssReloadEvent;
import fr.aym.acslib.services.error_tracking.TrackedErrorLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;

/**
 * Handles css reload and listens all errors
 *
 * TODO CLEAN THE RELOAD SYSTEM
 *
 * @see HotCssReloadOrigin
 */
public class CssReloadOrigin implements ICssFont.FontReloadOrigin
{
    private final CssGuisManager manager;
    private final boolean isHot;

    public CssReloadOrigin(CssGuisManager manager, boolean isHot) {
        this.manager = manager;
        this.isHot = isHot;
    }

    public void handleException(ResourceLocation r, Exception e) {
        ACsGuiApi.log.error("Error while loading css sheet "+r.toString(), e);
        ACsGuiApi.errorTracker.addError(ACsGuiApi.CSS_ERROR_TYPE, "Css sheets", "Css sheet "+r.toString(), e.getCause() instanceof Exception ? (Exception) e.getCause() : e, TrackedErrorLevel.LOW);
    }

    public void handleFontException(ResourceLocation r, Exception e) {
        ACsGuiApi.log.error("Error while loading css font "+r.toString(), e);
        ACsGuiApi.errorTracker.addError(ACsGuiApi.CSS_ERROR_TYPE, "Css fonts", "Css font "+r.toString(), e, TrackedErrorLevel.LOW);
    }

    protected void handlePreLoad() {
        ACsGuiApi.errorTracker.clear(ACsGuiApi.CSS_ERROR_TYPE);
        ACsGuisCssParser.clearFonts();
    }

    public void loadStyles() {
        try {
            handlePreLoad();
            manager.reloadAllCssSheets(this);
            handlePostLoad();
        } catch (Exception e) { //This should not happen with our reload origin
            throw new RuntimeException("Fatal error while loading css sheets", e);
        }
    }

    public void loadFonts() {
        ACsGuisCssParser.loadFonts(this);
    }

    public void postLoad() {
        MinecraftForge.EVENT_BUS.post(new CssReloadEvent.Post(this));
    }

    protected void handlePostLoad() throws Exception {
        if(Minecraft.getMinecraft().player != null)
        {
            if(ACsGuiApi.errorTracker.hasErrors()) {
                /*List<String> to = new ArrayList<>();
                to.add(TextFormatting.DARK_RED+"Erreur(s) de chargement du CSS :");
                to.add("");
                DynamXErrorTracker.getColoredErrors(ErrorType.CSS, to);
                for(String s : to)
                {
                    Minecraft.getMinecraft().player.sendMessage(new TextComponentString(s));
                }*/
                Minecraft.getMinecraft().player.sendMessage(new TextComponentString(TextFormatting.RED+"[ACsGuis] Certaines feuilles de style ont des problèmes, utilisez le menu de debug pour les voir"));
            }
        }
    }

    public boolean isHot() {
        return isHot;
    }

    /**
     * Listens loading errors and allows to show them on the error gui
     */
    public static class HotCssReloadOrigin extends CssReloadOrigin
    {
        protected final List<ResourceLocation> sheets;
        protected Exception throwE;

        public HotCssReloadOrigin(CssGuisManager manager, GuiFrame reloader) {
            super(manager, true);
            this.sheets = reloader.getCssStyles();
        }

        @Override
        public void handleException(ResourceLocation r, Exception e) {
            super.handleException(r, e);
            if(throwE == null && (sheets.contains(r) || ACsGuisCssParser.DEFAULT_STYLE_SHEET.equals(r)))
                throwE = e;
        }

        @Override
        protected void handlePostLoad() throws Exception {
            super.handlePostLoad();
            if(throwE != null)
                throw throwE;
        }
    }
}
