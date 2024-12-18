package fr.aym.acsguis.cssengine.positionning;

import fr.aym.acsguis.component.style.positionning.ReadableSize;
import fr.aym.acsguis.cssengine.parsing.core.objects.CssValue;
import fr.aym.acsguis.utils.GuiConstants;

/**
 * An 1D size, absolute or relative, with min and max values
 */
public class Size implements ReadableSize {
    private final SizeValue value = new SizeValue(1f, GuiConstants.ENUM_SIZE.RELATIVE);
    private final SizeValue maxValue = new SizeValue(-1, GuiConstants.ENUM_SIZE.ABSOLUTE), minValue = new SizeValue(-1, GuiConstants.ENUM_SIZE.ABSOLUTE);
    private boolean dirty;

    /**
     * Computes the value of this 1D size, respecting min and max sizes, and depending on the parent size
     *
     * @param screenWidth  The screen width
     * @param screenHeight The screen height
     * @param parentSize   The size of the parent, in the same dimension (width or height)
     * @return The real value
     */
    @Override
    public float computeValue(int screenWidth, int screenHeight, float parentSize) {
        float cVal = value.computeValue(screenWidth, screenHeight, parentSize);
        float min = minValue.computeValue(screenWidth, screenHeight, parentSize);
        float max = maxValue.computeValue(screenWidth, screenHeight, parentSize);
        if (min != -1)
            cVal = Math.max(min, cVal);
        if (max != -1)
            cVal = Math.min(max, cVal);
        setDirty(false);
        return cVal;
    }

    /**
     * @return The raw value (absolute or relative)
     */
    @Override
    public float getRawValue() {
        return value.getRawValue();
    }

    /**
     * Sets this size to an absolute size containing value
     *
     * @param value a position in pixels (int)
     */
    public void setAbsolute(float value) {
        this.value.setAbsolute(value);
        setDirty(true);
    }

    /**
     * Sets this size to a relative size containing value
     *
     * @param value relative pos (0-1)
     * @param type  The unit of the value. MUST be relative_something.
     */
    public void setRelative(float value, CssValue.Unit type) {
        this.value.setRelative(value, type);
        setDirty(true);
    }

    /**
     * @return The raw {@link SizeValue}
     */
    @Override
    public SizeValue getValue() {
        return value;
    }

    /**
     * @return The min {@link SizeValue}
     */
    @Override
    public SizeValue getMinValue() {
        return minValue;
    }

    /**
     * @return The max@link SizeValue}
     */
    @Override
    public SizeValue getMaxValue() {
        return maxValue;
    }

    /**
     * @return True if this size has changed since the last computeValue call
     */
    @Override
    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    /**
     * An 1D size, absolute or relative
     */
    public static class SizeValue implements ReadableSizeValue {
        private float value;
        private GuiConstants.ENUM_SIZE type;

        /**
         * Creates a new SizeValue
         *
         * @param value The value, in absolute pixels or relative (0-1)
         * @param type  The type of the value, absolute or relative
         */
        public SizeValue(float value, GuiConstants.ENUM_SIZE type) {
            this.value = value;
            this.type = type;
        }

        /**
         * Computes the value of this 1D size, depending on the parent size
         *
         * @param screenWidth  The screen width
         * @param screenHeight The screen height
         * @param parentSize   The size of the parent, in the same dimension (width or height)
         * @return The real value
         */
        @Override
        public float computeValue(float screenWidth, float screenHeight, float parentSize) {
            float computed = value;
            if (type == GuiConstants.ENUM_SIZE.RELATIVE)
                computed = (value * parentSize);
            else if (type == GuiConstants.ENUM_SIZE.RELATIVE_VW)
                computed = (value * screenWidth);
            else if (type == GuiConstants.ENUM_SIZE.RELATIVE_VH)
                computed = (value * screenHeight);
            return computed;
        }

        /**
         * @return The raw value (absolute or relative)
         */
        @Override
        public float getRawValue() {
            return value;
        }

        /**
         * Sets this size to an absolute size containing value
         *
         * @param value a position in pixels (int)
         */
        public void setAbsolute(float value) {
            setType(GuiConstants.ENUM_SIZE.ABSOLUTE);
            this.value = value;
        }

        /**
         * Sets this size to a relative size containing value
         *
         * @param value relative pos (0-1)
         * @param type  The unit of the value. MUST be relative_something.
         */
        public void setRelative(float value, CssValue.Unit type) {
            switch (type) {
                case RELATIVE_TO_PARENT:
                    setType(GuiConstants.ENUM_SIZE.RELATIVE);
                    break;
                case RELATIVE_TO_WINDOW_WIDTH:
                    setType(GuiConstants.ENUM_SIZE.RELATIVE_VW);
                    break;
                case RELATIVE_TO_WINDOW_HEIGHT:
                    setType(GuiConstants.ENUM_SIZE.RELATIVE_VH);
                    break;
            }
            this.value = value;
        }

        @Override
        public GuiConstants.ENUM_SIZE type() {
            return type;
        }

        public void setType(GuiConstants.ENUM_SIZE type) {
            this.type = type;
        }
    }
}
