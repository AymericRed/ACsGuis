package fr.aym.acsguis.cssengine.style;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.style.AutoStyleHandler;
import fr.aym.acsguis.component.style.ComponentStyle;
import fr.aym.acsguis.component.style.ComponentStyleCustomizer;
import fr.aym.acsguis.component.style.InternalComponentStyle;
import fr.aym.acsguis.cssengine.parsing.core.objects.CssValue;
import fr.aym.acsguis.cssengine.selectors.CompoundCssSelector;
import fr.aym.acsguis.utils.GuiConstants;
import fr.aym.acsguis.utils.IGuiTexture;

import javax.annotation.Nullable;
import java.util.*;

public class CssComponentStyleCustomizer implements ComponentStyleCustomizer {
    private final GuiComponent component;
    private final InternalComponentStyle style;

    protected final Map<EnumCssStyleProperty, List<AutoStyleHandler<?>>> autoStyleHandlers = new HashMap<>();
    protected final Map<EnumCssStyleProperty, AutoStyleHandler.SimpleStyleFunction> styleOverrides = new HashMap<>();
    protected Map<CompoundCssSelector, Map<EnumCssStyleProperty, CssStyleProperty<?>>> customStyle;

    private final Set<EnumCssStyleProperty> changes = new HashSet<>();

    public CssComponentStyleCustomizer(GuiComponent component, InternalComponentStyle style) {
        this.component = component;
        this.style = style;
    }

    @Override
    public GuiComponent getOwner() {
        return component;
    }

    @Override
    public boolean hasChanges() {
        return !changes.isEmpty();
    }

    @Override
    public EnumCssStyleProperty[] getChanges() {
        return changes.toArray(new EnumCssStyleProperty[0]);
    }

    @Override
    public void removeChange(EnumCssStyleProperty property) {
        changes.remove(property);
    }

    public Map<EnumCssStyleProperty, AutoStyleHandler.SimpleStyleFunction> getStyleOverrides() {
        return styleOverrides;
    }

    @Override
    @Nullable
    public AutoStyleHandler.SimpleStyleFunction getStyleOverride(EnumCssStyleProperty property) {
        return styleOverrides.get(property);
    }

    @Override
    public ComponentStyleCustomizer putStyleOverride(EnumCssStyleProperty property, AutoStyleHandler.SimpleStyleFunction styleFunction) {
        styleOverrides.put(property, styleFunction);
        changes.add(property);
        return this;
    }

    @Override
    public void setCustomParsedStyle(Map<CompoundCssSelector, Map<EnumCssStyleProperty, CssStyleProperty<?>>> data) {
        this.customStyle = data;
        style.resetCssStack();
    }

    @Override
    public Map<CompoundCssSelector, Map<EnumCssStyleProperty, CssStyleProperty<?>>> getCustomParsedStyle() {
        return customStyle;
    }

    @Override
    public ComponentStyleCustomizer withAutoStyle(EnumCssStyleProperty property, AutoStyleHandler.SimpleStyleFunction styleFunction) {
        return withAutoStyles((property1, context, target) -> {
            styleFunction.apply(target);
            return true;
        }, property);
    }

    @Override
    public ComponentStyleCustomizer withAutoStyles(AutoStyleHandler<?> handler, EnumCssStyleProperty... targetProperties) {
        if (targetProperties.length == 0) {
            throw new IllegalArgumentException("At least one property is expected");
        }
        for (EnumCssStyleProperty property : targetProperties) {
            if (!autoStyleHandlers.containsKey(property)) {
                autoStyleHandlers.put(property, new ArrayList<>());
            }
            autoStyleHandlers.get(property).add(handler);
            changes.add(property);
        }
        return this;
    }

