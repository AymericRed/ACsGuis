package fr.aym.acsguis.cssengine.style;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.panel.GuiScrollPane;
import fr.aym.acsguis.component.style.ComponentStyle;
import fr.aym.acsguis.component.style.ComponentStyleCustomizer;
import fr.aym.acsguis.component.style.InternalComponentStyle;
import fr.aym.acsguis.component.textarea.IChildSizeUpdateListener;
import fr.aym.acsguis.cssengine.parsing.ACsGuisCssParser;
import fr.aym.acsguis.cssengine.parsing.core.objects.CssValue;
import fr.aym.acsguis.cssengine.positionning.Position;
import fr.aym.acsguis.cssengine.positionning.Size;
import fr.aym.acsguis.cssengine.selectors.CssStackElement;
import fr.aym.acsguis.cssengine.selectors.EnumSelectorContext;
import fr.aym.acsguis.utils.GuiConstants;
import fr.aym.acsguis.utils.IGuiTexture;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.function.Function;

public class CssComponentStyle implements InternalComponentStyle {
    private final GuiComponent component;
    private final ComponentStyleCustomizer styleCustomizer;

    protected final Position xPos = new Position(0, GuiConstants.ENUM_POSITION.ABSOLUTE, GuiConstants.ENUM_RELATIVE_POS.START);
    protected final Position yPos = new Position(0, GuiConstants.ENUM_POSITION.ABSOLUTE, GuiConstants.ENUM_RELATIVE_POS.START);

    protected float computedX;
    protected float computedY;

    protected final Size width = new Size();
    protected final Size height = new Size();

    protected float computedWidth;
    protected float computedHeight;

    /**
     * Offset values of the component, used for GuiScrollPane for example
     **/
    protected int offsetX, offsetY;

    /**
     * The render zLevel, use to sort the render pipeline, by default the components
     * are rendered in the ordered in which they had been added to their parent.
     **/
    protected int zLevel = 0;

    protected boolean visible = true;

    private int foregroundColor = Color.WHITE.getRGB();
    private int backgroundColor = Color.TRANSLUCENT;

    protected boolean repeatBackgroundX = false, repeatBackgroundY = false;

    protected float relBorderSize = -1, relBorderRadius = -1;
    protected float borderSize = 0, borderRadius;
    protected boolean rescaleBorder;
    protected int borderColor = Color.DARK_GRAY.getRGB();

    protected BORDER_POSITION borderPosition = BORDER_POSITION.EXTERNAL;

    protected IGuiTexture texture;
    protected int textureWidth, textureHeight;
    protected float textureRelWidth = 1, textureRelHeight = 1;

    protected GuiConstants.ENUM_SIZE textureHorizontalSize = GuiConstants.ENUM_SIZE.ABSOLUTE;
    protected GuiConstants.ENUM_SIZE textureVerticalSize = GuiConstants.ENUM_SIZE.ABSOLUTE;

    protected int paddingLeft = 0, paddingRight = 0, paddingTop = 0, paddingBottom = 0;

    protected GuiConstants.COMPONENT_DISPLAY display = GuiConstants.COMPONENT_DISPLAY.BLOCK;

    protected CssStackElement cssStack;

    private EnumSelectorContext lastContext = EnumSelectorContext.NORMAL;

    public CssComponentStyle(GuiComponent component) {
        this.component = component;
        this.styleCustomizer = new CssComponentStyleCustomizer(component, this);
    }

    public CssComponentStyle(GuiComponent component, Function<CssComponentStyle, ComponentStyleCustomizer> styleCustomizerFactory) {
        this.component = component;
        this.styleCustomizer = styleCustomizerFactory.apply(this);
    }

    @Nullable
    public CssStackElement getCssStack() {
        return cssStack;
    }

    @Override
    public ComponentStyleCustomizer getCustomizer() {
        return styleCustomizer;
    }

