package fr.aym.acsguis.utils;

public interface ICssTexture
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
    default void drawSprite(int x, int y, int width, int height) {
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
    default void drawSprite(int x, int y, int spriteWidth, int spriteHeight, int width, int height) {
        drawSprite(x, y, spriteWidth, spriteHeight, 0, 0, getTextureWidth(), getTextureHeight(), width, height);
    }

    void drawSprite(int screenX, int i, int borderSize, int borderSize1, int i1, int i2, int borderSize2, int borderSize3, int borderSize4, int borderSize5);
}