    @Override
    public ComponentStyleCustomizer removeAutoStyles(AutoStyleHandler<?> handler, EnumCssStyleProperty... targetProperties) {
        if (targetProperties.length == 0) {
            throw new IllegalArgumentException("At least one property is expected");
        }
        for (EnumCssStyleProperty property : targetProperties) {
            if (!autoStyleHandlers.containsKey(property)) {
                continue;
            }
            autoStyleHandlers.get(property).remove(handler);
            if (autoStyleHandlers.get(property).isEmpty()) {
                autoStyleHandlers.remove(property);
            }
            changes.add(property);
        }
        return this;
    }

    @Override
    @Nullable
    public List<AutoStyleHandler<?>> getAutoStyleHandlers(EnumCssStyleProperty property) {
        return autoStyleHandlers.get(property);
    }

    @Override
    public ComponentStyleCustomizer setForegroundColor(int color) {
        styleOverrides.put(EnumCssStyleProperty.COLOR, style -> style.setForegroundColor(color));
        changes.add(EnumCssStyleProperty.COLOR);
        return this;
    }

    @Override
    public ComponentStyleCustomizer setBorderRadius(CssValue radius) {
        styleOverrides.put(EnumCssStyleProperty.BORDER_RADIUS, style -> style.setBorderRadius(radius));
        changes.add(EnumCssStyleProperty.BORDER_RADIUS);
        return this;
    }

    @Override
    public ComponentStyleCustomizer setTexture(IGuiTexture texture) {
        styleOverrides.put(EnumCssStyleProperty.TEXTURE, style -> style.setTexture(texture));
        changes.add(EnumCssStyleProperty.TEXTURE);
        return this;
    }

    @Override
    public ComponentStyleCustomizer setBackgroundColor(int backgroundColor) {
        styleOverrides.put(EnumCssStyleProperty.BACKGROUND_COLOR, style -> style.setBackgroundColor(backgroundColor));
        changes.add(EnumCssStyleProperty.BACKGROUND_COLOR);
        return this;
    }

    @Override
    public ComponentStyleCustomizer setZLevel(int zLevel) {
        styleOverrides.put(EnumCssStyleProperty.Z_INDEX, style -> style.setZLevel(zLevel));
        changes.add(EnumCssStyleProperty.Z_INDEX);
        return this;
    }

    @Override
    public ComponentStyleCustomizer setRepeatBackgroundX(boolean repeatBackgroundX) {
        styleOverrides.put(EnumCssStyleProperty.BACKGROUND_REPEAT, style -> style.setRepeatBackgroundX(repeatBackgroundX));
        changes.add(EnumCssStyleProperty.BACKGROUND_REPEAT);
        return this;
    }

    @Override
    public ComponentStyleCustomizer setRepeatBackgroundY(boolean repeatBackgroundY) {
        //TODO KEY
        styleOverrides.put(EnumCssStyleProperty.BACKGROUND_REPEAT, style -> style.setRepeatBackgroundX(repeatBackgroundY));
        changes.add(EnumCssStyleProperty.BACKGROUND_REPEAT);
        return this;
    }

    @Override
    public ComponentStyleCustomizer setBorderPosition(ComponentStyle.BORDER_POSITION borderPosition) {
        styleOverrides.put(EnumCssStyleProperty.BORDER_POSITION, style -> style.setBorderPosition(borderPosition));
        changes.add(EnumCssStyleProperty.BORDER_POSITION);
        return this;
    }

    @Override
    public ComponentStyleCustomizer setBorderSize(CssValue borderSize) {
        styleOverrides.put(EnumCssStyleProperty.BORDER_WIDTH, style -> style.setBorderSize(borderSize));
        changes.add(EnumCssStyleProperty.BORDER_WIDTH);
        return this;
    }

    @Override
    public ComponentStyleCustomizer setShouldRescaleBorder(boolean inverseScreenScale) {
        //TODO KEY
        styleOverrides.put(EnumCssStyleProperty.Z_INDEX, style -> style.setShouldRescaleBorder(inverseScreenScale));
        changes.add(EnumCssStyleProperty.Z_INDEX);
        return this;
    }

