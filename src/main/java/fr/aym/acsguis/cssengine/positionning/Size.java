package fr.aym.acsguis.cssengine.positionning;

import fr.aym.acsguis.utils.GuiConstants;

/**
 * An 1D size, absolute or relative, with min and max values
 */
public class Size
{
    private final SizeValue value = new SizeValue(1f, GuiConstants.ENUM_SIZE.RELATIVE);
    private final SizeValue maxValue = new SizeValue(-1, GuiConstants.ENUM_SIZE.ABSOLUTE), minValue = new SizeValue(-1, GuiConstants.ENUM_SIZE.ABSOLUTE);
    private boolean dirty;

    /**
     * Computes the value of this 1D size, respecting min and max sizes, and depending on the parent size
     *
     * @param parentSize The size of the parent, in the same dimension (width or height)
     * @return The real value
     */
    public int computeValue(int parentSize) {
        int cVal = value.computeValue(parentSize);
        int min = minValue.computeValue(parentSize);
        int max = maxValue.computeValue(parentSize);
        if(min != -1)
            cVal = Math.max(min, cVal);
        if(max != -1)
            cVal = Math.min(max, cVal);
        setDirty(false);
        return cVal;
    }

    /**
     * @return The raw value (absolute or relative)
     */
    public float getRawValue()
    {
        return value.getRawValue();
    }

    /**
     * Sets this size to an absolute size containing value
     * @param value a position in pixels (int)
     */
    public void setAbsolute(float value) {
        this.value.setAbsolute(value);
        setDirty(true);
    }

    /**
     * Sets this size to a relative size containing value
     * @param value relative pos (0-1)
     */
    public void setRelative(float value) {
        this.value.setRelative(value);
        setDirty(true);
    }

    /**
     * @return The raw {@link SizeValue}
     */
    public SizeValue getValue() {
        return value;
    }

    /**
     * @return The min {@link SizeValue}
     */
    public SizeValue getMinValue() {
        return minValue;
    }

    /**
     * @return The max@link SizeValue}
     */
    public SizeValue getMaxValue() {
        return maxValue;
    }

    /**
     * @return True if this size has changed since the last computeValue call
     */
    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    /**
     * An 1D size, absolute or relative
     */
    public static class SizeValue
    {
        private float value;
        private GuiConstants.ENUM_SIZE type;

        public SizeValue(float value, GuiConstants.ENUM_SIZE type) {
            this.value = value;
            this.type = type;
        }

        /**
         * Computes the value of this 1D size, depending on the parent size
         *
         * @param parentSize The size of the parent, in the same dimension (width or height)
         * @return The real value
         */
        public int computeValue(int parentSize)
        {
            int computed = (int) value;
            if(type == GuiConstants.ENUM_SIZE.RELATIVE)
                computed = (int) (value*parentSize);
            return computed;
        }

        /**
         * @return The raw value (absolute or relative)
         */
        public float getRawValue()
        {
            return value;
        }

        /**
         * Sets this size to an absolute size containing value
         * @param value a position in pixels (int)
         */
        public void setAbsolute(float value) {
            setType(GuiConstants.ENUM_SIZE.ABSOLUTE);
            this.value = value;
        }

        /**
         * Sets this size to a relative size containing value
         * @param value relative pos (0-1)
         */
        public void setRelative(float value) {
            setType(GuiConstants.ENUM_SIZE.RELATIVE);
            this.value = value;
        }

        public GuiConstants.ENUM_SIZE type() {
            return type;
        }

        public void setType(GuiConstants.ENUM_SIZE type) {
            this.type = type;
        }
    }
}
