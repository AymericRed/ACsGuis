package fr.aym.acsguis.cssengine.style;

import fr.aym.acsguis.component.style.ComponentStyle;
import fr.aym.acsguis.component.style.PanelStyle;
import fr.aym.acsguis.component.style.TextComponentStyle;
import fr.aym.acsguis.component.textarea.GuiProgressBar;
import fr.aym.acsguis.cssengine.CssHelper;
import fr.aym.acsguis.cssengine.DefinitionType;
import fr.aym.acsguis.utils.GuiConstants;
import net.minecraft.util.text.TextFormatting;
import org.newdawn.slick.font.effects.ShadowEffect;

import java.awt.*;

import static fr.aym.acsguis.cssengine.parsing.core.objects.CssValue.Unit.ABSOLUTE_INT;
import static fr.aym.acsguis.cssengine.parsing.core.objects.CssValue.Unit.RELATIVE_TO_PARENT;

public enum EnumCssStyleProperty {
    BACKGROUND_COLOR(CssHelper.COLOR, (p, c) -> c.setBackgroundColor(p.getValue()), "background-color", false, false, true),
    COLOR(CssHelper.COLOR, (p, c) -> c.setForegroundColor(p.getValue()), "color", false, false, true),
    BORDER_COLOR(CssHelper.COLOR, (p, c) -> c.setBorderColor(p.getValue()), "border-color"),
    TEXTURE(CssHelper.TEXTURE_SPRITE, (p, c) -> c.setTexture(p.getValue()), "background-image", false, true),
    VISIBILITY(CssHelper.STRING, (p, c) -> c.setVisible(!p.getValue().equals("hidden")), "visibility"),
    FONT_SIZE(CssHelper.CSS_INT, (p, c) -> {
        if (c instanceof TextComponentStyle.InternalStyle) {
            ((TextComponentStyle.InternalStyle) c).setFontSize(p.getValue().intValue());
        }
    }, "font-size"),
    FONT_STYLE(CssHelper.STRING, (p, c) -> {
        if (c instanceof TextComponentStyle.InternalStyle) {
            switch (p.getValue()) {
                case "italic":
                    ((TextComponentStyle.InternalStyle) c).setFontStyle(TextFormatting.ITALIC);
                    break;
                case "normal":
                    ((TextComponentStyle.InternalStyle) c).setFontStyle(null);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported font style " + p.getValue());
            }
        }
    }, "font-style"),
    FONT_FAMILY(CssHelper.RESOURCE_LOCATION, (p, c) -> {
        if (c instanceof TextComponentStyle.InternalStyle) {
            ((TextComponentStyle.InternalStyle) c).setFontFamily(p.getValue());
        }
    }, "font-family", false, false, true),
    BORDER_WIDTH(CssHelper.CSS_INT, (p, c) -> c.setBorderSize(p.getValue()), "border-width"),
    BORDER_STYLE(CssHelper.STRING, (p, c) -> {
        if (!p.getValue().equals("solid")) {
            throw new IllegalArgumentException("Border style " + p.getValue() + " is not supported !");
        }
    }, "border-style"),
    BORDER_POSITION(CssHelper.STRING, (p, c) -> {
        if (p.getValue().equals("internal")) {
            c.setBorderPosition(ComponentStyle.BORDER_POSITION.INTERNAL);
        } else {
            c.setBorderPosition(ComponentStyle.BORDER_POSITION.EXTERNAL);
        }
    }, "border-position", false, false, true),
    BACKGROUND_REPEAT(CssHelper.STRING, (p, c) -> {
        switch (p.getValue()) {
            case "repeat":
                c.setRepeatBackgroundX(true);
                c.setRepeatBackgroundY(true);
                break;
            case "repeat-x":
                c.setRepeatBackgroundX(true);
                c.setRepeatBackgroundY(false);
                break;
            case "repeat-y":
                c.setRepeatBackgroundX(false);
                c.setRepeatBackgroundY(true);
                break;
            case "no-repeat":
            case "none":
                c.setRepeatBackgroundX(false);
                c.setRepeatBackgroundY(false);
                break;
            default:
                throw new IllegalArgumentException("Background repeat " + p.getValue() + " is not supported !");
        }
    }, "background-repeat"),
    BACKGROUND_POSITION(CssHelper.STRING, (p, c) -> {
        if (!p.getValue().equals("top left")) {
            throw new IllegalArgumentException("Background position property is not supported ! It should be defined in the texture property");
        }
    }, "background-position"),
    BACKGROUND_ATTACHMENT(CssHelper.STRING, (p, c) -> {/* ignore */}, "background-attachment"),
    BACKGROUND_ORIGIN(CssHelper.STRING, (p, c) -> {/* ignore */}, "background-origin"),
    BACKGROUND_SIZE(CssHelper.STRING, (p, c) -> {
        if (!p.getValue().equals("auto auto")) {
            throw new IllegalArgumentException("Background size property is not supported ! It should be defined in the texture property");
        }
    }, "background-size"),
    BACKGROUND_CLIP(CssHelper.STRING, (p, c) -> {/* ignore */}, "background-clip"),
    BORDER_RADIUS(CssHelper.CSS_INT, (p, c) -> c.setBorderRadius(p.getValue()), "border-radius"),
    TEXT_SHADOW(CssHelper.STRING, (p, c) -> {
        if (c instanceof TextComponentStyle.InternalStyle) {
            if (p.getValue().equals("enable")) { //TODO IMPROVE SHADOWS
                ShadowEffect effect = new ShadowEffect();
                effect.setColor(Color.BLACK);
                ((TextComponentStyle.InternalStyle) c).addEffect(effect);
            } else {
                ((TextComponentStyle.InternalStyle) c).removeEffect(ShadowEffect.class);
            }
        }
    }, "text-shadow"),
    Z_INDEX(CssHelper.INT, (p, c) -> c.setZLevel(p.getValue()), "z-index"),
    PADDING_LEFT(CssHelper.CSS_INT, (p, c) -> {
        if (p.getValue().getUnit() != ABSOLUTE_INT) {
            c.setPaddingLeft((int) (p.getValue().intValue() * c.getRenderWidth() / 100f));
        } else {
            c.setPaddingLeft(p.getValue().intValue());
        }
    }, "padding-left"),
    PADDING_TOP(CssHelper.CSS_INT, (p, c) -> {
        if (p.getValue().getUnit() != ABSOLUTE_INT) {
            c.setPaddingTop((int) (p.getValue().intValue() * c.getRenderWidth() / 100f));
        } else { //FIXME IMPROVE RELATIVE THINGS
            c.setPaddingTop(p.getValue().intValue());
        }
    }, "padding-top"),
    PADDING_RIGHT(CssHelper.CSS_INT, (p, c) -> {
        if (p.getValue().getUnit() != ABSOLUTE_INT) {
            c.setPaddingRight((int) (p.getValue().intValue() * c.getRenderWidth() / 100f));
        } else {
            c.setPaddingRight(p.getValue().intValue());
        }
    }, "padding-right"),
    PADDING_BOTTOM(CssHelper.CSS_INT, (p, c) -> {
        if (p.getValue().getUnit() != ABSOLUTE_INT) {
            c.setPaddingBottom((int) (p.getValue().intValue() * c.getRenderWidth() / 100f));
        } else {
            c.setPaddingBottom(p.getValue().intValue());
        }
    }, "padding-bottom"),
    TEXT_ALIGN_HORIZONTAL(CssHelper.STRING, (p, c) -> {
        if (c instanceof TextComponentStyle.InternalStyle) {
            switch (p.getValue()) {
                case "left":
                    ((TextComponentStyle.InternalStyle) c).setHorizontalTextAlignment(GuiConstants.HORIZONTAL_TEXT_ALIGNMENT.LEFT);
                    break;
                case "right":
                    ((TextComponentStyle.InternalStyle) c).setHorizontalTextAlignment(GuiConstants.HORIZONTAL_TEXT_ALIGNMENT.RIGHT);
                    break;
                case "center":
                    ((TextComponentStyle.InternalStyle) c).setHorizontalTextAlignment(GuiConstants.HORIZONTAL_TEXT_ALIGNMENT.CENTER);
                    break;
                case "justify":
                    ((TextComponentStyle.InternalStyle) c).setHorizontalTextAlignment(GuiConstants.HORIZONTAL_TEXT_ALIGNMENT.JUSTIFY);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown (horizontal) text align : " + p.getValue());
            }
        } else if (c instanceof GuiProgressBar.ProgressBarStyle) {
            switch (p.getValue()) {
                case "left":
                    ((GuiProgressBar.ProgressBarStyle) c).setHorizontalTextAlignment(GuiConstants.HORIZONTAL_TEXT_ALIGNMENT.LEFT);
                    break;
                case "right":
                    ((GuiProgressBar.ProgressBarStyle) c).setHorizontalTextAlignment(GuiConstants.HORIZONTAL_TEXT_ALIGNMENT.RIGHT);
                    break;
                case "center":
                    ((GuiProgressBar.ProgressBarStyle) c).setHorizontalTextAlignment(GuiConstants.HORIZONTAL_TEXT_ALIGNMENT.CENTER);
                    break;
                case "justify":
                    ((GuiProgressBar.ProgressBarStyle) c).setHorizontalTextAlignment(GuiConstants.HORIZONTAL_TEXT_ALIGNMENT.JUSTIFY);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown (horizontal) text align : " + p.getValue());
            }
        }
    }, "text-align"),
    TEXT_ALIGN_VERTICAL(CssHelper.STRING, (p, c) -> {
        if (c instanceof TextComponentStyle.InternalStyle) {
            switch (p.getValue()) {
                case "top":
                    ((TextComponentStyle.InternalStyle) c).setVerticalTextAlignment(GuiConstants.VERTICAL_TEXT_ALIGNMENT.TOP);
                    break;
                case "bottom":
                    ((TextComponentStyle.InternalStyle) c).setVerticalTextAlignment(GuiConstants.VERTICAL_TEXT_ALIGNMENT.BOTTOM);
                    break;
                case "center":
                    ((TextComponentStyle.InternalStyle) c).setVerticalTextAlignment(GuiConstants.VERTICAL_TEXT_ALIGNMENT.CENTER);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown vertical text align : " + p.getValue());
            }
        } else if (c instanceof GuiProgressBar.ProgressBarStyle) {
            switch (p.getValue()) {
                case "top":
                    ((GuiProgressBar.ProgressBarStyle) c).setVerticalTextAlignment(GuiConstants.VERTICAL_TEXT_ALIGNMENT.TOP);
                    break;
                case "bottom":
                    ((GuiProgressBar.ProgressBarStyle) c).setVerticalTextAlignment(GuiConstants.VERTICAL_TEXT_ALIGNMENT.BOTTOM);
                    break;
                case "center":
                    ((GuiProgressBar.ProgressBarStyle) c).setVerticalTextAlignment(GuiConstants.VERTICAL_TEXT_ALIGNMENT.CENTER);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown vertical text align : " + p.getValue());
            }
        }
    }, "text-align-vertical"),
    PANEL_LAYOUT(CssHelper.PANEL_LAYOUT, (p, c) -> {
        if (c instanceof PanelStyle) {
            ((PanelStyle) c).setLayout(p.getValue());
        }
    }, "component-layout", false, true),
    //NOTE : for the positioning, the order is important ! And other properties as padding must have been set before for labels
    WIDTH(CssHelper.CSS_INT, (p, c) -> {
        //System.out.println("Set width of "+c.getOwner()+" to "+p.getValue());
        if (p.getValue().getUnit() != ABSOLUTE_INT) {
            c.getWidth().setRelative(p.getValue().intValue() / 100f, p.getValue().getUnit());
        } else {
            c.getWidth().setAbsolute(p.getValue().intValue());
        }
    }, "width", true),
    MAX_WIDTH(CssHelper.CSS_INT, (p, c) -> {
        if (p.getValue().getUnit() != ABSOLUTE_INT) {
            c.getWidth().getMaxValue().setRelative(p.getValue().intValue() / 100f, p.getValue().getUnit());
        } else {
            c.getWidth().getMaxValue().setAbsolute(p.getValue().intValue());
        }
    }, "max-width"),
    MIN_WIDTH(CssHelper.CSS_INT, (p, c) -> {
        if (p.getValue().getUnit() != ABSOLUTE_INT) {
            c.getWidth().getMinValue().setRelative(p.getValue().intValue() / 100f, p.getValue().getUnit());
        } else {
            c.getWidth().getMinValue().setAbsolute(p.getValue().intValue());
        }
    }, "min-width"),
    HEIGHT(CssHelper.CSS_INT, (p, c) -> {
        if (p.getValue().getUnit() != ABSOLUTE_INT) {
            c.getHeight().setRelative(p.getValue().intValue() / 100f, p.getValue().getUnit());
        } else {
            c.getHeight().setAbsolute(p.getValue().intValue());
        }
    }, "height", true),
    MAX_HEIGHT(CssHelper.CSS_INT, (p, c) -> {
        if (p.getValue().getUnit() != ABSOLUTE_INT) {
            c.getHeight().getMaxValue().setRelative(p.getValue().intValue() / 100f, p.getValue().getUnit());
        } else {
            c.getHeight().getMaxValue().setAbsolute(p.getValue().intValue());
        }
    }, "max-height"),
    MIN_HEIGHT(CssHelper.CSS_INT, (p, c) -> {
        if (p.getValue().getUnit() != ABSOLUTE_INT) {
            c.getHeight().getMinValue().setRelative(p.getValue().intValue() / 100f, p.getValue().getUnit());
        } else {
            c.getHeight().getMinValue().setAbsolute(p.getValue().intValue());
        }
    }, "min-height"),
    LEFT(CssHelper.CSS_INT, (p, c) -> {
        if (p.getValue().getUnit() != ABSOLUTE_INT) {
            c.getXPos().setRelative(p.getValue().intValue() / 100f, p.getValue().getUnit(), GuiConstants.ENUM_RELATIVE_POS.START);
        } else {
            c.getXPos().setAbsolute(p.getValue().intValue(), GuiConstants.ENUM_RELATIVE_POS.START);
        }
    }, "left", true),
    RIGHT(CssHelper.CSS_INT, (p, c) -> {
        if (p.getValue().getUnit() != ABSOLUTE_INT) {
            c.getXPos().setRelative(p.getValue().intValue() / 100f, p.getValue().getUnit(), GuiConstants.ENUM_RELATIVE_POS.END);
        } else {
            c.getXPos().setAbsolute(p.getValue().intValue(), GuiConstants.ENUM_RELATIVE_POS.END);
        }
    }, "right", true),
    TOP(CssHelper.CSS_INT, (p, c) -> {
        if (p.getValue().getUnit() != ABSOLUTE_INT) {
            c.getYPos().setRelative(p.getValue().intValue() / 100f, p.getValue().getUnit(), GuiConstants.ENUM_RELATIVE_POS.START);
        } else {
            c.getYPos().setAbsolute(p.getValue().intValue(), GuiConstants.ENUM_RELATIVE_POS.START);
        }
    }, "top", true),
    BOTTOM(CssHelper.CSS_INT, (p, c) -> {
        if (p.getValue().getUnit() != ABSOLUTE_INT) {
            c.getYPos().setRelative(p.getValue().intValue() / 100f, p.getValue().getUnit(), GuiConstants.ENUM_RELATIVE_POS.END);
        } else {
            c.getYPos().setAbsolute(p.getValue().intValue(), GuiConstants.ENUM_RELATIVE_POS.END);
        }
    }, "bottom", true),
    HORIZONTAL_POSITION(CssHelper.STRING, (p, c) -> {
        if (p.getValue().equals("center")) {
            c.getXPos().setRelative(0, RELATIVE_TO_PARENT, GuiConstants.ENUM_RELATIVE_POS.CENTER);
        }
    }, "horizontal-position"),
    VERTICAL_POSITION(CssHelper.STRING, (p, c) -> {
        if (p.getValue().equals("center")) {
            c.getYPos().setRelative(0, RELATIVE_TO_PARENT, GuiConstants.ENUM_RELATIVE_POS.CENTER);
        }
    }, "vertical-position"),
    PROGRESS_FULL(CssHelper.TEXTURE_SPRITE, (p, c) -> {
        if (c instanceof GuiProgressBar.ProgressBarStyle) {
            ((GuiProgressBar.ProgressBarStyle) c).setFullTexture(p.getValue());
        }
    }, "progress-bar-full-image"),
    PROGRESS_FULL_COLOR(CssHelper.COLOR, (p, c) -> {
        if (c instanceof GuiProgressBar.ProgressBarStyle) {
            ((GuiProgressBar.ProgressBarStyle) c).setFullProgressBarColor(p.getValue());
        }
    }, "progress-bar-full-color"),
    PROGRESS_TEXT_COLOR(CssHelper.COLOR, (p, c) -> {
        if (c instanceof GuiProgressBar.ProgressBarStyle) {
            ((GuiProgressBar.ProgressBarStyle) c).setProgressTextColor(p.getValue());
        }
    }, "progress-bar-text-color"),
    DISPLAY(CssHelper.STRING, (p, c) -> {
        if (p.getType().isNone() || p.getValue().equals("none")) {
            c.setDisplay(GuiConstants.COMPONENT_DISPLAY.NONE);
        } else if (p.getValue().equals("block")) {
            c.setDisplay(GuiConstants.COMPONENT_DISPLAY.BLOCK);
        } else if (p.getValue().equals("inline")) {
            c.setDisplay(GuiConstants.COMPONENT_DISPLAY.INLINE);
        } else {
            throw new IllegalArgumentException("Unsupported display value : " + p.getValue());
        }
    }, "display", false, true);

