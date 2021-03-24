package fr.aym.acsguis.cssengine.positionning;

import fr.aym.acsguis.utils.GuiConstants;
import net.minecraft.util.math.MathHelper;

/**
 * A 1D position, absolute or relative to something
 */
public class Position
{
    private float value;
    private GuiConstants.ENUM_RELATIVE_POS relativePos;
    private GuiConstants.ENUM_POSITION type;
    private boolean dirty;

    /**
     * @param value The value
     * @param type The type of value
     * @param relativePos The local origin of this position
     */
    public Position(float value, GuiConstants.ENUM_POSITION type, GuiConstants.ENUM_RELATIVE_POS relativePos) {
        this.value = value;
        this.relativePos = relativePos;
        this.type = type;
    }

    /**
     * Computes the value of this 1D position depending on the element and parent sizes
     *
     * @param parentSize The size of the parent, in the same dimension (width or height)
     * @param elementSize The side of this element, in the same dimension (width or height)
     * @return The real value
     */
    public int computeValue(int parentSize, int elementSize)
    {
        //System.out.println("Compute pos from "+parentSize+" "+elementSize+" "+value+" "+relativePos+" "+type);
        int computed = (int) value;
        if(type == GuiConstants.ENUM_POSITION.RELATIVE)
            computed = (int) (value*parentSize);
        if(relativePos == GuiConstants.ENUM_RELATIVE_POS.END) {
            computed = parentSize - computed - elementSize;
        } else if(relativePos == GuiConstants.ENUM_RELATIVE_POS.CENTER) {
            computed = (parentSize - elementSize)/2 - computed;
        }
        setDirty(false);
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
     * Sets this position to an absolute position containing value
     * @param value a position in pixels (int)
     */
    public void setAbsolute(float value)
    {
        setAbsolute(value, GuiConstants.ENUM_RELATIVE_POS.START);
    }
    /**
     * Sets this position to an absolute position containing value
     * @param value a position in pixels (int)
     * @param pos The local origin of this position to set
     */
    public void setAbsolute(float value, GuiConstants.ENUM_RELATIVE_POS pos) {
        setType(GuiConstants.ENUM_POSITION.ABSOLUTE);
        setRelativeTo(pos);
        this.value = value;
        setDirty(true);
    }

    /**
     * Sets this position to a relative position containing value
     * @param value relative pos (0-1)
     */
    public void setRelative(float value) {
        setRelative(value, GuiConstants.ENUM_RELATIVE_POS.START);
    }
    /**
     * Sets this position to a relative position containing value
     * @param pos The local origin of this position to set
     */
    public void setRelative(float value, GuiConstants.ENUM_RELATIVE_POS pos) {
        if(type() == GuiConstants.ENUM_POSITION.RELATIVE && relativePos() != pos && relativePos() != GuiConstants.ENUM_RELATIVE_POS.CENTER && pos != GuiConstants.ENUM_RELATIVE_POS.CENTER)
        {
            //System.out.println("Centering X ! "+getOwner());
            setRelativeTo(GuiConstants.ENUM_RELATIVE_POS.CENTER);

            this.value = MathHelper.clamp((value-this.value)/2f, -0.5f, 0.5f);
            setDirty(true);
            //System.out.println("Rel x is "+this.relX+" "+getOwner());
        }
        else {
            setType(GuiConstants.ENUM_POSITION.RELATIVE);
            setRelativeTo(pos);
            this.value = value;
            setDirty(true);
        }
    }

    public GuiConstants.ENUM_POSITION type()
    {
        return type;
    }

    public void setType(GuiConstants.ENUM_POSITION type) {
        this.type = type;
    }

    /**
     * Sets local the origin of this position
     */
    private void setRelativeTo(GuiConstants.ENUM_RELATIVE_POS pos) {
        this.relativePos = pos;
    }

    /**
     * @return the local origin of this position
     */
    public GuiConstants.ENUM_RELATIVE_POS relativePos() {
        return relativePos;
    }

    /**
     * @return True if this position has changed since the last computeValue call
     */
    public boolean isDirty() {
        return dirty;
    }

    protected void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
