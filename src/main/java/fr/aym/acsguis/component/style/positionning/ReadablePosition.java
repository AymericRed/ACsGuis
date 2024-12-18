package fr.aym.acsguis.component.style.positionning;

import fr.aym.acsguis.component.style.InternalComponentStyle;
import fr.aym.acsguis.utils.GuiConstants;

public interface ReadablePosition {
    float getRawValue();

    GuiConstants.ENUM_POSITION type();

    GuiConstants.ENUM_RELATIVE_POS relativePos();

    float computeValue(InternalComponentStyle componentStyle, int screenWidth, int screenHeight, float parentSize, float elementSize);

    boolean isDirty();
}
