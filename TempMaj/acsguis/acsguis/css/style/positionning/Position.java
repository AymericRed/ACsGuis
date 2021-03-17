package fr.aym.acsguis.cssengine.positionning;

import fr.aym.acsguis.GuiConstants;
import net.minecraft.util.math.MathHelper;

public class Position
{
    private float value;
    private GuiConstants.ENUM_RELATIVE_POS relativePos;
    private GuiConstants.ENUM_POSITION type;

    public Position(float value, GuiConstants.ENUM_RELATIVE_POS relativePos, GuiConstants.ENUM_POSITION type) {
        this.value = value;
        this.relativePos = relativePos;
        this.type = type;
    }

    public int compute(int parentSize)
    {
        switch (type)
        {
            case ABSOLUTE:
                return (int) value;
            case RELATIVE:
                return (int) (value*parentSize);
        }
        return 0;
    }

    public float getRawValue()
    {
        return value;
    }

    public void setRawValue(float value)
    {
        this.value = value;
    }

    public GuiConstants.ENUM_POSITION type()
    {
        return type;
    }

    public void setType(GuiConstants.ENUM_POSITION type) {
        this.type = type;
    }

    public GuiConstants.ENUM_RELATIVE_POS relativePos() {
        return relativePos;
    }

    private void setRelativePos(GuiConstants.ENUM_RELATIVE_POS pos) {
        this.relativePos = pos;
    }
    public void setRelativePos(float value, GuiConstants.ENUM_RELATIVE_POS pos) {
        if(type() == GuiConstants.ENUM_POSITION.RELATIVE && relativePos() != pos && relativePos() != GuiConstants.ENUM_RELATIVE_POS.CENTER && pos != GuiConstants.ENUM_RELATIVE_POS.CENTER)
        {
            //System.out.println("Centering X ! "+getOwner());
            setRelativePos(GuiConstants.ENUM_RELATIVE_POS.CENTER);

            this.value = MathHelper.clamp((value-this.value)/2f, -0.5f, 0.5f);
            //System.out.println("Rel x is "+this.relX+" "+getOwner());
        }
        else {
            setType(GuiConstants.ENUM_POSITION.RELATIVE);
            setRelativePos(pos);
            this.value = value;
        }
    }
}
