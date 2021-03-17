package fr.aym.acsguis.cssengine.style;

import com.helger.css.ECSSUnit;
import com.helger.css.propertyvalue.CSSSimpleValueWithUnit;
import fr.aym.acsguis.GuiConstants;
import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.panel.GuiScrollPane;
import fr.aym.acsguis.cssengine.parsing.DnxCssParser;
import fr.aym.acsguis.cssengine.selectors.CssStackElement;
import fr.aym.acsguis.cssengine.selectors.EnumSelectorContext;
import fr.aym.acsguis.cssengine.positionning.Position;
import fr.aym.acsguis.utils.GuiTextureSprite;
import fr.aym.acsguis.utils.ICssTexture;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CssComponentStyleManager implements ComponentStyleManager
{
    private final GuiComponent<?> component;
    
    protected int computedX, computedY;
    protected Position xPos = new Position(0, GuiConstants.ENUM_RELATIVE_POS.START, GuiConstants.ENUM_POSITION.ABSOLUTE), yPos = new Position(0, GuiConstants.ENUM_RELATIVE_POS.START, GuiConstants.ENUM_POSITION.ABSOLUTE);

    protected int width, height;
    protected int maxWidth = -1, maxHeight = -1, minWidth = -1, minHeight = -1;
    protected float relWidth = 1, relHeight = 1;

    /**
     * Component parent's relative alignment, ABSOLUTE if not relative
     * {@link GuiConstants.ENUM_POSITION}
     **/
    protected GuiConstants.ENUM_POSITION verticalAlignment = GuiConstants.ENUM_POSITION.ABSOLUTE;

    protected GuiConstants.ENUM_RELATIVE_Y relativeY = GuiConstants.ENUM_RELATIVE_Y.TOP;

    protected GuiConstants.ENUM_SIZE horizontalSize = GuiConstants.ENUM_SIZE.ABSOLUTE;

    protected GuiConstants.ENUM_SIZE verticalSize = GuiConstants.ENUM_SIZE.ABSOLUTE;

    /**Offset values of the component, used for GuiScrollPane for example**/
    protected int offsetX, offsetY;

    /**
     * The render zLevel, use to sort the render pipeline, by default the components
     * are rendered in the ordered in which they had been added to their parent.
     **/
    protected int zLevel = 0;

    protected boolean visible;

    private int foregroundColor = Color.WHITE.getRGB();
    private int backgroundColor = Color.TRANSLUCENT;

    protected boolean repeatBackgroundX = false, repeatBackgroundY = false;

    protected float relBorderSize = -1, relBorderRadius = -1;
    protected int borderSize = 0, borderRadius;
    protected int borderColor = Color.DARK_GRAY.getRGB();

    protected BORDER_POSITION borderPosition = BORDER_POSITION.EXTERNAL;

    protected ICssTexture texture;
    protected int textureWidth, textureHeight;
    protected float textureRelWidth = 1, textureRelHeight = 1;

    protected GuiConstants.ENUM_SIZE textureHorizontalSize = GuiConstants.ENUM_SIZE.ABSOLUTE;
    protected GuiConstants.ENUM_SIZE textureVerticalSize = GuiConstants.ENUM_SIZE.ABSOLUTE;
    
    protected CssStackElement cssStack;
    protected final List<AutoStyleHandler<?>> autoStyleHandler = new ArrayList<>();

    public CssComponentStyleManager(GuiComponent<?> component)
    {
        this.component = component;
    }

    private EnumSelectorContext lastContext = EnumSelectorContext.NORMAL;

    @Nullable
    public CssStackElement getCssStack() {
        return cssStack;
    }

    @Override
    public void update() {
        onCssChange(component.getState());
    }

    private void onCssChange(EnumSelectorContext context) {
        if(context != lastContext || cssStack == null) {
            if(cssStack == null && (getParent() == null || getParent().getCssStack() != null)) {
                //System.out.println("Init stack with opt "+context);
                reloadCssStack();
            }
            if(cssStack != null) {
                //System.out.println("Change context from "+lastContext+" to "+context+" "+getOwner());
                lastContext = context;
                //Reset
                refreshCss(false, "state_upd");
            }
        }
    }

    @Override
    public void reloadCssStack() {
        //System.out.println("RELOAD stack of "+getOwner());
        cssStack = DnxCssParser.getStyleFor(this);
    }

    @Override //reload css
    public void refreshCss(boolean reloadCssStack, String reason) {
        if(reloadCssStack && cssStack != null) {
            //System.out.println("RECOMPUTING STACK");
            reloadCssStack();
        }
        //update css with parents
        if(cssStack != null) {
            //Anticipate and apply the new context now
            lastContext = component.getState();

            //System.out.println("DOING Refresh "+getOwner()+" with opt "+reloadCssStack+" cur stack "+(cssStack!=null)+" and nw state? "+lastContext+" REASON "+reason);
            //Reset
            getXPos().setType(GuiConstants.ENUM_POSITION.ABSOLUTE);
            getYPos().setType(GuiConstants.ENUM_POSITION.ABSOLUTE);
            //update
            cssStack.applyAllProperties(getContext(), this);
            updateComponentSize(GuiFrame.resolution.getScaledWidth(), GuiFrame.resolution.getScaledHeight());
            updateComponentPosition(GuiFrame.resolution.getScaledWidth(), GuiFrame.resolution.getScaledHeight());

            if (component instanceof GuiPanel) {
                for (GuiComponent<?> c : ((GuiPanel) component).getChildComponents()) {
                    if(!((GuiPanel)component).getToRemoveComponents().contains(c))
                        c.getStyle().refreshCss(reloadCssStack, "i_"+reason);
                }
            }
        }
        /*else
        {
            System.out.println("SKIPPED Refresh "+getOwner()+" with opt "+reloadCssStack+" cur stack "+(cssStack!=null)+" REASON "+reason);
        }*/
    }
    @Override
    public EnumSelectorContext getContext() {
        return lastContext;
    }

    @Override
    public ComponentStyleManager addAutoStyleHandler(AutoStyleHandler<?> handler) {
        autoStyleHandler.add(handler);
        return this;
    }
    @Override
    public ComponentStyleManager removeAutoStyleHandler(AutoStyleHandler<?> handler) {
        autoStyleHandler.remove(handler);
        return this;
    }
    @Override
    public Collection<AutoStyleHandler<?>> getAutoStyleHandlers() {
        return autoStyleHandler;
    }

    @Override
    public ComponentStyleManager setForegroundColor(int color) {
        this.foregroundColor = color;
        return this;
    }

    @Override
    public int getForegroundColor() {
        return foregroundColor;
    }

    @Override
    public ComponentStyleManager setBorderRadius(CSSSimpleValueWithUnit radius) {
        if(radius.getUnit() == ECSSUnit.PERCENTAGE) {
            this.relBorderRadius = (float) radius.getAsIntValue() / 100;
            this.borderRadius = (int) (relBorderRadius * getWidth());
        }
        else {
            this.relBorderRadius = -1;
            this.borderRadius = radius.getAsIntValue();
        }
        return this;
    }

    @Override
    public int getBorderRadius() {
        return borderRadius;
    }

    public void updateComponentSize(int screenWidth, int screenHeight) {

        if(getHorizontalSize() == GuiConstants.ENUM_SIZE.RELATIVE) {
            int parentWidth = component.getParent() != null ? component.getParent().getWidth() : screenWidth;
            setWidth((int) (parentWidth * getRelativeWidth()));
        }

        if(getVerticalSize() == GuiConstants.ENUM_SIZE.RELATIVE) {
            int parentHeight = component.getParent() != null ? component.getParent().getHeight() : screenHeight;
            setHeight((int) (parentHeight * getRelativeHeight()));
        }

        if(getTextureHorizontalSize() == GuiConstants.ENUM_SIZE.RELATIVE) {
            setTextureWidth((int) (getWidth() * getTextureRelativeWidth()));
        }

        if(getTextureVerticalSize() == GuiConstants.ENUM_SIZE.RELATIVE) {
            setTextureHeight((int) (getHeight() * getTextureRelativeHeight()));
        }

    }

    /**
     * Update the x and y coordinates
     */
    public void updateComponentPosition(int screenWidth, int screenHeight)
    {
        int parentWidth = component.getParent() != null ? component.getParent().getWidth() : screenWidth;
        int parentHeight = component.getParent() != null ? component.getParent().getHeight() : screenHeight;

        if(getHorizontalAlignment() == GuiConstants.ENUM_POSITION.RELATIVE) {
            setX((int) (parentWidth * getRelativeX()), getEnumRelativeX());
        }
        //else if(getHorizontalAlignment() == GuiConstants.ENUM_POSITION.ABSOLUTE) {
            if(getEnumRelativeX() == GuiConstants.ENUM_RELATIVE_X.RIGHT) {
                setX(parentWidth - x - getWidth());
            } else if(getEnumRelativeX() == GuiConstants.ENUM_RELATIVE_X.CENTER) {
                setX((parentWidth  - getWidth())/2 - x);
            }
        //}

        if(getVerticalAlignment() == GuiConstants.ENUM_POSITION.RELATIVE) {
            //System.out.println("Read rel "+getRelativeY()+" "+parentHeight+" "+getVerticalAlignment());
            setY((int) (parentHeight * getRelativeY()), getEnumRelativeY());
        }
        //else if(getVerticalAlignment() == GuiConstants.ENUM_POSITION.ABSOLUTE) {
            if(getEnumRelativeY() == GuiConstants.ENUM_RELATIVE_Y.BOTTOM) {
                setY(parentHeight - y - getHeight());
            } else if(getEnumRelativeY() == GuiConstants.ENUM_RELATIVE_Y.CENTER) {
                //System.out.println("Center by "+y);
                setY(parentHeight / 2 - y - getHeight()/2);
            }
        //}
    }

    /**
     * @return Return the {@link GuiTextureSprite} for render depending
     * on the component state.
     */
    @Override
    public ICssTexture getTexture()
    {
        return texture;
    }

    @Override
    public void resize(int screenWidth, int screenHeight) {
        refreshCss(false,"resize");
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public ComponentStyleManager setVisible(boolean visible) {
        this.visible = visible;
        if(!visible) {
            component.setPressed(false);
            component.setHovered(false);
        }
        return this;
    }

    @Override
    public Position getXPos() {
        return xPos;
    }

    @Override
    public Position getYPos() {
        return yPos;
    }

    @Override
    public int getRenderX() {
        return computedX;
    }

    @Override
    public int getRenderY() {
        return computedY;
    }

    @Override
    public ComponentStyleManager setHorizontalSize(GuiConstants.ENUM_SIZE horizontalSize) {
        this.horizontalSize = horizontalSize;
        return this;
    }

    @Override
    public GuiConstants.ENUM_SIZE getHorizontalSize() {
        return horizontalSize;
    }

    @Override
    public ComponentStyleManager setVerticalSize(GuiConstants.ENUM_SIZE verticalSize) {
        this.verticalSize = verticalSize;
        return this;
    }

    @Override
    public GuiConstants.ENUM_SIZE getVerticalSize() {
        return verticalSize;
    }

    @Override
    public ComponentStyleManager setRelativeWidth(float relWidth) {
        setHorizontalSize(GuiConstants.ENUM_SIZE.RELATIVE);
        this.relWidth = MathHelper.clamp(relWidth, 0, Float.MAX_VALUE);

        if(component.getParent() != null) {
            setWidth((int) (getRelativeWidth() * component.getParent().getWidth()));
        }

        return this;
    }

    @Override
    public ComponentStyleManager setRelativeHeight(float relHeight) {
        setVerticalSize(GuiConstants.ENUM_SIZE.RELATIVE);
        this.relHeight = MathHelper.clamp(relHeight, 0, Float.MAX_VALUE);

        if(component.getParent() != null) {
            setHeight((int) (getRelativeHeight() * component.getParent().getHeight()));
        }

        return this;
    }

    @Override
    public float getRelativeWidth() {
        return relWidth;
    }

    @Override
    public float getRelativeHeight() {
        return relHeight;
    }

    @Override
    public ComponentStyleManager setWidth(int width) {
        this.width = width;
        if(relBorderSize != -1)
            this.borderSize = (int) (relBorderSize * getWidth());
        if(relBorderRadius != -1)
            this.borderRadius = (int) (relBorderRadius * getWidth());

        setHorizontalSize(GuiConstants.ENUM_SIZE.ABSOLUTE);
        //updateComponentPosition(GuiFrame.resolution.getScaledWidth(), GuiFrame.resolution.getScaledHeight());

        if(component.getParent() instanceof GuiScrollPane) {
            ((GuiScrollPane) component.getParent()).updateSlidersVisibility();
        }

        return this;
    }

    @Override
    public int getMaxWidth() {
        return maxWidth;
    }

    @Override
    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    @Override
    public int getMinWidth() {
        return minWidth;
    }

    @Override
    public void setMinWidth(int maxWidth) {
        this.minWidth = minWidth;
    }

    @Override
    public ComponentStyleManager setHeight(int height) {
        this.height = height;
        setVerticalSize(GuiConstants.ENUM_SIZE.ABSOLUTE);
        //updateComponentPosition(GuiFrame.resolution.getScaledWidth(), GuiFrame.resolution.getScaledHeight());

        if(component.getParent() instanceof GuiScrollPane) {
            ((GuiScrollPane) component.getParent()).updateSlidersVisibility();
        }

        return this;
    }

    @Override
    public int getMaxHeight() {
        return maxHeight;
    }

    @Override
    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    @Override
    public int getMinHeight() { return minHeight; }

    @Override
    public void setMinHeight(int minHeight) {
        this.minHeight = minHeight;
    }

    @Override
    public int getWidth() {
        if(maxWidth != -1)
            return Math.min(width, maxWidth);
        if(minWidth != -1)
            return Math.max(width, minWidth);
        return width;
    }

    @Override
    public int getHeight() {
        if(maxHeight != -1)
            return Math.min(height, maxHeight);
        if(minHeight != -1)
            return Math.max(height, minHeight);
        return height;
    }

    @Override
    public int getOffsetX() {
        return offsetX;
    }

    @Override
    public ComponentStyleManager setOffsetX(int offsetX) {
        this.offsetX = offsetX;
        return this;
    }

    @Override
    public int getOffsetY() {
        return offsetY;
    }

    @Override
    public ComponentStyleManager setOffsetY(int offsetY) {
        this.offsetY = offsetY;
        return this;
    }

    @Override
    public int getZLevel() {
        return zLevel;
    }

    @Override
    public ComponentStyleManager setZLevel(int zLevel) {
        this.zLevel = zLevel;
        return this;
    }

    @Override
    public int getBackgroundColor() {
        return backgroundColor;
    }
    @Override
    public ComponentStyleManager setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    @Override
    public ComponentStyleManager setRepeatBackgroundX(boolean repeatBackgroundX) {
        this.repeatBackgroundX = repeatBackgroundX;
        return this;
    }
    @Override
    public ComponentStyleManager setRepeatBackgroundY(boolean repeatBackgroundY) {
        this.repeatBackgroundY = repeatBackgroundY;
        return this;
    }

    @Override
    public ComponentStyleManager setTexture(ICssTexture texture) {
        this.texture = texture;
        if(texture != null)
        {
            setTextureWidth(texture.getTextureWidth());
            setTextureHeight(texture.getTextureHeight());
        }
        return this;
    }

    @Override
    public int getTextureWidth() {
        return textureWidth;
    }
    @Override
    public ComponentStyleManager setTextureWidth(int textureWidth) {
        this.textureWidth = textureWidth;
        return this;
    }

    @Override
    public int getTextureHeight() {
        return textureHeight;
    }
    @Override
    public ComponentStyleManager setTextureHeight(int textureHeight) {
        this.textureHeight = textureHeight;
        return this;
    }

    public float getTextureRelativeWidth() {
        return textureRelWidth;
    }

    public ComponentStyleManager setTextureRelativeWidth(float textureRelWidth) {
        setTextureHorizontalSize(GuiConstants.ENUM_SIZE.RELATIVE);
        this.textureRelWidth = MathHelper.clamp(textureRelWidth, 0, Float.MAX_VALUE);

        if(getParent() != null) {
            setTextureWidth((int) (getTextureRelativeWidth() * getParent().getWidth()));
        }

        return this;
    }

    public float getTextureRelativeHeight() {
        return textureRelHeight;
    }

    public ComponentStyleManager setTextureRelativeHeight(float textureRelHeight) {
        setTextureVerticalSize(GuiConstants.ENUM_SIZE.RELATIVE);
        this.textureRelHeight = MathHelper.clamp(textureRelHeight, 0, Float.MAX_VALUE);

        if(getParent() != null) {
            setTextureHeight((int) (getTextureRelativeHeight() * getParent().getHeight()));
        }

        return this;
    }

    @Override
    public GuiConstants.ENUM_SIZE getTextureHorizontalSize() {
        return textureHorizontalSize;
    }
    @Override
    public ComponentStyleManager setTextureHorizontalSize(GuiConstants.ENUM_SIZE textureHorizontalSize) {
        this.textureHorizontalSize = textureHorizontalSize;
        return this;
    }

    @Override
    public GuiConstants.ENUM_SIZE getTextureVerticalSize() {
        return textureVerticalSize;
    }
    @Override
    public ComponentStyleManager setTextureVerticalSize(GuiConstants.ENUM_SIZE textureVerticalSize) {
        this.textureVerticalSize = textureVerticalSize;
        return this;
    }

    @Override
    public boolean isRepeatBackgroundX() {
        return repeatBackgroundX; //TODO USE
    }
    @Override
    public boolean isRepeatBackgroundY() {
        return repeatBackgroundY;
    }

    @Override
    public ComponentStyleManager setBorderPosition(BORDER_POSITION borderPosition) {
        this.borderPosition = borderPosition;
        return this;
    }
    @Override
    public BORDER_POSITION getBorderPosition() {
        return borderPosition;
    }

    @Override
    public int getBorderSize() {
        return borderSize;
    }
    @Override
    public ComponentStyleManager setBorderSize(CSSSimpleValueWithUnit borderSize) {
        if(borderSize.getUnit() == ECSSUnit.PERCENTAGE) {
            this.relBorderSize = (float) borderSize.getAsIntValue() / 100;
            this.borderSize = (int) (relBorderSize * getWidth());
        }
        else {
            this.relBorderSize = -1;
            this.borderSize = borderSize.getAsIntValue();
        }
        return this;
    }

    @Override
    public int getBorderColor() {
        return borderColor;
    }

    @Override
    public ComponentStyleManager setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    @Override
    @Nullable
    public ComponentStyleManager getParent() {
        return component.getParent() == null ? null : component.getParent().getStyle();
    }

    @Override
    public GuiComponent getOwner() {
        return component;
    }
}
