package fr.aym.acsguis.cssengine.positionning;

import fr.aym.acsguis.component.style.InternalComponentStyle;
import fr.aym.acsguis.component.style.positionning.ReadablePosition;
import fr.aym.acsguis.cssengine.parsing.core.objects.CssValue;
import fr.aym.acsguis.utils.GuiConstants;
import net.minecraft.util.math.MathHelper;

/**
 * A 1D position, absolute or relative to something
 */
public class Position implements ReadablePosition {
    private float value;
    private GuiConstants.ENUM_RELATIVE_POS relativePos;
    private GuiConstants.ENUM_POSITION type;
    private boolean dirty;

    private PositionUpdateFunction positionFunction;

    /**
     * @param value       The value
     * @param type        The type of value
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
     * @param componentStyle The component style
     * @param screenWidth    The screen width
     * @param screenHeight   The screen height
     * @param parentSize     The size of the parent, in the same dimension (width or height)
     * @param elementSize    The side of this element, in the same dimension (width or height)
     * @return The real value
     */
    @Override
    public float computeValue(InternalComponentStyle componentStyle, int screenWidth, int screenHeight, float parentSize, float elementSize) {
        if (positionFunction != null) {
            positionFunction.apply(componentStyle, this);
            positionFunction = null;
        }
        float computed = value;
        if (type == GuiConstants.ENUM_POSITION.RELATIVE)
            computed = (value * parentSize);
        else if (type == GuiConstants.ENUM_POSITION.RELATIVE_VW)
            computed = (value * screenWidth);
        else if (type == GuiConstants.ENUM_POSITION.RELATIVE_VH)
            computed = (value * screenHeight);

        if (relativePos == GuiConstants.ENUM_RELATIVE_POS.END) {
            computed = parentSize - computed - elementSize;
        } else if (relativePos == GuiConstants.ENUM_RELATIVE_POS.CENTER) {
            computed = (parentSize - elementSize) / 2 - computed;
        }
        setDirty(false);
        //System.out.println("Compute pos from "+parentSize+" "+elementSize+" "+value+" "+relativePos+" "+type +" == " + computed);
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
     * Sets this position to an absolute position containing value
     *
     * @param value a position in pixels (int)
     */
    public void setAbsolute(float value) {
        setAbsolute(value, GuiConstants.ENUM_RELATIVE_POS.START);
    }

    /**
     * Sets this position to an absolute position containing value
     *
     * @param value a position in pixels (int)
     * @param pos   The local origin of this position to set
     */
    public void setAbsolute(float value, GuiConstants.ENUM_RELATIVE_POS pos) {
        setType(GuiConstants.ENUM_POSITION.ABSOLUTE);
        setRelativeTo(pos);
        this.value = value;
        setDirty(true);
    }

    /**
     * Sets this position to a relative position containing value
     *
     * @param value relative pos (0-1)
     * @param type  The unit of the value. MUST be relative_something.
     */
    public void setRelative(float value, CssValue.Unit type) {
        setRelative(value, type, GuiConstants.ENUM_RELATIVE_POS.START);
    }

    /**
     * Sets this position to a relative position containing value
     *
     * @param pos  The local origin of this position to set
     * @param type The unit of the value. MUST be relative_something.
     */
    public void setRelative(float value, CssValue.Unit type, GuiConstants.ENUM_RELATIVE_POS pos) {
        if (type == CssValue.Unit.RELATIVE_TO_PARENT &&
                type() == GuiConstants.ENUM_POSITION.RELATIVE &&
                relativePos() != pos &&
                relativePos() != GuiConstants.ENUM_RELATIVE_POS.CENTER &&
                pos != GuiConstants.ENUM_RELATIVE_POS.CENTER) {
            //System.out.println("Centering X ! "+getOwner());
            setRelativeTo(GuiConstants.ENUM_RELATIVE_POS.CENTER);

            this.value = MathHelper.clamp((value - this.value) / 2f, -0.5f, 0.5f);
            setDirty(true);
            //System.out.println("Rel x is "+this.relX+" "+getOwner());
        } else {
            switch (type) {
                case RELATIVE_TO_PARENT:
                    setType(GuiConstants.ENUM_POSITION.RELATIVE);
                    break;
                case RELATIVE_TO_WINDOW_WIDTH:
                    setType(GuiConstants.ENUM_POSITION.RELATIVE_VW);
                    break;
                case RELATIVE_TO_WINDOW_HEIGHT:
                    setType(GuiConstants.ENUM_POSITION.RELATIVE_VH);
                    break;
            }
            setRelativeTo(pos);
            this.value = value;
            setDirty(true);
        }
    }

    @Override
    public GuiConstants.ENUM_POSITION type() {
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
    @Override
    public GuiConstants.ENUM_RELATIVE_POS relativePos() {
        return relativePos;
    }

    /**
     * @return True if this position has changed since the last computeValue call
     */
    @Override
    public boolean isDirty() {
        return dirty;
    }

    protected void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public void setPositionFunction(PositionUpdateFunction positionFunction) {
        this.positionFunction = positionFunction;
    }

    /**
     * Called after the render width and height has been computed, so that the position can depend on them <br>
     * Used by the {@link fr.aym.acsguis.component.layout.FlowLayout}
     */
    public interface PositionUpdateFunction {
        void apply(InternalComponentStyle style, Position position);
    }
}
