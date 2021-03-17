package fr.aym.acsguis.cssengine.font;

import fr.aym.acsguis.cssengine.parsing.DnxCssParser;
import net.minecraft.util.ResourceLocation;
import org.newdawn.slick.font.effects.Effect;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Provides helper methods to draw fonts
 */
public class CssFontHelper
{
    private static ICssFont drawingFont;

    /**
     * Loads the given font, and loads effects into it <br>
     *     popDrawing should be called before calling this again
     *
     * @param font The font to bind
     * @param effectList The effects to load, nullable
     */
    public static void pushDrawing(ResourceLocation font, @Nullable Collection<Effect> effectList) {
        if(drawingFont != null)
            throw new IllegalStateException("Font manager is already drawing !");
        drawingFont = DnxCssParser.getFont(font);
        if(effectList != null)
            drawingFont.pushEffects(effectList);
    }

    /**
     * Draw the given text this the currently bind font <br>
     *      pushDrawing should have been called before this
     *
     * @param x Screen x pos
     * @param y Screen y pos
     * @param text The text to draw
     * @param color Color of the text to draw
     * @throws IllegalStateException If no font is bind
     */
    public static void draw(float x, float y, String text, int color) {
        if(drawingFont == null)
            throw new IllegalStateException("Not drawing fonts");
        drawingFont.draw(x, y, text, color);
    }

    /**
     * Unloads the loaded font and resets effects <br>
     *     pushDrawing should have been called before this
     */
    public static void popDrawing() {
        if(drawingFont == null)
            throw new IllegalStateException("Not drawing fonts");
        drawingFont.popEffects();
        drawingFont = null;
    }

    /**
     * Loads the given font (as pushDrawing), draws the text (as draw) then unloads the font (as popDrawing) <br>
     *     This method is heavier than the normal draw, use it only if you draw one thing with this font
     *
     * @param font The font to use
     * @param effectList The effects to load, nullable
     */
    public static void drawDirect(ResourceLocation font, float x, float y, String text, int color, @Nullable Collection<Effect> effectList) {
        if(drawingFont != null)
            throw new IllegalStateException("Font manager is already drawing !");
        drawingFont = DnxCssParser.getFont(font);
        if(effectList != null)
            drawingFont.pushEffects(effectList);
        drawingFont.draw(x, y, text, color);
        drawingFont.popEffects();
        drawingFont = null;
    }

    /**
     * Note : pushDrawing should have been called before this
     *
     * @return The height of the given text, with the currently bind font
     */
    public static int getFontHeight(String text) {
        if(drawingFont == null)
            throw new IllegalStateException("Not drawing fonts");
        return drawingFont.getHeight(text);
    }

    /**
     * @return The currently bind font, or null
     */
    @Nullable
    public static ICssFont getBoundFont() {
        return drawingFont;
    }
}
