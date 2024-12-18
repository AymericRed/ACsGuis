package fr.aym.acsguis.component.style;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.component.style.positionning.ReadablePosition;
import fr.aym.acsguis.component.style.positionning.ReadableSize;
import fr.aym.acsguis.cssengine.selectors.CssStackElement;
import fr.aym.acsguis.cssengine.selectors.EnumSelectorContext;
import fr.aym.acsguis.cssengine.style.CssComponentStyle;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperty;
import fr.aym.acsguis.utils.GuiConstants;
import fr.aym.acsguis.utils.GuiTextureSprite;
import fr.aym.acsguis.utils.IGuiTexture;

/**
 * Handles style of a {@link GuiComponent}
 *
 * @see CssComponentStyle
 * @see TextComponentStyle
 */
public interface ComponentStyle {
    /**
     * @return The parent style manager
     */
    ComponentStyle getParent();

    /**
     * @return The owner of this style manager
     */
    GuiComponent getOwner();

    /**
     * @return The applicable computed css properties (depends on the id, class, parents and custom css of this element)
     */
    CssStackElement getCssStack();

    /**
     * todo doc
     *
     * @return
     */
    ComponentStyleCustomizer getCustomizer();

    /**
     * Refreshes the css of this element
     *
     * @param reloadCssStack If css stack should be reloaded (heavy)
     */
    default void refreshStyle() {
        refreshStyle(getOwner().getGui());
    }

    /**
     * Refreshes the css of this element (all properties)
     *
     * @param gui            The displayed gui
     * @param reloadCssStack If css stack should be reloaded (heavy)
     */
    default void refreshStyle(GuiFrame.APIGuiScreen gui) {
        refreshStyle(gui, EnumCssStyleProperty.values());
    }

    /**
     * Refreshes the css of this element
     *
     * @param gui            The displayed gui
     * @param reloadCssStack If css stack should be reloaded (heavy)
     * @param properties     The properties to refresh
     */
    void refreshStyle(GuiFrame.APIGuiScreen gui, EnumCssStyleProperty... properties);

    /**
     * Reloads the css stack (heavy)
     */
    void reloadCssStack();

    /**
     * Resets the css stack <br>
     * It will be reloaded upon next component update
     */
    void resetCssStack();

    /**
     * @return Return the state of the component
     */
    EnumSelectorContext getContext();

    /**
     * Detects state changes to update the component style (fired each tick)
     */
    void update(GuiFrame.APIGuiScreen gui);

    /**
     * @return The x pos of this component
     */
    ReadablePosition getXPos();

    /**
     * @return The y pos of this component
     */
    ReadablePosition getYPos();

    /**
     * @return The computed X render pos of this component
     */
    float getRenderX();

    /**
     * @return The computed y render pos of this component
     */
    float getRenderY();

    /**
     * @return The width of this component
     */
    ReadableSize getWidth();

    /**
     * @return The height of this component
     */
    ReadableSize getHeight();

    /**
     * @return The computed render width of this component
     */
    float getRenderWidth();

    /**
     * @return The computed render height of this component
     */
    float getRenderHeight();

    /**
     * Fired when the display screen is resized <br>
     * Refreshes the css state
     *
     * @param gui The displayed gui
     */
    void resize(GuiFrame.APIGuiScreen gui);

    int getForegroundColor();

    float getBorderRadius();

    GuiConstants.COMPONENT_DISPLAY getDisplay();

    enum BORDER_POSITION {INTERNAL, EXTERNAL}

    /**
     * @return Return the {@link GuiTextureSprite} for render depending
     * on the component state.
     */
    IGuiTexture getTexture();

    int getBackgroundColor();

    int getZLevel();

    boolean isRepeatBackgroundX();

    boolean isRepeatBackgroundY();

    int getOffsetX();

    int getOffsetY();

    ComponentStyle setOffsetX(int offsetX);

    ComponentStyle setOffsetY(int offsetY);

    BORDER_POSITION getBorderPosition();

    float getBorderSize();

    /**
     * Allows keeping thin border visible when the gui has a little scale (see {@link fr.aym.acsguis.component.layout.GuiScaler}) <br>
     * API : the border system will be refactored in near future
     *
     * @return True to rescale the border bigger, according to the gui scale
     */
    boolean shouldRescaleBorder();

    int getBorderColor();

    boolean isVisible();

    GuiConstants.ENUM_SIZE getTextureVerticalSize();

    GuiConstants.ENUM_SIZE getTextureHorizontalSize();

    int getTextureHeight();

    int getTextureWidth();

    int getPaddingTop();

    int getPaddingBottom();

    int getPaddingLeft();

    int getPaddingRight();
}
