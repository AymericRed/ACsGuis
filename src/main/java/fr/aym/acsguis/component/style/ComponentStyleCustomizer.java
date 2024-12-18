package fr.aym.acsguis.component.style;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.cssengine.parsing.core.objects.CssValue;
import fr.aym.acsguis.cssengine.selectors.CompoundCssSelector;
import fr.aym.acsguis.cssengine.style.CssStyleProperty;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperty;
import fr.aym.acsguis.utils.GuiConstants;
import fr.aym.acsguis.utils.IGuiTexture;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public interface ComponentStyleCustomizer {
    GuiComponent getOwner();

    boolean hasChanges();

    EnumCssStyleProperty[] getChanges();

    void removeChange(EnumCssStyleProperty property);

    @Nullable
    AutoStyleHandler.SimpleStyleFunction getStyleOverride(EnumCssStyleProperty property);

    /**
     * Adds this style to the element <br>
     * The style will persist after reloading the element style
     * TODO DOC
     *
     * @param property The property name
     * @param value    The new property value
     * @return this
     */
    ComponentStyleCustomizer putStyleOverride(EnumCssStyleProperty property, AutoStyleHandler.SimpleStyleFunction styleFunction);

    /**
     * Sets custom css properties and reloads the css stack
     */
    void setCustomParsedStyle(@Nullable Map<CompoundCssSelector, Map<EnumCssStyleProperty, CssStyleProperty<?>>> data);

    /**
     * @return The applied custom css properties, or null
     */
    @Nullable
    Map<CompoundCssSelector, Map<EnumCssStyleProperty, CssStyleProperty<?>>> getCustomParsedStyle();

    /**
     * Adds this style to the element <br>
     * The style will persist after reloading the element style
     * TODO DOC
     *
     * @param property The property name
     * @param value    The new property value
     * @return this
     */
    ComponentStyleCustomizer withAutoStyle(EnumCssStyleProperty property, AutoStyleHandler.SimpleStyleFunction styleFunction);

    /**
     * Adds an auto style handler
     *
     * @return this
     */
    ComponentStyleCustomizer withAutoStyles(AutoStyleHandler<?> handler, EnumCssStyleProperty... targetProperties);

    /**
     * Removes an auto style handler
     *
     * @return this
     */
    ComponentStyleCustomizer removeAutoStyles(AutoStyleHandler<?> handler, EnumCssStyleProperty... targetProperties);

    @Nullable
    List<AutoStyleHandler<?>> getAutoStyleHandlers(EnumCssStyleProperty property);

    ComponentStyleCustomizer setForegroundColor(int color);

    ComponentStyleCustomizer setBorderRadius(CssValue radius);

    ComponentStyleCustomizer setTexture(IGuiTexture texture);

    ComponentStyleCustomizer setBackgroundColor(int backgroundColor);

    ComponentStyleCustomizer setZLevel(int zLevel);

    ComponentStyleCustomizer setRepeatBackgroundX(boolean repeatBackgroundX);

    ComponentStyleCustomizer setRepeatBackgroundY(boolean repeatBackgroundY);

    ComponentStyleCustomizer setBorderPosition(ComponentStyle.BORDER_POSITION borderPosition);

    ComponentStyleCustomizer setBorderSize(CssValue borderSize);

    /**
     * Allows keeping thin border visible when the gui has a little scale (see {@link fr.aym.acsguis.component.layout.GuiScaler}) <br>
     * API : the border system will be refactored in near future
     *
     * @param inverseScreenScale True to rescale the border bigger, according to the gui scale
     * @return this
     */
    ComponentStyleCustomizer setShouldRescaleBorder(boolean inverseScreenScale);

    ComponentStyleCustomizer setBorderColor(int borderColor);

    ComponentStyleCustomizer setVisible(boolean enabled);

    ComponentStyleCustomizer setTextureVerticalSize(GuiConstants.ENUM_SIZE textureVerticalSize);

    ComponentStyleCustomizer setTextureHorizontalSize(GuiConstants.ENUM_SIZE textureHorizontalSize);

    ComponentStyleCustomizer setTextureHeight(int textureHeight);

    ComponentStyleCustomizer setTextureWidth(int textureWidth);

    ComponentStyleCustomizer setXPos(float x);

    ComponentStyleCustomizer setYPos(float y);

    ComponentStyleCustomizer setPosition(float x, float y);

    ComponentStyleCustomizer setRelativeX(float relativeX, CssValue.Unit unit);

    ComponentStyleCustomizer setRelativeY(float relativeY, CssValue.Unit unit);

    ComponentStyleCustomizer setRelativePosition(float relativeX, float relativeY, CssValue.Unit unit);

    ComponentStyleCustomizer setXOrigin(GuiConstants.ENUM_RELATIVE_POS xOrigin);

    ComponentStyleCustomizer setYOrigin(GuiConstants.ENUM_RELATIVE_POS yOrigin);

    ComponentStyleCustomizer setWidth(float width);

    ComponentStyleCustomizer setHeight(float height);

    ComponentStyleCustomizer setSize(float width, float height);

    ComponentStyleCustomizer setRelativeWidth(float relativeWidth, CssValue.Unit unit);

    ComponentStyleCustomizer setRelativeHeight(float relativeHeight, CssValue.Unit unit);

    ComponentStyleCustomizer setRelativeSize(float relativeWidth, float relativeHeight, CssValue.Unit unit);

    ComponentStyleCustomizer clearProperty(EnumCssStyleProperty property);
}
