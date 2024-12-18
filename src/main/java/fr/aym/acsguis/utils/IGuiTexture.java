package fr.aym.acsguis.utils;

/**
 * A texture for ACsGuis
 *
 * @see GuiTextureSprite
 */
public interface IGuiTexture
{
    /**
     * @return Texture sprite width
     */
    int getTextureWidth();

    /**
     * @return Texture sprite height
     */
    int getTextureHeight();

    /**
     * Draws this texture
     *
     * @param x Screen x coord
     * @param y Screen y coord
     * @param width The total width to draw on screen
     * @param height The total height to draw on screen
     */
    default void drawSprite(float x, float y, float width, float height) {
        drawSprite(x, y, width, height, width, height);
    }

    /**
     * Draws this texture, multiple times if spriteWidth < width or spriteHeight < height
     *
     * @param x Screen x coord
     * @param y Screen y coord
     * @param spriteWidth Width of a sprite on the screen
     * @param spriteHeight Height of a sprite on the screen
     * @param width The total width to draw on screen
     * @param height The total height to draw on screen
     */
    default void drawSprite(float x, float y, float spriteWidth, float spriteHeight, float width, float height) {
        drawSprite(x, y, spriteWidth, spriteHeight, 0, 0, getTextureWidth(), getTextureHeight(), width, height);
    }

    void drawSprite(float x, float y, float spriteWidth, float spriteHeight, int uOffset, int vOffset, int textureWidth, int textureHeight, float width, float height);
}
