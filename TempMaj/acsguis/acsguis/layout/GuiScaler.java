package fr.aym.acsguis.component.layout;

import net.minecraft.client.gui.ScaledResolution;

/**
 * Computes x and y scales to adjust guis size to screen size and minecraft's scale
 *
 * @see Identity
 * @see AdjustFullScreen
 */
public interface GuiScaler
{
    /**
     * @param res Game resolution
     * @param mcWidth Total screen width (mc's display width)
     * @param scaledWidth Scaled screen width
     * @param guiWidth The gui width, as computed from css data
     * @return The scale on X axis
     */
    float getScaleX(ScaledResolution res, int mcWidth, int scaledWidth, int guiWidth);
    /**
     * @param res Game resolution
     * @param mcHeight Total screen height (mc's display height)
     * @param scaledHeight Scaled screen height
     * @param guiHeight The gui height, as computed from css data
     * @return The scale on Y axis
     */
    float getScaleY(ScaledResolution res, int mcHeight, int scaledHeight, int guiHeight);

    /**
     * Returns an 1:1 scale
     */
    class Identity implements GuiScaler
    {
        @Override
        public float getScaleX(ScaledResolution res, int mcWidth, int scaledWidth, int guiWidth) {
            return 1;
        }

        @Override
        public float getScaleY(ScaledResolution res, int mcHeight, int scaledHeight, int guiHeight) {
            return 1;
        }
    }

    /**
     * Adjusts the scale to let the gui take the entire screen
     */
    class AdjustFullScreen implements GuiScaler
    {
        @Override
        public float getScaleX(ScaledResolution res, int mcWidth, int scaledWidth, int guiWidth) {
            return ((float)scaledWidth)/guiWidth;
        }

        @Override
        public float getScaleY(ScaledResolution res, int mcHeight, int scaledHeight, int guiHeight) {
            return ((float)scaledHeight)/guiHeight;
        }
    }

    /**
     * Adjusts the scale to let the gui take the entire screen
     */
    class AdjustToScreenSize implements GuiScaler
    {
        private final float maxScreenSizeWidth, maxScreenSizeHeight;

        public AdjustToScreenSize(float maxScreenSizeWidth, float maxScreenSizeHeight) {
            this.maxScreenSizeWidth = maxScreenSizeWidth;
            this.maxScreenSizeHeight = maxScreenSizeHeight;
        }

        @Override
        public float getScaleX(ScaledResolution res, int mcWidth, int scaledWidth, int guiWidth) {
            if(guiWidth > scaledWidth*maxScreenSizeWidth)
                return ((float)scaledWidth * maxScreenSizeWidth)/guiWidth;
            return 1;
        }

        @Override
        public float getScaleY(ScaledResolution res, int mcHeight, int scaledHeight, int guiHeight) {
            if(guiHeight > scaledHeight*maxScreenSizeHeight)
                return ((float)scaledHeight * maxScreenSizeHeight)/guiHeight;
            return 1;
        }
    }
}