    @Override
    public void update(GuiFrame.APIGuiScreen gui) {
        if (component.getState() != lastContext || cssStack == null) {
            refreshStyle(getOwner().getGui());
        } else if (styleCustomizer.hasChanges()) {
            refreshStyle(gui, styleCustomizer.getChanges());
        }
    }

    @Override
    public void reloadCssStack() {
        cssStack = ACsGuisCssParser.getStyleFor(this);
    }

    @Override
    public void resetCssStack() {
        cssStack = null;
    }

    @Override //reload css
    public void refreshStyle(GuiFrame.APIGuiScreen gui, EnumCssStyleProperty... properties) {
        if (cssStack == null && (getParent() == null || getParent().getCssStack() != null)) {
            reloadCssStack();
        }
        if (cssStack == null) {
            return;
        }
        //Anticipate and apply the new context now
        lastContext = component.getState();

        //update
        cssStack.applyProperties(getContext(), this, properties);
        int sx = gui != null ? (int) (gui.getFrame().getResolution().getScaledWidth() / gui.getScaleX()) : 1;
        int sy = gui != null ? (int) (gui.getFrame().getResolution().getScaledHeight() / gui.getScaleY()) : 1;
        updateComponentSize(sx, sy);
        updateComponentPosition(sx, sy);

        //refresh children
        if (!(component instanceof GuiPanel)) {
            return;
        }
        for (GuiComponent c : ((GuiPanel) component).getChildComponents()) {
            if (!((GuiPanel) component).getToRemoveComponents().contains(c)) {
                if (c.getStyle().getCssStack() == null) {
                    c.getStyle().reloadCssStack();
                }
                c.getStyle().refreshStyle(getOwner().getGui(), properties);
            }
        }
        //TODO PAS OUF
        if (component instanceof GuiScrollPane) {
            ((GuiScrollPane) component).updateSlidersVisibility();
        }
    }

    @Override
    public EnumSelectorContext getContext() {
        return lastContext;
    }

    @Override
    public InternalComponentStyle setForegroundColor(int color) {
        this.foregroundColor = color;
        return this;
    }

    @Override
    public int getForegroundColor() {
        return foregroundColor;
    }

    @Override
    public InternalComponentStyle setBorderRadius(CssValue radius) {
        if (radius.getUnit() == CssValue.Unit.RELATIVE_TO_PARENT) {
            this.relBorderRadius = (float) radius.intValue() / 100;
            this.borderRadius = (int) (relBorderRadius * getRenderWidth());
        } else {
            this.relBorderRadius = -1;
            this.borderRadius = radius.intValue();
        }
        return this;
    }

    @Override
    public float getBorderRadius() {
        return borderRadius;
    }

    /**
     * Updates component size, sliders and borders...
     *
     * @param screenWidth  scaled mc screen with
     * @param screenHeight scaled mc screen height
     */
    public void updateComponentSize(int screenWidth, int screenHeight) {
        float parentWidth = component.getParent() != null ? component.getParent().getWidth() : screenWidth;
        computedWidth = width.computeValue(screenWidth, screenHeight, parentWidth);

        float parentHeight = component.getParent() != null ? component.getParent().getHeight() : screenHeight;
        computedHeight = height.computeValue(screenWidth, screenHeight, parentHeight);
     //   System.out.println("Set height " + computedHeight + " on " + component + " " + component.hashCode());

        if (relBorderSize != -1)
            this.borderSize = (int) (relBorderSize * getRenderWidth());
        if (relBorderRadius != -1)
            this.borderRadius = (int) (relBorderRadius * getRenderWidth());

        if (component.getParent() instanceof IChildSizeUpdateListener) {
            ((IChildSizeUpdateListener) component.getParent()).onComponentChildSizeUpdate();
        }

        if (getTextureHorizontalSize() == GuiConstants.ENUM_SIZE.RELATIVE) {
            setTextureWidth((int) (getRenderWidth() * getTextureRelativeWidth()));
        }

        if (getTextureVerticalSize() == GuiConstants.ENUM_SIZE.RELATIVE) {
            setTextureHeight((int) (getRenderHeight() * getTextureRelativeHeight()));
        }

        //refresh children
        /*if (component instanceof GuiPanel) {
            for (GuiComponent c : ((GuiPanel) component).getChildComponents()) {
                if (!((GuiPanel) component).getToRemoveComponents().contains(c)) {
                    ((CssComponentStyle) c.getStyle()).updateComponentSize(screenWidth, screenHeight);
                    ((CssComponentStyle) c.getStyle()).updateComponentPosition(screenWidth, screenHeight);
                }
            }
        }*/
    }

