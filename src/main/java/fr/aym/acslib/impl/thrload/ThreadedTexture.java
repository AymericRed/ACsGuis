package fr.aym.acslib.impl.thrload;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class ThreadedTexture extends AbstractTexture
{
    private static final Logger LOGGER = LogManager.getLogger();
    protected final ResourceLocation textureLocation;

    private BufferedImage imageData;
    private boolean flag, flag1;

    public ThreadedTexture(ResourceLocation textureResourceLocation)
    {
        this.textureLocation = textureResourceLocation;
    }

    public void loadTexture(IResourceManager resourceManager) throws IOException {
        IResource iresource = null;
        try
        {
            iresource = resourceManager.getResource(this.textureLocation);
            BufferedImage bufferedimage = TextureUtil.readBufferedImage(iresource.getInputStream());
            flag = false;
            flag1 = false;

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
                    LOGGER.warn("Failed reading metadata of: {}", this.textureLocation, runtimeexception);
                }
            }
            imageData = bufferedimage;
        }
        finally
        {
            IOUtils.closeQuietly(iresource);
        }
    }

    @Override
    public int getGlTextureId() {
        if(imageData != null)
            throw new IllegalStateException("Image data of "+textureLocation+" has not been uploaded !");
        return super.getGlTextureId();
    }

    public void uploadTexture(TextureManager resourceManager)
    {
        if(imageData != null) {
            TextureUtil.uploadTextureImageAllocate(super.getGlTextureId(), imageData, flag, flag1); //take care of the super
            imageData = null;
        }
        //else already uploaded (or errored)
    }
}