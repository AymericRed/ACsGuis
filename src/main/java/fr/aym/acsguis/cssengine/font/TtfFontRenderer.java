package fr.aym.acsguis.cssengine.font;

import fr.aym.acsguis.api.GuiAPIClientHelper;
import fr.aym.acsguis.cssengine.parsing.ACsGuisCssParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.font.effects.Effect;
import org.newdawn.slick.font.effects.OutlineEffect;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Renders strings with ttf fonts
 */
public class TtfFontRenderer implements ICssFont {
    /**
     * Array of RGB triplets defining the 16 standard chat colors followed by 16 darker version of the same colors for
     * drop shadows.
     */
    public final int[] colorCode = new int[32];
    /**
     * Set if the "n" style (underlined) is active in currently rendering string
     */
    private boolean underlineStyle;
    /**
     * Set if the "m" style (strikethrough) is active in currently rendering string
     */
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
    public void load(IResourceManager resourceManager) {
        GlStateManager.disableTexture2D();
        try {
            Font UIFont1 = Font.createFont(Font.TRUETYPE_FONT, ACsGuisCssParser.getResource(location));
            UIFont1 = UIFont1.deriveFont(Font.PLAIN, style.getSize()); //You can change "PLAIN" to "BOLD" or "ITALIC"... and 16.f is the size of your font

            uniFont = new org.newdawn.slick.UnicodeFont(UIFont1, UIFont1.getSize(), style.isBold(), style.isItalic());
            uniFont.addAsciiGlyphs();
            uniFont.getEffects().add(new ColorEffect(java.awt.Color.white)); //You can change your color here, but you can also change it in the render{ ... }
            //uniFont.addAsciiGlyphs();
            uniFont.loadGlyphs();

            stylizedUniFont = new org.newdawn.slick.UnicodeFont(UIFont1, UIFont1.getSize(), style.isBold(), style.isItalic());
            stylizedUniFont.addAsciiGlyphs();
            //ShadowEffect
            OutlineEffect e = new OutlineEffect(1, java.awt.Color.white);
            e.setColor(java.awt.Color.white);
            //uniFontStroke.getEffects().add(e); //You can change your color here, but you can also change it in the render{ ... }
            stylizedUniFont.getEffects().add(new ColorEffect(java.awt.Color.white)); //You can change your color here, but you can also change it in the render{ ... }
            stylizedUniFont.loadGlyphs();

        } catch (Exception e) {
            throw new RuntimeException("Cannot load css file " + location, e);
        }
        for (int i = 0; i < 32; ++i) {
            int j = (i >> 3 & 1) * 85;
            int k = (i >> 2 & 1) * 170 + j;
            int l = (i >> 1 & 1) * 170 + j;
            int i1 = (i >> 0 & 1) * 170 + j;

            if (i == 6) {
                k += 85;
            }

            if (Minecraft.getMinecraft().gameSettings.anaglyph) {
                int j1 = (k * 30 + l * 59 + i1 * 11) / 100;
                int k1 = (k * 30 + l * 70) / 100;
                int l1 = (k * 30 + i1 * 70) / 100;
                k = j1;
                l = k1;
                i1 = l1;
            }

            if (i >= 16) {
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
        if (stylizedUniFont == null)
            return; //is loading
        for (Effect e : effectList) {
            if (!stylizedUniFont.getEffects().contains(e)) {
                appliedEffects.add(e);
                stylizedUniFont.getEffects().add(e);
                break;
            }
        }
    }

    @Override
    public void popEffects() {
        if (stylizedUniFont == null)
            return; //is loading
        stylizedUniFont.getEffects().removeAll(appliedEffects);
        appliedEffects.clear();
        this.strikethroughStyle = false;
        this.underlineStyle = false; //TODO COLOR
    }

    @Override
    public void draw(float x, float y, String text, int colorint) {
        if (uniFont == null)
            return; //is loading
        Color defaultColor = new Color(colorint);
        Color color = defaultColor;
        int line = 0;
        int u = 0;//text.split("%%").length*(uniFont.getHeight(text)+2);
        text = text.replace("\t", "   ");
        GL11.glPushMatrix();
        for (String s : text.split("\n")) {
            if (s.contains("§")) {
                int xOffset = 0;
                for (String part : s.split("§")) {
                    if (part.trim().isEmpty())
                        continue;
                    //Mc formatting
                    int i1 = "0123456789abcdefklmnor".indexOf(part.substring(0, 1).toLowerCase(Locale.ROOT).charAt(0));
                    if (i1 < 16) {
                        //this.randomStyle = false;
                        //this.boldStyle = false;
                        this.strikethroughStyle = false;
                        this.underlineStyle = false;
                        //this.italicStyle = false;

                        if (i1 < 0 || i1 > 15) {
                            i1 = 15;
                        }

                        /*if (shadow)
                        {
                            i1 += 16;
                        }*/ //TODO FIX

                        int j1 = this.colorCode[i1];
                        color = new Color(j1);
                    } else if (i1 == 16) {
                        // just ignore this.randomStyle = true;
                    } else if (i1 == 17) {
                        // ignored : use proper css definition this.boldStyle = true;
                    } else if (i1 == 18) {
                        this.strikethroughStyle = true;
                    } else if (i1 == 19) {
                        this.underlineStyle = true;
                    } else if (i1 == 20) {
                        // ignored : use proper css definition this.italicStyle = true;
                    } else if (i1 == 21) {
                        //this.randomStyle = false;
                        //this.boldStyle = false;
                        this.strikethroughStyle = false;
                        this.underlineStyle = false;
                        //this.italicStyle = false;
                        color = defaultColor;
                    }
                    part = part.substring(1);
                    if (!appliedEffects.isEmpty()) {
                        stylizedUniFont.drawString(x + xOffset, y - u + (uniFont.getHeight(s) + 2) * line, part); //x, y, string to draw, color
                        uniFont.drawString(x + xOffset, y - u + (uniFont.getHeight(s) + 2) * line, part, color); //x, y, string to draw, color
                    } else
                        uniFont.drawString(x + xOffset, y - u + (uniFont.getHeight(s) + 2) * line, part, color); //x, y, string to draw, color
                    drawAdditionnalEffects(x + xOffset, y - u + (uniFont.getHeight(s) + 2) * line, getWidth(part), getHeight(s));
                    xOffset += getWidth(part);
                }
            } else {
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
        GL11.glColor4f(1, 1, 1, 1); //Clear color, don't use GlStateManager which keeps the old value in cache
        GlStateManager.bindTexture(0); //Make mc think another texture is bind (which is true)
    }

    protected void drawAdditionnalEffects(float posX, float posY, float length, float height) {
        if (this.strikethroughStyle) {
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

        if (this.underlineStyle) {
            Tessellator tessellator1 = Tessellator.getInstance();
            BufferBuilder bufferbuilder1 = tessellator1.getBuffer();
            GlStateManager.disableTexture2D();
            bufferbuilder1.begin(7, DefaultVertexFormats.POSITION);
            int l = this.underlineStyle ? -1 : 0;
            bufferbuilder1.pos(posX + (float) l, posY + height, 0.0D).endVertex();
            bufferbuilder1.pos(posX + length, posY + height, 0.0D).endVertex();
            bufferbuilder1.pos(posX + length, posY + height - 1.0F, 0.0D).endVertex();
            bufferbuilder1.pos(posX + (float) l, posY + height - 1.0F, 0.0D).endVertex();
            tessellator1.draw();
            GlStateManager.enableTexture2D();
        }
    }

    @Override
    public int getHeight(String text) {
        if (uniFont == null)
            return 9; //is loading
        return uniFont.getHeight(text);
    }

    @Override
    public int getWidth(String text) {
        if (uniFont == null)
            return ACsGuisCssParser.DEFAULT_FONT.getWidth(text); //is loading
        return uniFont.getWidth(text);
    }

    /**
     * Trims text to fit within lines of a specified maximum width, based on the pixel width of the text when rendered.
     * The text will be split at spaces where possible, and words will not be split unless necessary.
     *
     * @param text          The input text to be split into lines.
     * @param maxWidth      The maximum width (in pixels) for each line.
     * @param maxTextHeight The maximum height of the text. -1 for no limit
     * @return A list of lines, each of which fits within the specified maxWidth.
     */
    @Override
    public List<String> trimTextToWidth(String text, int maxWidth, int maxTextHeight) {
        if (uniFont == null)
            return ACsGuisCssParser.DEFAULT_FONT.trimTextToWidth(text, maxWidth, maxTextHeight); //is loading
        List<String> renderedLines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        int totalRenderedTextHeight = 0;

        // Split the text into words
        String[] words = text.split("\\s+");
        for (String word : words) {
            // Check if adding the word to the current line exceeds the max width
            if (uniFont.getWidth(currentLine + " " + word) <= maxWidth) {
                if (currentLine.length() > 0) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            } else {
                // If the word doesn't fit, add the current line to the rendered lines
                if (currentLine.length() > 0) {
                    renderedLines.add(currentLine.toString());
                    totalRenderedTextHeight += uniFont.getHeight(currentLine.toString());
                    currentLine.setLength(0);
                }
                // Check if we've reached the maximum allowed height
                if (GuiAPIClientHelper.addEllipsisToLastLine(this, maxWidth, maxTextHeight, renderedLines, totalRenderedTextHeight, word)) {
                    return renderedLines;
                }
                // Handle the case where a single word is too long for maxWidth
                if (uniFont.getWidth(word) > maxWidth) {
                    List<String> splitWordLines = splitWordToWidth(word, maxWidth);
                    for (String part : splitWordLines) {
                        if (GuiAPIClientHelper.addEllipsisToLastLine(this, maxWidth, maxTextHeight, renderedLines, totalRenderedTextHeight, part)) {
                            return renderedLines;
                        }
                        renderedLines.add(part);
                        totalRenderedTextHeight += uniFont.getHeight(part);
                    }
                } else {
                    // Start a new line with the current word
                    currentLine.append(word);
                }
            }
        }
        // Add the last line if it has any content
        if (currentLine.length() > 0) {
            renderedLines.add(currentLine.toString());
        }
        return renderedLines;
    }

    /**
     * Splits a word into multiple parts so that each part fits within maxWidth.
     */
    private List<String> splitWordToWidth(String word, int maxWidth) {
        List<String> parts = new ArrayList<>();
        int start = 0;
        while (start < word.length()) {
            int end = findMaxFittingSubstring(word, start, maxWidth);
            parts.add(word.substring(start, end));
            start = end;
        }
        return parts;
    }

    /**
     * Finds the maximum substring of a word starting at `start` that fits within maxWidth, using binary search.
     */
    private int findMaxFittingSubstring(String word, int start, int maxWidth) {
        int low = start;
        int high = word.length();
        int mid;
        while (low < high) {
            mid = (low + high + 1) / 2;
            if (uniFont.getWidth(word.substring(start, mid)) <= maxWidth) {
                low = mid;
            } else {
                high = mid - 1;
            }
        }
        return low;
    }
}
