package fr.aym.acsguis.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class GuiDynamicTexture implements ICssTexture
{
    private final ResourceLocation atlasTexture;
    private int textureWidth;
    private int textureHeight;

    /**
     * @param atlasTexture The texture location
     * @param textureWidth Texture (atlas) width
     * @param textureHeight Texture (atlas) height
     */
    public GuiDynamicTexture(ResourceLocation atlasTexture, int textureWidth, int textureHeight) {
        this.atlasTexture = atlasTexture;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    /**
     * Draws this texture, multiple times if spriteWidth < width or spriteHeight < height
     *
     * @param x Screen x coord
     * @param y Screen y coord
     * @param spriteWidth Width of a sprite on the screen
     * @param spriteHeight Height of a sprite on the screen
     * @param uOffset U offset added to the u coord of this sprite
     * @param vOffset V offset added to the v coord of this sprite
     * @param textureWidth Sprite width on texture
     * @param textureHeight Sprite height on texture
     * @param width The total width to draw on screen
     * @param height The total height to draw on screen
     */
    public void drawSprite(int x, int y, int spriteWidth, int spriteHeight, int uOffset, int vOffset, int textureWidth, int textureHeight, int width, int height)
    {
        Minecraft.getMinecraft().renderEngine.bindTexture(atlasTexture);
        double nRepeatY = height / (double) spriteHeight;
        int j = 0;

        do {
            int i = 0;
            double nRepeatX = width / (double) spriteWidth;
            double ySize = MathHelper.clamp(nRepeatY, 0, 1);

            do {
                double xSize = MathHelper.clamp(nRepeatX, 0, 1);
                drawTexturedModalRect(x + i * spriteWidth, y + j * spriteHeight, (int) Math.ceil(spriteWidth * xSize), (int) Math.ceil(spriteHeight * ySize), uOffset, vOffset, (int) Math.ceil(textureWidth * xSize), (int) Math.ceil(textureHeight * ySize));
                i++;
                nRepeatX -= 1;

            } while(nRepeatX > 0);

            j++;
            nRepeatY -= 1;

        } while(nRepeatY > 0);
    }

    private void drawTexturedModalRect(int x, int y, int spriteWidth, int spriteHeight, int uOffset, int vOffset, int textureWidth, int textureHeight)
    {
        Gui.drawScaledCustomSizeModalRect(x, y, uOffset, vOffset, textureWidth, textureHeight, spriteWidth, spriteHeight, textureWidth, textureHeight);
    }

    /**
     * @return Texture sprite width
     */
    public int getTextureWidth() {
        return textureWidth;
    }

    /**
     * @return Texture sprite height
     */
    public int getTextureHeight() {
        return textureHeight;
    }
}
