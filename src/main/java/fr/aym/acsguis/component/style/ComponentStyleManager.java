package fr.aym.acsguis.component.style;

import com.helger.css.propertyvalue.CSSSimpleValueWithUnit;
import fr.aym.acsguis.cssengine.selectors.CompoundCssSelector;
import fr.aym.acsguis.cssengine.style.CssStyleProperty;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperties;
import fr.aym.acsguis.utils.GuiConstants;
import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.cssengine.selectors.CssStackElement;
import fr.aym.acsguis.cssengine.selectors.EnumSelectorContext;
import fr.aym.acsguis.cssengine.positionning.Position;
import fr.aym.acsguis.cssengine.positionning.Size;
import fr.aym.acsguis.utils.GuiTextureSprite;
import fr.aym.acsguis.utils.IGuiTexture;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

/**
 * Handles style of a {@link GuiComponent}
 *
 * @see fr.aym.acsguis.cssengine.style.CssComponentStyleManager
 * @see TextComponentStyleManager
 */
public interface ComponentStyleManager
{
    /**
     * @return The parent style manager
     */
    ComponentStyleManager getParent();

    /**
     * @return The owner of this style manager
     */
    GuiComponent<?> getOwner();

    /**
     * @return The applicable computed css properties (depends on the id, class, parents and custom css of this element)
     */
    CssStackElement getCssStack();

    /**
     * Sets custom css properties and reloads the css stack
     */
    void setCustomParsedStyle(@Nullable Map<CompoundCssSelector, Map<EnumCssStyleProperties, CssStyleProperty<?>>> data);

    /**
     * @return The applied custom css properties, or null
     */
    @Nullable
    Map<CompoundCssSelector, Map<EnumCssStyleProperties, CssStyleProperty<?>>> getCustomParsedStyle();

    /**
     * Adds an auto style handler
     * @return this
     */
    ComponentStyleManager addAutoStyleHandler(AutoStyleHandler<?> handler);
    /**
     * Removes an auto style handler
     * @return this
     */
    ComponentStyleManager removeAutoStyleHandler(AutoStyleHandler<?> handler);
    /**
     * @return The auto styles applied to this component
     */
    Collection<AutoStyleHandler<?>> getAutoStyleHandlers();

    /**
     * Refreshes the css of this element
     *
     * @param reloadCssStack If css stack should be reloaded (heavy)
     * @param reason Used for debug, identifies the caller of this function
     */
    void refreshCss(boolean reloadCssStack, String reason);
    /**
     * Reloads the css stack (heavy)
     */
    void reloadCssStack();

    /**
     * @return Return the state of the component
     */
    EnumSelectorContext getContext();

    /**
     * Detects state changes to update the component style (fired each tick)
     */
    void update();

    Position getXPos();

    Position getYPos();

    int getRenderX();

    int getRenderY();

    Size getWidth();

    Size getHeight();

    int getRenderWidth();

    int getRenderHeight();

    void resize(int screenWidth, int screenHeight);

    ComponentStyleManager setForegroundColor(int color);
    int getForegroundColor();

    ComponentStyleManager setBorderRadius(CSSSimpleValueWithUnit radius);
    int getBorderRadius();

    enum BORDER_POSITION {INTERNAL, EXTERNAL }

    /**
     * @return Return the {@link GuiTextureSprite} for render depending
     * on the component state.
     */
    IGuiTexture getTexture();
    ComponentStyleManager setTexture(IGuiTexture texture);

    int getBackgroundColor();
    ComponentStyleManager setBackgroundColor(int backgroundColor);

    int getZLevel();
    ComponentStyleManager setZLevel(int zLevel);

    boolean isRepeatBackgroundX();
    ComponentStyleManager setRepeatBackgroundX(boolean repeatBackgroundX);

    boolean isRepeatBackgroundY();
    ComponentStyleManager setRepeatBackgroundY(boolean repeatBackgroundY);

    int getOffsetX();
    ComponentStyleManager setOffsetX(int offsetX);
    int getOffsetY();
    ComponentStyleManager setOffsetY(int offsetY);

    ComponentStyleManager setBorderPosition(BORDER_POSITION borderPosition);
    BORDER_POSITION getBorderPosition();

    ComponentStyleManager setBorderSize(CSSSimpleValueWithUnit borderSize);
    int getBorderSize();

    ComponentStyleManager setBorderColor(int borderColor);
    int getBorderColor();

    ComponentStyleManager setVisible(boolean enabled);
    boolean isVisible();

    GuiConstants.ENUM_SIZE getTextureVerticalSize();
    ComponentStyleManager setTextureVerticalSize(GuiConstants.ENUM_SIZE textureVerticalSize);

    GuiConstants.ENUM_SIZE getTextureHorizontalSize();
    ComponentStyleManager setTextureHorizontalSize(GuiConstants.ENUM_SIZE textureHorizontalSize);

    int getTextureHeight();
    ComponentStyleManager setTextureHeight(int textureHeight);

    int getTextureWidth();
    ComponentStyleManager setTextureWidth(int textureWidth);
}