    /**
     * Update the x and y coordinates
     *
     * @param screenWidth  scaled mc screen with
     * @param screenHeight scaled mc screen height
     */
    public void updateComponentPosition(int screenWidth, int screenHeight) {
        float parentWidth = component.getParent() != null ? component.getParent().getWidth() : screenWidth;
        float parentHeight = component.getParent() != null ? component.getParent().getHeight() : screenHeight;

        //.out.println("Compute "+getOwner()+" x and from "+computedX);
        computedX = getXPos().computeValue(this, screenWidth, screenHeight, parentWidth, getRenderWidth());
        //System.out.println("Got "+computedX);
        //System.out.println("Compute "+getOwner()+" y and from "+computedY);
        computedY = getYPos().computeValue(this, screenWidth, screenHeight, parentHeight, getRenderHeight());
        //System.out.println("Got "+computedY);
    }

    @Override
    public IGuiTexture getTexture() {
        return texture;
    }

    @Override
    public void resize(GuiFrame.APIGuiScreen gui) {
        if (getCssStack() != null) {
            refreshStyle(gui);
        }
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public InternalComponentStyle setVisible(boolean visible) {
        this.visible = visible;
        if (!visible) {
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
    public float getRenderX() {
        return computedX;
    }

    @Override
    public float getRenderY() {
        return computedY;
    }

    @Override
    public Size getWidth() {
        return width;
    }

    @Override
    public Size getHeight() {
        return height;
    }

    @Override
    public GuiConstants.COMPONENT_DISPLAY getDisplay() {
        return display;
    }

    @Override
    public InternalComponentStyle setDisplay(GuiConstants.COMPONENT_DISPLAY componentDisplay) {
        this.display = componentDisplay;
        return this;
    }

    @Override
    public float getRenderWidth() {
        return computedWidth;
    }

    @Override
    public float getRenderHeight() {
        return computedHeight;
    }

    @Override
    public int getOffsetX() {
        return offsetX;
    }

    @Override
    public ComponentStyle setOffsetX(int offsetX) {
        this.offsetX = offsetX;
        return this;
    }

    @Override
    public int getOffsetY() {
        return offsetY;
    }

    @Override
    public ComponentStyle setOffsetY(int offsetY) {
        this.offsetY = offsetY;
        return this;
    }

    @Override
    public int getZLevel() {
        return zLevel;
    }

    @Override
    public InternalComponentStyle setZLevel(int zLevel) {
        this.zLevel = zLevel;
        return this;
    }

    @Override
    public int getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public InternalComponentStyle setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    @Override
    public InternalComponentStyle setRepeatBackgroundX(boolean repeatBackgroundX) {
        this.repeatBackgroundX = repeatBackgroundX;
        return this;
    }

    @Override
    public InternalComponentStyle setRepeatBackgroundY(boolean repeatBackgroundY) {
        this.repeatBackgroundY = repeatBackgroundY;
        return this;
    }

    @Override
    public InternalComponentStyle setTexture(IGuiTexture texture) {
        this.texture = texture;
        if (texture != null) {
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
    public InternalComponentStyle setTextureWidth(int textureWidth) {
        this.textureWidth = textureWidth;
        return this;
    }

    @Override
    public int getTextureHeight() {
        return textureHeight;
    }

    @Override
    public InternalComponentStyle setTextureHeight(int textureHeight) {
        this.textureHeight = textureHeight;
        return this;
    }

    //TODO WHAT TO DO WITH THIS ??
    public float getTextureRelativeWidth() {
        return textureRelWidth;
    }

    public InternalComponentStyle setTextureRelativeWidth(float textureRelWidth) {
        setTextureHorizontalSize(GuiConstants.ENUM_SIZE.RELATIVE);
        this.textureRelWidth = MathHelper.clamp(textureRelWidth, 0, Float.MAX_VALUE);

        if (getParent() != null) {
            setTextureWidth((int) (getTextureRelativeWidth() * getParent().getRenderWidth()));
        }

        return this;
    }

    public float getTextureRelativeHeight() {
        return textureRelHeight;
    }

    public InternalComponentStyle setTextureRelativeHeight(float textureRelHeight) {
        setTextureVerticalSize(GuiConstants.ENUM_SIZE.RELATIVE);
        this.textureRelHeight = MathHelper.clamp(textureRelHeight, 0, Float.MAX_VALUE);

        if (getParent() != null) {
            setTextureHeight((int) (getTextureRelativeHeight() * getParent().getRenderHeight()));
        }

        return this;
    }

    @Override
    public GuiConstants.ENUM_SIZE getTextureHorizontalSize() {
        return textureHorizontalSize;
    }

    @Override
    public InternalComponentStyle setTextureHorizontalSize(GuiConstants.ENUM_SIZE textureHorizontalSize) {
        this.textureHorizontalSize = textureHorizontalSize;
        return this;
    }

    @Override
    public GuiConstants.ENUM_SIZE getTextureVerticalSize() {
        return textureVerticalSize;
    }

    @Override
    public InternalComponentStyle setTextureVerticalSize(GuiConstants.ENUM_SIZE textureVerticalSize) {
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
    public InternalComponentStyle setBorderPosition(BORDER_POSITION borderPosition) {
        this.borderPosition = borderPosition;
        return this;
    }

    @Override
    public BORDER_POSITION getBorderPosition() {
        return borderPosition;
    }

    @Override
    public float getBorderSize() {
        return borderSize;
    }

    @Override
    public InternalComponentStyle setBorderSize(CssValue borderSize) {
        if (borderSize.getUnit() == CssValue.Unit.RELATIVE_TO_PARENT) {
            this.relBorderSize = (float) borderSize.intValue() / 100;
            this.borderSize = (int) (relBorderSize * getRenderWidth());
        } else {
            this.relBorderSize = -1;
            this.borderSize = borderSize.intValue();
        }
        return this;
    }

    @Override
    public boolean shouldRescaleBorder() {
        return rescaleBorder;
    }

    @Override
    public InternalComponentStyle setShouldRescaleBorder(boolean inverseScreenScale) {
        this.rescaleBorder = inverseScreenScale;
        return this;
    }

    @Override
    public int getBorderColor() {
        return borderColor;
    }

    @Override
    public InternalComponentStyle setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    @Override
    public int getPaddingTop() {
        return paddingTop;
    }

    @Override
    public int getPaddingBottom() {
        return paddingBottom;
    }

    @Override
    public int getPaddingLeft() {
        return paddingLeft;
    }

    @Override
    public int getPaddingRight() {
        return paddingRight;
    }

    @Override
    public InternalComponentStyle setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
        return this;
    }

    @Override
    public InternalComponentStyle setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
        return this;
    }

    @Override
    public InternalComponentStyle setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
        return this;
    }

    @Override
    public InternalComponentStyle setPaddingRight(int paddingRight) {
        this.paddingRight = paddingRight;
        return this;
    }

    @Override
    @Nullable
    public ComponentStyle getParent() {
        return component.getParent() == null ? null : component.getParent().getStyle();
    }

    @Override
    public GuiComponent getOwner() {
        return component;
    }
}