    @Override
    public ComponentStyleCustomizer setBorderColor(int borderColor) {
        styleOverrides.put(EnumCssStyleProperty.BORDER_COLOR, style -> style.setBorderColor(borderColor));
        changes.add(EnumCssStyleProperty.BORDER_COLOR);
        return this;
    }

    @Override
    public ComponentStyleCustomizer setVisible(boolean enabled) {
        styleOverrides.put(EnumCssStyleProperty.VISIBILITY, style -> style.setVisible(enabled));
        changes.add(EnumCssStyleProperty.VISIBILITY);
        return this;
    }

    @Override
    public ComponentStyleCustomizer setTextureVerticalSize(GuiConstants.ENUM_SIZE textureVerticalSize) {
        //TODO KEY
        styleOverrides.put(EnumCssStyleProperty.Z_INDEX, style -> style.setTextureVerticalSize(textureVerticalSize));
        changes.add(EnumCssStyleProperty.Z_INDEX);
        return this;
    }

    @Override
    public ComponentStyleCustomizer setTextureHorizontalSize(GuiConstants.ENUM_SIZE textureHorizontalSize) {
        //TODO KEY
        styleOverrides.put(EnumCssStyleProperty.Z_INDEX, style -> style.setTextureHorizontalSize(textureHorizontalSize));
        changes.add(EnumCssStyleProperty.Z_INDEX);
        return this;
    }

    @Override
    public ComponentStyleCustomizer setTextureHeight(int textureHeight) {
        //TODO KEY
        styleOverrides.put(EnumCssStyleProperty.Z_INDEX, style -> style.setTextureHeight(textureHeight));
        changes.add(EnumCssStyleProperty.Z_INDEX);
        return this;
    }

    @Override
    public ComponentStyleCustomizer setTextureWidth(int textureWidth) {
        //TODO KEY
        styleOverrides.put(EnumCssStyleProperty.Z_INDEX, style -> style.setTextureWidth(textureWidth));
        changes.add(EnumCssStyleProperty.Z_INDEX);
        return this;
    }

    @Override
    public ComponentStyleCustomizer setXPos(float x) {
        styleOverrides.put(EnumCssStyleProperty.LEFT, style -> style.getXPos().setAbsolute(x));
        changes.add(EnumCssStyleProperty.LEFT);
        return this;
    }

    @Override
    public ComponentStyleCustomizer setYPos(float y) {
        styleOverrides.put(EnumCssStyleProperty.TOP, style -> style.getYPos().setAbsolute(y));
        changes.add(EnumCssStyleProperty.TOP);
        return this;
    }

    @Override
    public ComponentStyleCustomizer setPosition(float x, float y) {
        return setXPos(x).setYPos(y);
    }

    @Override
    public ComponentStyleCustomizer setRelativeX(float relativeX, CssValue.Unit unit) {
        styleOverrides.put(EnumCssStyleProperty.LEFT, style -> style.getXPos().setRelative(relativeX, unit));
        changes.add(EnumCssStyleProperty.LEFT);
        return this;
    }

    @Override
    public ComponentStyleCustomizer setRelativeY(float relativeY, CssValue.Unit unit) {
        styleOverrides.put(EnumCssStyleProperty.TOP, style -> style.getYPos().setRelative(relativeY, unit));
        changes.add(EnumCssStyleProperty.TOP);
        return this;
    }

    @Override
    public ComponentStyleCustomizer setRelativePosition(float relativeX, float relativeY, CssValue.Unit unit) {
        return setRelativeX(relativeX, unit).setRelativeY(relativeY, unit);
    }

