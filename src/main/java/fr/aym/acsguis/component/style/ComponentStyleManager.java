package fr.aym.acsguis.component.style;

import com.helger.css.propertyvalue.CSSSimpleValueWithUnit;
import fr.aym.acsguis.utils.GuiConstants;
import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.cssengine.selectors.CssStackElement;
import fr.aym.acsguis.cssengine.selectors.EnumSelectorContext;
import fr.aym.acsguis.cssengine.positionning.Position;
import fr.aym.acsguis.cssengine.positionning.Size;
import fr.aym.acsguis.utils.GuiTextureSprite;
import fr.aym.acsguis.utils.IGuiTexture;

import java.util.Collection;

/**
 * Handles style of a {@link GuiComponent}
 */
public interface ComponentStyleManager
{
    ComponentStyleManager getParent();

    GuiComponent getOwner();

    CssStackElement getCssStack();

    ComponentStyleManager addAutoStyleHandler(AutoStyleHandler<?> handler);
    ComponentStyleManager removeAutoStyleHandler(AutoStyleHandler<?> handler);
    Collection<AutoStyleHandler<?>> getAutoStyleHandlers();

    void refreshCss(boolean reloadCssStack, String reason);
    void reloadCssStack();

    EnumSelectorContext getContext();

    ComponentStyleManager setForegroundColor(int color);
    int getForegroundColor();

    ComponentStyleManager setBorderRadius(CSSSimpleValueWithUnit radius);
    int getBorderRadius();

    enum BORDER_POSITION {INTERNAL, EXTERNAL }

    void update();

    /**
     * @return Return the {@link GuiTextureSprite} for render depending
     * on the component state.
     */
    IGuiTexture getTexture();
    ComponentStyleManager setTexture( IGuiTexture texture);

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

    Position getXPos();

    Position getYPos();

    int getRenderX();

    int getRenderY();

    Size getWidth();

    Size getHeight();
    
    int getRenderWidth();

    int getRenderHeight();

    void resize(int screenWidth, int screenHeight);

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
