package fr.aym.acsguis.component.layout;

import fr.aym.acsguis.utils.ACsScaledResolution;

/**
 * Computes x and y scales to adjust guis size to screen size and minecraft's scale
 *
 * @see Identity
 * @see AdjustFullScreen
 */
public interface GuiScaler
{
    //TODO DOC
    default float[] getScale(ACsScaledResolution res, int mcWidth, int scaledWidth, float guiWidth, int mcHeight, int scaledHeight, float guiHeight) {
        return new float[] {getScaleX(res, mcWidth, scaledWidth, guiWidth), getScaleY(res, mcHeight, scaledHeight, guiHeight)};
    }

    /**
     * @param res Game resolution
     * @param mcWidth Total screen width (mc's display width)
     * @param scaledWidth Scaled screen width
     * @param guiWidth The gui width, as computed from css data
     * @return The scale on X axis
     */
    float getScaleX(ACsScaledResolution res, int mcWidth, int scaledWidth, float guiWidth);

    /**
     * @param res Game resolution
     * @param mcHeight Total screen height (mc's display height)
     * @param scaledHeight Scaled screen height
     * @param guiHeight The gui height, as computed from css data
     * @return The scale on Y axis
     */
    float getScaleY(ACsScaledResolution res, int mcHeight, int scaledHeight, float guiHeight);

    default void onApplyScale(float scaleX, float scaleY) {
    }

    default void onRemoveScale(float scaleX, float scaleY) {
    }

    /**
     * Returns an 1:1 scale
     */
    class Identity implements GuiScaler
    {
        @Override
        public float getScaleX(ACsScaledResolution res, int mcWidth, int scaledWidth, float guiWidth) {
            return 1;
        }

        @Override
        public float getScaleY(ACsScaledResolution res, int mcHeight, int scaledHeight, float guiHeight) {
            return 1;
        }
    }

    /**
     * Adjusts the scale to let the gui take the entire screen
     */
    class AdjustFullScreen implements GuiScaler
    {
        @Override
        public float getScaleX(ACsScaledResolution res, int mcWidth, int scaledWidth, float guiWidth) {
            return ((float)scaledWidth)/guiWidth;
        }

        @Override
        public float getScaleY(ACsScaledResolution res, int mcHeight, int scaledHeight, float guiHeight) {
            return ((float)scaledHeight)/guiHeight;
        }
    }

    /**
     * Adjusts the scale to let the gui take the max desired relative size
     */
    class AdjustToScreenSize implements GuiScaler
    {
        private final boolean keepProportions;
        private final float maxScreenSizeWidth, maxScreenSizeHeight;

        public AdjustToScreenSize(boolean keepProportions, float maxScreenSizeWidth, float maxScreenSizeHeight) {
            this.keepProportions = keepProportions;
            this.maxScreenSizeWidth = maxScreenSizeWidth;
            this.maxScreenSizeHeight = maxScreenSizeHeight;
        }

        @Override
        public float[] getScale(ACsScaledResolution res, int mcWidth, int scaledWidth, float guiWidth, int mcHeight, int scaledHeight, float guiHeight) {
            float[] scales = GuiScaler.super.getScale(res, mcWidth, scaledWidth, guiWidth, mcHeight, scaledHeight, guiHeight);
            if(keepProportions) {
                if (scales[0] < scales[1]) {
                    scales[1] = scales[0];
                } else if (scales[1] < scales[0]) {
                    scales[0] = scales[1];
                }
            }
            return scales;
        }

        @Override
        public float getScaleX(ACsScaledResolution res, int mcWidth, int scaledWidth, float guiWidth) {
            if(guiWidth > scaledWidth*maxScreenSizeWidth)
                return ((float)scaledWidth * maxScreenSizeWidth)/guiWidth;
            return 1;
        }

        @Override
        public float getScaleY(ACsScaledResolution res, int mcHeight, int scaledHeight, float guiHeight) {
            if(guiHeight > scaledHeight*maxScreenSizeHeight)
                return ((float)scaledHeight * maxScreenSizeHeight)/guiHeight;
            return 1;
        }
    }
}
