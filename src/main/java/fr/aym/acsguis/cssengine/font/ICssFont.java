package fr.aym.acsguis.cssengine.font;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.newdawn.slick.font.effects.Effect;

import java.util.Collection;
import java.util.List;

/**
 * A css font, supporting special effects
 *
 * @see Effect
 */
public interface ICssFont {
    /**
     * Loads this font, called on each resource pack reload
     */
    void load(IResourceManager resourceManager);

    /**
     * Loads the given effects into this font
     */
    void pushEffects(Collection<Effect> effectList);

    /**
     * Draw a text
     *
     * @param x     Screen x pos
     * @param y     Screen y pos
     * @param text  The text to draw
     * @param color Color of the text to draw
     */
    void draw(float x, float y, String text, int color);

    /**
     * Unloads all special effects
     */
    void popEffects();

    /**
     * @return The height of the given text with this font
     */
    int getHeight(String text);

    /**
     * @return The width of the given text with this font
     */
    int getWidth(String text);

    /**
     * Trim the text to the given width, without cutting words unless the word is larger than a line.
     *
     * @param text          The text to trim.
     * @param maxWidth      The maximum line's width.
     * @param maxTextHeight The maximum height of the text. -1 for no limit
     * @return Return the list of the lines trimmed to the given width.
     */
    List<String> trimTextToWidth(String text, int maxWidth, int maxTextHeight);

    /**
     * Listener of font loading errors
     */
    interface FontReloadOrigin {
        void handleFontException(ResourceLocation r, Exception e);
    }
}