    @Override
    public ComponentStyleCustomizer setXOrigin(GuiConstants.ENUM_RELATIVE_POS xOrigin) {
        //TODO KEY
        styleOverrides.put(EnumCssStyleProperty.RIGHT, style -> {
            switch (style.getXPos().type()) {
                case ABSOLUTE:
                    style.getXPos().setAbsolute(style.getXPos().getRawValue(), xOrigin);
                    break;
                case RELATIVE:
                    style.getXPos().setRelative(style.getXPos().getRawValue(), CssValue.Unit.RELATIVE_TO_PARENT, xOrigin);
                    break;
                case RELATIVE_VW:
                    style.getXPos().setRelative(style.getXPos().getRawValue(), CssValue.Unit.RELATIVE_TO_WINDOW_WIDTH, xOrigin);
                    break;
                case RELATIVE_VH:
                    style.getXPos().setRelative(style.getXPos().getRawValue(), CssValue.Unit.RELATIVE_TO_WINDOW_HEIGHT, xOrigin);
                    break;
            }
        });
        changes.add(EnumCssStyleProperty.RIGHT);
        return this;
    }

    @Override
    public ComponentStyleCustomizer setYOrigin(GuiConstants.ENUM_RELATIVE_POS yOrigin) {
        //TODO KEY
        styleOverrides.put(EnumCssStyleProperty.BOTTOM, style -> {
            switch (style.getYPos().type()) {
                case ABSOLUTE:
                    style.getYPos().setAbsolute(style.getYPos().getRawValue(), yOrigin);
                    break;
                case RELATIVE:
                    style.getYPos().setRelative(style.getYPos().getRawValue(), CssValue.Unit.RELATIVE_TO_PARENT, yOrigin);
                    break;
                case RELATIVE_VW:
                    style.getYPos().setRelative(style.getYPos().getRawValue(), CssValue.Unit.RELATIVE_TO_WINDOW_WIDTH, yOrigin);
                    break;
                case RELATIVE_VH:
                    style.getYPos().setRelative(style.getYPos().getRawValue(), CssValue.Unit.RELATIVE_TO_WINDOW_HEIGHT, yOrigin);
                    break;
            }
        });
        changes.add(EnumCssStyleProperty.BOTTOM);
        return this;
    }

    @Override
    public ComponentStyleCustomizer setWidth(float width) {
        styleOverrides.put(EnumCssStyleProperty.WIDTH, style -> style.getWidth().setAbsolute(width));
        changes.add(EnumCssStyleProperty.WIDTH);
        return this;
    }

    @Override
    public ComponentStyleCustomizer setHeight(float height) {
        styleOverrides.put(EnumCssStyleProperty.HEIGHT, style -> style.getHeight().setAbsolute(height));
        changes.add(EnumCssStyleProperty.HEIGHT);
        return this;
    }

    @Override
    public ComponentStyleCustomizer setSize(float width, float height) {
        return setWidth(width).setHeight(height);
    }

    @Override
    public ComponentStyleCustomizer setRelativeWidth(float relativeWidth, CssValue.Unit unit) {
        styleOverrides.put(EnumCssStyleProperty.WIDTH, style -> style.getWidth().setRelative(relativeWidth, unit));
        changes.add(EnumCssStyleProperty.WIDTH);
        return this;
    }

    @Override
    public ComponentStyleCustomizer setRelativeHeight(float relativeHeight, CssValue.Unit unit) {
        styleOverrides.put(EnumCssStyleProperty.HEIGHT, style -> style.getHeight().setRelative(relativeHeight, unit));
        changes.add(EnumCssStyleProperty.HEIGHT);
        return this;
    }

    @Override
    public ComponentStyleCustomizer setRelativeSize(float relativeWidth, float relativeHeight, CssValue.Unit unit) {
        return setRelativeWidth(relativeWidth, unit).setRelativeHeight(relativeHeight, unit);
    }

    @Override
    public ComponentStyleCustomizer clearProperty(EnumCssStyleProperty property) {
        if (styleOverrides.remove(property) != null) {
            changes.add(property);
        }
        return this;
    }
}
