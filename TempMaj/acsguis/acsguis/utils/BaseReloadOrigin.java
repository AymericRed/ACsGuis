package fr.aym.acsguis.utils;

import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.cssengine.CssGuiManager;
import fr.aym.acsguis.cssengine.font.ICssFont;
import fr.aym.acsguis.cssengine.parsing.DnxCssParser;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class BaseReloadOrigin implements ICssFont.FontReloadOrigin
{
    public void handleException(ResourceLocation r, Exception e) {
        CssGuiManager.log.error("Error while loading css sheet "+r.toString(), e);
        //DynamXErrorTracker.addError(ErrorType.CSS, "Css sheet "+r.toString(), "Error : "+e.getMessage(), 1);
    }

    public void handleFontException(ResourceLocation r, Exception e) {
        CssGuiManager.log.error("Error while loading css font "+r.toString(), e);
        //DynamXErrorTracker.addError(ErrorType.CSS, "Css font "+r.toString(), "Error : "+e.getMessage(), 1);
    }

    public void handlePreLoad() {
        //DynamXErrorTracker.clear(ErrorType.CSS);
        DnxCssParser.clearFonts();
    }

    public void handlePostLoad(boolean mcThread) throws Exception {
        if(mcThread)
            DnxCssParser.loadFonts(this);
        /*if(Minecraft.getMinecraft().player != null)
        {
            if(DynamXErrorTracker.hasErrors(ErrorType.CSS)) {
                List<String> to = new ArrayList<>();
                to.add(TextFormatting.DARK_RED+"Erreur(s) de chargement du CSS :");
                to.add("");
                DynamXErrorTracker.getColoredErrors(ErrorType.CSS, to);
                for(String s : to)
                {
                    Minecraft.getMinecraft().player.sendMessage(new TextComponentString(s));
                }
            }
        }*/
    }

    public static class HotReloadOrigin extends BaseReloadOrigin
    {
        protected final List<ResourceLocation> sheets;
        protected Exception throwE;

        public HotReloadOrigin(GuiFrame reloader) {
            this.sheets = reloader.getCssStyles();
        }

        @Override
        public void handleException(ResourceLocation r, Exception e) {
            super.handleException(r, e);
            if(throwE == null && (sheets.contains(r) || DnxCssParser.DEFAULT_STYLE_SHEET.equals(r)))
                throwE = e;
        }

        @Override
        public void handlePostLoad(boolean mcThread) throws Exception {
            //DO NOT LOAD FONT, IT IS NOT THE RIGHT THREAD
            /*if(Minecraft.getMinecraft().player != null)
            {
                if(DynamXErrorTracker.hasErrors(ErrorType.CSS)) {
                    List<String> to = new ArrayList<>();
                    to.add(TextFormatting.DARK_RED+"Erreur(s) de chargement du CSS :");
                    to.add("");
                    DynamXErrorTracker.getColoredErrors(ErrorType.CSS, to);
                    for(String s : to)
                    {
                        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(s));
                    }
                }
            }*/
            if(throwE != null)
                throw throwE;
        }
    }
}
