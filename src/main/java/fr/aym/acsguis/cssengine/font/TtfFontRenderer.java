package fr.aym.acsguis.cssengine.font;

import fr.aym.acsguis.cssengine.parsing.ACsGuisCssParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.font.effects.Effect;
import org.newdawn.slick.font.effects.OutlineEffect;
import org.newdawn.slick.Color;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Renders strings with ttf fonts
 */
public class TtfFontRenderer implements ICssFont
{
    /**
     * Array of RGB triplets defining the 16 standard chat colors followed by 16 darker version of the same colors for
     * drop shadows.
     */
    public final int[] colorCode = new int[32];
    /** Set if the "n" style (underlined) is active in currently rendering string */
    private boolean underlineStyle;
    /** Set if the "m" style (strikethrough) is active in currently rendering string */
    private boolean strikethroughStyle;

    private org.newdawn.slick.UnicodeFont uniFont;
    private org.newdawn.slick.UnicodeFont stylizedUniFont;

    private final ResourceLocation location;
    private final CssFontStyle style;
    private final List<Effect> appliedEffects = new ArrayList<>();

    public TtfFontRenderer(ResourceLocation location, CssFontStyle style) {
        this.location = location;
        this.style = style;
    }

    @Override
    public void load(IResourceManager resourceManager)
    {
        GlStateManager.disableTexture2D();
        try{
            Font UIFont1 = Font.createFont(Font.TRUETYPE_FONT, resourceManager.getResource(location).getInputStream());
            UIFont1 = UIFont1.deriveFont(Font.PLAIN, style.getSize()); //You can change "PLAIN" to "BOLD" or "ITALIC"... and 16.f is the size of your font

            uniFont = new org.newdawn.slick.UnicodeFont(UIFont1, UIFont1.getSize(), style.isBold(), style.isItalic());
            uniFont.addAsciiGlyphs();
            uniFont.getEffects().add(new ColorEffect(java.awt.Color.white)); //You can change your color here, but you can also change it in the render{ ... }
            //uniFont.addAsciiGlyphs();
            uniFont.loadGlyphs();

            stylizedUniFont = new org.newdawn.slick.UnicodeFont(UIFont1, UIFont1.getSize(), style.isBold(), style.isItalic());
            stylizedUniFont.addAsciiGlyphs();
            //ShadowEffect
            OutlineEffect e =new OutlineEffect(1, java.awt.Color.white);
            e.setColor(java.awt.Color.white);
            //uniFontStroke.getEffects().add(e); //You can change your color here, but you can also change it in the render{ ... }
            stylizedUniFont.getEffects().add(new ColorEffect(java.awt.Color.white)); //You can change your color here, but you can also change it in the render{ ... }
            stylizedUniFont.loadGlyphs();

        }catch(Exception e){
            throw new RuntimeException("Cannot load css file "+location, e);
        }
        for (int i = 0; i < 32; ++i)
        {
            int j = (i >> 3 & 1) * 85;
            int k = (i >> 2 & 1) * 170 + j;
            int l = (i >> 1 & 1) * 170 + j;
            int i1 = (i >> 0 & 1) * 170 + j;

            if (i == 6)
            {
                k += 85;
            }

            if (Minecraft.getMinecraft().gameSettings.anaglyph)
            {
                int j1 = (k * 30 + l * 59 + i1 * 11) / 100;
                int k1 = (k * 30 + l * 70) / 100;
                int l1 = (k * 30 + i1 * 70) / 100;
                k = j1;
                l = k1;
                i1 = l1;
            }

            if (i >= 16)
            {
                k /= 4;
                l /= 4;
                i1 /= 4;
            }

            colorCode[i] = (k & 255) << 16 | (l & 255) << 8 | i1 & 255;
        }
        //mcFontTextureId = Minecraft.getMinecraft().getTextureManager().getTexture(new ResourceLocation("textures/font/ascii.png")).getGlTextureId();
    }

    @Override
    public void pushEffects(Collection<Effect> effectList) {
        if(stylizedUniFont == null)
            return; //is loading
        for(Effect e : effectList) {
            if(!stylizedUniFont.getEffects().contains(e))
            {
                appliedEffects.add(e);
                stylizedUniFont.getEffects().add(e);
                break;
            }
        }
    }

    @Override
    public void popEffects() {
        if(stylizedUniFont == null)
            return; //is loading
        stylizedUniFont.getEffects().removeAll(appliedEffects);
        appliedEffects.clear();
        this.strikethroughStyle = false;
        this.underlineStyle = false; //TODO COLOR
    }

