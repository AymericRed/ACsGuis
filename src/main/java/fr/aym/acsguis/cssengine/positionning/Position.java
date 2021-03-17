package fr.aym.acsguis.cssengine.positionning;

import fr.aym.acsguis.utils.GuiConstants;
import net.minecraft.util.math.MathHelper;

public class Position //TODO DOC
{
    private float value;
    private GuiConstants.ENUM_RELATIVE_POS relativePos;
    private GuiConstants.ENUM_POSITION type;
    private boolean dirty;

    public Position(float value, GuiConstants.ENUM_RELATIVE_POS relativePos, GuiConstants.ENUM_POSITION type) {
        this.value = value;
        this.relativePos = relativePos;
        this.type = type;
    }

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

    public float getRawValue()
    {
        return value;
    }

    public void setAbsolute(float value)
    {
        setAbsolute(value, GuiConstants.ENUM_RELATIVE_POS.START);
    }

    public void setAbsolute(float value, GuiConstants.ENUM_RELATIVE_POS pos) {
        setType(GuiConstants.ENUM_POSITION.ABSOLUTE);
        setRelativeTo(pos);
        this.value = value;
        setDirty(true);
    }

    public void setRelative(float value) {
        setRelative(value, GuiConstants.ENUM_RELATIVE_POS.START);
    }
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

    private void setRelativeTo(GuiConstants.ENUM_RELATIVE_POS pos) {
        this.relativePos = pos;
    }

    public GuiConstants.ENUM_RELATIVE_POS relativePos() {
        return relativePos;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
