package fr.aym.acsguis.component.style;

import fr.aym.acsguis.cssengine.parsing.core.objects.CssValue;
import fr.aym.acsguis.cssengine.positionning.Position;
import fr.aym.acsguis.cssengine.positionning.Size;
import fr.aym.acsguis.utils.GuiConstants;
import fr.aym.acsguis.utils.IGuiTexture;

public interface InternalComponentStyle extends ComponentStyle {
    InternalComponentStyle setForegroundColor(int color);

    InternalComponentStyle setBorderRadius(CssValue radius);

    InternalComponentStyle setTexture(IGuiTexture texture);

    InternalComponentStyle setBackgroundColor(int backgroundColor);

    InternalComponentStyle setZLevel(int zLevel);

    InternalComponentStyle setRepeatBackgroundX(boolean repeatBackgroundX);

    InternalComponentStyle setRepeatBackgroundY(boolean repeatBackgroundY);

    InternalComponentStyle setBorderPosition(ComponentStyle.BORDER_POSITION borderPosition);

    InternalComponentStyle setBorderSize(CssValue borderSize);

    /**
     * Allows keeping thin border visible when the gui has a little scale (see {@link fr.aym.acsguis.component.layout.GuiScaler}) <br>
     * API : the border system will be refactored in near future
     *
     * @param inverseScreenScale True to rescale the border bigger, according to the gui scale
     * @return this
     */
    InternalComponentStyle setShouldRescaleBorder(boolean inverseScreenScale);

    InternalComponentStyle setBorderColor(int borderColor);

    InternalComponentStyle setVisible(boolean enabled);

    InternalComponentStyle setTextureVerticalSize(GuiConstants.ENUM_SIZE textureVerticalSize);

    InternalComponentStyle setTextureHorizontalSize(GuiConstants.ENUM_SIZE textureHorizontalSize);

    InternalComponentStyle setTextureHeight(int textureHeight);

    InternalComponentStyle setTextureWidth(int textureWidth);

    InternalComponentStyle setPaddingTop(int paddingTop);

    InternalComponentStyle setPaddingBottom(int paddingBottom);

    InternalComponentStyle setPaddingLeft(int paddingLeft);

    InternalComponentStyle setPaddingRight(int paddingRight);

    /**
     * @return The x pos of this component
     */
    @Override
    Position getXPos();

    /**
     * @return The y pos of this component
     */
    @Override
    Position getYPos();

    /**
     * @return The width of this component
     */
    @Override
    Size getWidth();

    /**
     * @return The height of this component
     */
    @Override
    Size getHeight();

    InternalComponentStyle setDisplay(GuiConstants.COMPONENT_DISPLAY componentDisplay);
}