    @Override
    public void draw(float x, float y, String text, int colorint) {
        if(uniFont == null)
            return; //is loading
        Color defaultColor = new Color(colorint);
        Color color = defaultColor;
        int line=0;
        int u = 0;//text.split("%%").length*(uniFont.getHeight(text)+2);
        text = text.replace("\t", "   ");
        GL11.glPushMatrix();
        for(String s : text.split("\n")) {
            if(s.contains("§"))
            {
                int xOffset = 0;
                for(String part : s.split("§"))
                {
                    if(part.trim().isEmpty())
                        continue;
                    //Mc formatting
                    int i1 = "0123456789abcdefklmnor".indexOf(part.substring(0, 1).toLowerCase(Locale.ROOT).charAt(0));
                    if (i1 < 16)
                    {
                        //this.randomStyle = false;
                        //this.boldStyle = false;
                        this.strikethroughStyle = false;
                        this.underlineStyle = false;
                        //this.italicStyle = false;

                        if (i1 < 0 || i1 > 15)
                        {
                            i1 = 15;
                        }

                        /*if (shadow)
                        {
                            i1 += 16;
                        }*/ //TODO FIX

                        int j1 = this.colorCode[i1];
                        color = new Color(j1);
                    }
                    else if (i1 == 16)
                    {
                        // just ignore this.randomStyle = true;
                    }
                    else if (i1 == 17)
                    {
                        // ignored : use proper css definition this.boldStyle = true;
                    }
                    else if (i1 == 18)
                    {
                        this.strikethroughStyle = true;
                    }
                    else if (i1 == 19)
                    {
                        this.underlineStyle = true;
                    }
                    else if (i1 == 20)
                    {
                        // ignored : use proper css definition this.italicStyle = true;
                    }
                    else if (i1 == 21)
                    {
                        //this.randomStyle = false;
                        //this.boldStyle = false;
                        this.strikethroughStyle = false;
                        this.underlineStyle = false;
                        //this.italicStyle = false;
                        color = defaultColor;
                    }
                    part = part.substring(1);
                    if (!appliedEffects.isEmpty()) {
                        stylizedUniFont.drawString(x+xOffset, y - u + (uniFont.getHeight(s) + 2) * line, part); //x, y, string to draw, color
                        uniFont.drawString(x+xOffset, y - u + (uniFont.getHeight(s) + 2) * line, part, color); //x, y, string to draw, color
                    } else
                        uniFont.drawString(x+xOffset, y - u + (uniFont.getHeight(s) + 2) * line, part, color); //x, y, string to draw, color
                    drawAdditionnalEffects(x+xOffset, y - u + (uniFont.getHeight(s) + 2) * line, getWidth(part), getHeight(s));
                    xOffset += getWidth(part);
                }
            }
            else {
                if (!appliedEffects.isEmpty()) {
                    //stylizedUniFont.drawString(x, y - u + (uniFont.getHeight(s) + 2) * line, s); //x, y, string to draw, color
                    uniFont.drawString(x, y - u + (uniFont.getHeight(s) + 2) * line, s, color); //x, y, string to draw, color
                } else
                    uniFont.drawString(x, y - u + (uniFont.getHeight(s) + 2) * line, s, color); //x, y, string to draw, color
                drawAdditionnalEffects(x, y - u + (uniFont.getHeight(s) + 2) * line, getWidth(s), getHeight(s));
            }
            line++;
        }
        GL11.glPopMatrix();
        GlStateManager.bindTexture(0); //Make mc think another texture is bind (which is true)
        //GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    protected void drawAdditionnalEffects(float posX, float posY, float length, float height)
    {
        if (this.strikethroughStyle)
        {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            GlStateManager.disableTexture2D();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
            bufferbuilder.pos(posX, posY + (height / 2), 0.0D).endVertex();
            bufferbuilder.pos(posX + length, posY + (height / 2), 0.0D).endVertex();
            bufferbuilder.pos(posX + length, posY + (height / 2) - 1.0F, 0.0D).endVertex();
            bufferbuilder.pos(posX, posY + (height / 2) - 1.0F, 0.0D).endVertex();
            tessellator.draw();
            GlStateManager.enableTexture2D();
        }

        if (this.underlineStyle)
        {
            Tessellator tessellator1 = Tessellator.getInstance();
            BufferBuilder bufferbuilder1 = tessellator1.getBuffer();
            GlStateManager.disableTexture2D();
            bufferbuilder1.begin(7, DefaultVertexFormats.POSITION);
            int l = this.underlineStyle ? -1 : 0;
            bufferbuilder1.pos(posX + (float)l, posY + height, 0.0D).endVertex();
            bufferbuilder1.pos(posX + length, posY + height, 0.0D).endVertex();
            bufferbuilder1.pos(posX + length, posY + height - 1.0F, 0.0D).endVertex();
            bufferbuilder1.pos(posX + (float)l, posY + height - 1.0F, 0.0D).endVertex();
            tessellator1.draw();
            GlStateManager.enableTexture2D();
        }
    }

    @Override
    public int getHeight(String text) {
        if(uniFont == null)
            return 9; //is loading
        return uniFont.getHeight(text);
    }

    @Override
    public int getWidth(String text) {
        if(uniFont == null)
            return ACsGuisCssParser.DEFAULT_FONT.getWidth(text); //is loading
        return uniFont.getWidth(text);
    }
}