    /*
    overflow, border style, asymetric borders.....
     */

    public final String key;
    public final DefinitionType<?> parser;
    public final CssStyleApplier<?> applyFunction;
    public final boolean isDefaultAuto;
    public final boolean acceptsNullValue;
    public final boolean inheritable;

    <T> EnumCssStyleProperty(DefinitionType<T> parser, CssStyleApplier<T> applyFunction, String key) {
        this(parser, applyFunction, key, false, false);
    }

    <T> EnumCssStyleProperty(DefinitionType<T> parser, CssStyleApplier<T> applyFunction, String key, boolean isDefaultAuto) {
        this(parser, applyFunction, key, isDefaultAuto, isDefaultAuto, false);
    }

    <T> EnumCssStyleProperty(DefinitionType<T> parser, CssStyleApplier<T> applyFunction, String key, boolean isDefaultAuto, boolean acceptsNullValue) {
        this(parser, applyFunction, key, isDefaultAuto, acceptsNullValue, false);
    }

    <T> EnumCssStyleProperty(DefinitionType<T> parser, CssStyleApplier<T> applyFunction, String key, boolean isDefaultAuto, boolean acceptsNullValue, boolean inheritable) {
        this.applyFunction = applyFunction;
        this.key = key;
        this.parser = parser;
        this.isDefaultAuto = isDefaultAuto;
        this.acceptsNullValue = acceptsNullValue;
        this.inheritable = inheritable;
    }

    public static EnumCssStyleProperty fromKey(String property) {
        for (EnumCssStyleProperty prop : values()) {
            if (prop.key.equals(property))
                return prop;
        }
        return null;
    }
}
