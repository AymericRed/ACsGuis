package fr.aym.acsguis.api;

import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.cssengine.font.ICssFont;
import fr.aym.acsguis.cssengine.parsing.ACsGuisCssParser;
import fr.aym.acsguis.utils.CircleBackground;
import fr.aym.acsguis.utils.GuiConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Reden for the gui api, base of the acsguis api by Aym'
 */
@SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
public class GuiAPIClientHelper {
    public static final Minecraft mc = Minecraft.getMinecraft();

    public static void drawItemStack(ItemStack stack, int x, int y) {
        drawItemStack(stack, x, y, 1, true);
    }

    /**
     * Draw the given itemStack at the (x; y) position.
     *
     * @param stack       The itemStack to render.
     * @param x           x render position.
     * @param y           y render position.
     * @param scale       The scale of the render (1 by default).
     * @param drawAltText The text to be rendered next to the itemStack (stackSize).
     */
    public static void drawItemStack(ItemStack stack, int x, int y, float scale, boolean drawAltText) {
        String altText;

        int stackSize = stack.getCount();

        if (stackSize < 0) {
            altText = TextFormatting.RED + String.valueOf(stackSize);
        } else if (stackSize == 0 || stackSize > stack.getMaxStackSize()) {
            altText = TextFormatting.YELLOW + String.valueOf(stackSize);
        } else if (stackSize == 1) {
            altText = "";
        } else {
            altText = String.valueOf(stackSize);
        }

        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, scale);
        RenderHelper.enableGUIStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
        if (drawAltText) {
            mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, stack, x, y, altText);
        }
        GlStateManager.enableBlend();
        RenderHelper.disableStandardItemLighting();
        GL11.glPopMatrix();
    }

    /**
     * @param text                    The text to align.
     * @param parentWidth             The parent width. Note that the x value will be relative to this parent.
     * @param horizontalTextAlignment The text horizontal alignment. {@link GuiConstants.HORIZONTAL_TEXT_ALIGNMENT}
     * @param scale                   The render scale, used for different font sizes
     * @return Return the x value of the text depending on the horizontal alignment given. {@link GuiConstants.HORIZONTAL_TEXT_ALIGNMENT}
     */
    public static float getRelativeTextX(String text, float parentWidth, GuiConstants.HORIZONTAL_TEXT_ALIGNMENT horizontalTextAlignment, ICssFont font, float scale) {
        switch (horizontalTextAlignment) {
            case CENTER:
                return (parentWidth - font.getWidth(text) * scale) / 2;
            case RIGHT:
                return parentWidth - font.getWidth(text) * scale;
            default:
                return 0;
        }
    }

    /**
     * @param lineIndex             The text's line index.
     * @param maxLines              The number of lines in the text.
     * @param parentHeight          The parent height. Note that the y value will be relative to this parent.
     * @param verticalTextAlignment The text vertical alignment. {@link GuiConstants.VERTICAL_TEXT_ALIGNMENT}
     * @param fontSize              The font height
     * @return Return the x value of the text depending on the horizontal alignment given. {@link GuiConstants.VERTICAL_TEXT_ALIGNMENT}
     */
    public static float getRelativeTextY(int lineIndex, int maxLines, float parentHeight, GuiConstants.VERTICAL_TEXT_ALIGNMENT verticalTextAlignment, float fontSize) {
        switch (verticalTextAlignment) {
            case CENTER:
                return (parentHeight - maxLines * fontSize) / 2 + lineIndex * fontSize;
            case BOTTOM:
                return parentHeight - maxLines * fontSize + lineIndex * fontSize;
            default:
                return lineIndex * fontSize;
        }
    }

    /**
     * Trim the text to the given width, without cutting words unless the word is larger than a line.
     *
     * @param text          The text to trim.
     * @param maxWidth      The maximum line's width (int pixels)
     * @param maxTextHeight The maximum height of the text. -1 for no limit
     * @return Return the list of the lines trimmed to the given width.
     */
    public static List<String> trimTextToWidth(String text, int maxWidth, int maxTextHeight) {
        List<String> renderedLines = new ArrayList<String>();
        int totalHeight = 0; // Total height of the rendered text (in pixels)
        int fontHeight = Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;

        while (!text.isEmpty()) {
            String rawTrim = Minecraft.getMinecraft().fontRenderer.trimStringToWidth(text, maxWidth);

            String str = text.substring(MathHelper.clamp(rawTrim.length(), 0, text.length()), MathHelper.clamp(rawTrim.length() + 1, 0, text.length()));
            Character lastChar = rawTrim.isEmpty() ? null : rawTrim.charAt(rawTrim.length() - 1);
            Character nextChar = str.isEmpty() ? null : str.charAt(str.length() - 1);

            int lastSpace = rawTrim.lastIndexOf(' ');

            boolean flag = lastChar == null || lastChar == ' ' || nextChar == null || nextChar == ' ' || lastSpace == 0 || lastSpace == -1;

            String line;
            if (rawTrim.contains("\n") && (rawTrim.indexOf("\n") == 0 || rawTrim.charAt(rawTrim.indexOf("\n") - 1) != '\\')) {
                line = rawTrim.substring(0, rawTrim.indexOf("\n") + 1);
            } else {
                if (flag) {
                    line = rawTrim;
                } else {
                    line = rawTrim.substring(0, MathHelper.clamp(lastSpace + 1, 0, rawTrim.length()));
                }
            }

            if (line.isEmpty()) {
                break;
            }
            // Check if we've reached the maximum allowed height
            if (addEllipsisToLastLine(ACsGuisCssParser.DEFAULT_FONT, maxWidth, maxTextHeight, renderedLines, totalHeight, line)) {
                return renderedLines;
            }

            text = text.substring(line.length());
            line = line.replaceAll("\n", "").replaceAll("\t", "    ");
            renderedLines.add(line);
            totalHeight += fontHeight;
        }
        if (renderedLines.isEmpty()) {
            text = text.replaceAll("\n", "").replaceAll("\t", "    ");
            renderedLines.add(text);
        }
        return renderedLines;
    }

    /**
     * @param text The full text to find the character in.
     * @param c    The character to find.
     * @return Return the index of all {@code c} occurrences.
     */
    public static int[] getCharIndexes(String text, char c) {

        int n = text.split(Character.toString(c)).length - 1;
        int lastIndex = 0;

        int[] indexes = new int[n];

        for (int i = 0; i < n; i++) {
            int index = text.indexOf(c, lastIndex + 1);
            indexes[i] = index;
            lastIndex = index;
        }

        return indexes;
    }

    //TODO DOC
    private static float currentScaleX = 1, currentScaleY = 1;

    public static void setCurrentScissorScaling(float scaleX, float scaleY) {
        currentScaleX = scaleX;
        currentScaleY = scaleY;
    }

    public static float getCurrentScaleX() {
        return currentScaleX;
    }

    public static float getCurrentScaleY() {
        return currentScaleY;
    }

    public static void resetScissorScaling() {
        setCurrentScissorScaling(1, 1);
    }

    /**
     * Create rendering boundaries, the elements' parts outside of them will not be rendered.
     */
    public static void glScissor(float x, float y, float width, float height) {
        int f = GuiFrame.resolution.getScaleFactor();
        GL11.glScissor(MathHelper.floor(x * f * currentScaleX), MathHelper.ceil(mc.displayHeight - (y + height) * f * currentScaleY),
                MathHelper.clamp(MathHelper.ceil(width * f * currentScaleX), 0, Integer.MAX_VALUE), MathHelper.clamp(MathHelper.ceil(height * f * currentScaleY), 0, Integer.MAX_VALUE));
    }

    public static void drawBorderedRectangle(float left, float top, float right, float bottom, float borderSize, int backgroundColor, int borderColor, float borderRadius) {
        CircleBackground.renderBorder(borderRadius, left, top, right, bottom, borderSize, borderColor);
        CircleBackground.renderBackground(borderRadius, left + borderSize, top + borderSize,
                right - borderSize, bottom - borderSize, backgroundColor);
    }

    /**
     * Basically just a copy of the vanilla method {@link net.minecraft.client.gui.GuiScreen#drawHoveringText(String, int, int)} 
     */
    public static void drawHoveringText(List<String> textLines, int x, int y) {
        if (!textLines.isEmpty()) {
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int i = 0;

            for (String s : textLines) {
                int j = mc.fontRenderer.getStringWidth(s);

                if (j > i) {
                    i = j;
                }
            }

            int l1 = x + 12;
            int i2 = y - 12;
            int k = 8;

            if (textLines.size() > 1) {
                k += 2 + (textLines.size() - 1) * 10;
            }

            if (l1 + i > GuiFrame.resolution.getScaledWidth()) {
                l1 -= 28 + i;
            }

            if (i2 + k + 6 > GuiFrame.resolution.getScaledHeight()) {
                i2 = GuiFrame.resolution.getScaledHeight() - k - 6;
            }

            if (i2 - 4 < 0) {
                i2 += 20;
            }

            int l = -267386864;
            drawGradientRect(l1 - 3, i2 - 4, l1 + i + 3, i2 - 3, l, l);
            drawGradientRect(l1 - 3, i2 + k + 3, l1 + i + 3, i2 + k + 4, l, l);
            drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 + k + 3, l, l);
            drawGradientRect(l1 - 4, i2 - 3, l1 - 3, i2 + k + 3, l, l);
            drawGradientRect(l1 + i + 3, i2 - 3, l1 + i + 4, i2 + k + 3, l, l);
            int i1 = 1347420415;
            int j1 = (i1 & 16711422) >> 1 | i1 & -16777216;
            drawGradientRect(l1 - 3, i2 - 3 + 1, l1 - 3 + 1, i2 + k + 3 - 1, i1, j1);
            drawGradientRect(l1 + i + 2, i2 - 3 + 1, l1 + i + 3, i2 + k + 3 - 1, i1, j1);
            drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 - 3 + 1, i1, i1);
            drawGradientRect(l1 - 3, i2 + k + 2, l1 + i + 3, i2 + k + 3, j1, j1);

            for (int k1 = 0; k1 < textLines.size(); ++k1) {
                String s1 = textLines.get(k1);
                mc.fontRenderer.drawStringWithShadow(s1, l1, i2, -1);

                if (k1 == 0) {
                    i2 += 2;
                }

                i2 += 10;
            }

            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.disableLighting();
        }
    }

    /**
     * Draw a rectangle with gradient color
     */
    public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) //MODIFIED FOR 1.12.2
    {
        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(right, top, 0).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos(left, top, 0).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos(left, bottom, 0).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos(right, bottom, 0).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    /**
     * Tests if adding one more line would exceed the maxTextHeight.
     * If so, it adds an ellipsis to the current line and returns true.
     *
     * @param font          The font
     * @param maxWidth      The max line width (in pixels)
     * @param maxTextHeight The max text height. -1 for no limit.
     * @param lines         The text lines
     * @param totalHeight   The current height of the text (in pixels)
     * @param word          The word to add on a new line
     * @return True if the word can't be added within maxTextHeight, and a ellipsis was added
     */
    public static boolean addEllipsisToLastLine(ICssFont font, int maxWidth, int maxTextHeight, List<String> lines, int totalHeight, String word) {
        if (maxTextHeight > 0 && totalHeight + font.getHeight(word) > maxTextHeight) {
            String lastLine = lines.isEmpty() ? word : lines.get(lines.size() - 1);
            if (lastLine.length() > 3 && font.getWidth(lastLine + "...") > maxWidth) {
                lastLine = lastLine.substring(0, lastLine.length() - 3) + "...";
            } else {
                lastLine += "...";
            }
            if(lines.isEmpty()) {
                lastLine = lastLine.replaceAll("\n", "").replaceAll("\t", "    ");
                lines.add(lastLine);
            } else {
                lines.set(lines.size() - 1, lastLine);
            }
            return true;
        }
        return false;
    }
}
