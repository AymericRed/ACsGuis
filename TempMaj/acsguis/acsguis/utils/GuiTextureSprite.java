package fr.aym.acsguis.utils;

import fr.aym.acsguis.ACsGuiApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.io.IOUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * A texture sprite (part of a texture)
 */
public class GuiTextureSprite extends AbstractTexture implements ICssTexture
{
    private final ResourceLocation atlasTexture;
    private int atlasWidth, atlasHeight;

    private final int textureU, textureV;
    private int textureWidth;
    private int textureHeight;

    /**
     * @param atlasTexture The texture location
     * @param textureU Sprite U
     * @param textureV Sprite V
     * @param textureWidth Sprite width
     * @param textureHeight Sprite height
     */
    public GuiTextureSprite(ResourceLocation atlasTexture, int textureU, int textureV, int textureWidth, int textureHeight) {
        this.atlasTexture = atlasTexture;
        this.textureU = textureU;
        this.textureV = textureV;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    /**
     * Sets u and v to 0, sets width and height to the entire texture
     * @param location The texture location
     */
    public GuiTextureSprite(ResourceLocation location) {
        this(location, 0, 0, 0, 0);
    }

    @Override
    public void loadTexture(IResourceManager resourceManager) throws IOException
    {
        this.deleteGlTexture();
        IResource iresource = null;

        try
        {
            iresource = resourceManager.getResource(this.atlasTexture);
            BufferedImage bufferedimage = TextureUtil.readBufferedImage(iresource.getInputStream());
            boolean flag = false;
            boolean flag1 = false;

            if (iresource.hasMetadata())
            {
                try
                {
                    TextureMetadataSection texturemetadatasection = iresource.getMetadata("texture");

                    if (texturemetadatasection != null)
                    {
                        flag = texturemetadatasection.getTextureBlur();
                        flag1 = texturemetadatasection.getTextureClamp();
                    }
                }
                catch (RuntimeException runtimeexception)
                {
                    ACsGuiApi.log.warn("Failed reading metadata of: {}", this.atlasTexture, runtimeexception);
                }
            }
            TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), bufferedimage, flag, flag1);
            atlasWidth = bufferedimage.getWidth();
            atlasHeight = bufferedimage.getHeight();
            if(textureWidth == 0) //Auto width
                textureWidth = atlasWidth;
            if(textureHeight == 0) //Auto height
                textureHeight = atlasHeight;
        }
        catch (IOException e)
        {
            atlasWidth = textureWidth;
            atlasHeight = textureHeight;
            if(atlasWidth == 0 && atlasHeight == 0)
            {
                atlasWidth = atlasHeight = 100;
            }
            throw e;
        }
        finally
        {
            IOUtils.closeQuietly(iresource);
        }
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
        if(atlasWidth == 0 && atlasHeight == 0) //Not loaded
        {
            Minecraft.getMinecraft().renderEngine.loadTexture(atlasTexture, this);
        }
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
    	Gui.drawScaledCustomSizeModalRect(x, y, textureU+uOffset, textureV+vOffset, textureWidth, textureHeight, spriteWidth, spriteHeight, atlasWidth, atlasHeight);
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
