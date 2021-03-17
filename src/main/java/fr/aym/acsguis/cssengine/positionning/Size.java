package fr.aym.acsguis.cssengine.positionning;

import fr.aym.acsguis.utils.GuiConstants;

public class Size
{
    private SizeValue value = new SizeValue(1f, GuiConstants.ENUM_SIZE.RELATIVE);
    private SizeValue maxValue = new SizeValue(-1, GuiConstants.ENUM_SIZE.ABSOLUTE), minValue = new SizeValue(-1, GuiConstants.ENUM_SIZE.ABSOLUTE);
    private boolean dirty;

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

    public float getRawValue()
    {
        return value.getRawValue();
    }

    public void setAbsolute(float value) {
        this.value.setAbsolute(value);
        setDirty(true);
    }

    public void setRelative(float value) {
        this.value.setRelative(value);
        setDirty(true);
    }

    public SizeValue getValue() {
        return value;
    }

    public SizeValue getMinValue() {
        return minValue;
    }

    public SizeValue getMaxValue() {
        return maxValue;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public static class SizeValue
    {
        private float value;
        private GuiConstants.ENUM_SIZE type;

        public SizeValue(float value, GuiConstants.ENUM_SIZE type) {
            this.value = value;
            this.type = type;
        }

        public int computeValue(int parentSize)
        {
            int computed = (int) value;
            if(type == GuiConstants.ENUM_SIZE.RELATIVE)
                computed = (int) (value*parentSize);
            return computed;
        }

        public float getRawValue()
        {
            return value;
        }

        public void setAbsolute(float value) {
            setType(GuiConstants.ENUM_SIZE.ABSOLUTE);
            this.value = value;
        }

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
