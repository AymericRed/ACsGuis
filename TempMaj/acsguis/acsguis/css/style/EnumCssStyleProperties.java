package fr.aym.acsguis.cssengine.style;

import com.helger.css.ECSSUnit;
import fr.aym.acsguis.GuiConstants;
import fr.aym.acsguis.cssengine.DefinitionType;
import fr.aym.acsguis.cssengine.DnxCssHelper;
import net.minecraft.util.text.TextFormatting;
import org.newdawn.slick.font.effects.ShadowEffect;

import java.awt.*;

public enum EnumCssStyleProperties
{
    BACKGROUND_COLOR(DnxCssHelper.COLOR, (ctx, p, c) ->
                c.setBackgroundColor(p.getValue()), "background-color", true),
    COLOR(DnxCssHelper.COLOR, (ctx, p, c) -> {
        c.setForegroundColor(p.getValue());
    }, "color", true),
    BORDER_COLOR(DnxCssHelper.COLOR, (ctx, p, c) -> c.setBorderColor(p.getValue()), "border-color"),
    TEXTURE(DnxCssHelper.TEXTURE_SPRITE, (ctx, p, c) -> c.setTexture(p.getValue()), "background-image"),
    VISIBILITY(DnxCssHelper.STRING, (ctx, p, c) -> c.setVisible(!p.getValue().equals("hidden")), "visibility"),
    FONT_SIZE(DnxCssHelper.INT, (ctx, p, c) -> {
         if(c instanceof TextComponentStyleManager)
             ((TextComponentStyleManager) c).setFontSize(p.getValue());
    }, "font-size"),
    FONT_STYLE(DnxCssHelper.STRING, (ctx, p, c) -> {
        if(c instanceof TextComponentStyleManager) {
            switch (p.getValue())
            {
                case "italic":
                    ((TextComponentStyleManager) c).setFontColor(TextFormatting.ITALIC);
                    break;
                case "normal":
                    ((TextComponentStyleManager) c).setFontColor(null);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported font style "+p.getValue());
            }
        }
    }, "font-style"),
    FONT_FAMILY(DnxCssHelper.RESOURCE_LOCATION, (ctx, p, c) -> {
        if(c instanceof TextComponentStyleManager) {
            ((TextComponentStyleManager) c).setFontFamily(p.getValue());
        }
    }, "font-family", true),
    WIDTH(DnxCssHelper.CSS_INT, (ctx, p, c) -> {
        //System.out.println("Set width of "+c.getOwner()+" to "+p.getValue());
        if (p.getValue().getUnit() == ECSSUnit.PERCENTAGE) {
            c.setRelativeWidth(p.getValue().getAsIntValue() / 100f);
        } else {
            c.setWidth(p.getValue().getAsIntValue());
        }
    }, "width", true),
    MAX_WIDTH(DnxCssHelper.CSS_INT, (ctx, p , c) -> {
        if (p.getValue().getUnit() == ECSSUnit.PERCENTAGE) {
            c.setRelativeWidth(p.getValue().getAsIntValue() / 100f);
        } else {
            c.setMaxWidth(p.getValue().getAsIntValue());
        }
    }, "max-width", false),
    MIN_WIDTH(DnxCssHelper.INT, (ctx, p , c) -> {
        c.setMinWidth(p.getValue());
    }, "min-width", false),
    BORDER_WIDTH(DnxCssHelper.CSS_INT, (ctx, p, c) -> c.setBorderSize(p.getValue()), "border-width"),
    HEIGHT(DnxCssHelper.CSS_INT, (ctx, p, c) -> {
        if (p.getValue().getUnit() == ECSSUnit.PERCENTAGE) {
            c.setRelativeHeight(p.getValue().getAsIntValue() / 100f);
        } else {
            c.setHeight(p.getValue().getAsIntValue());
        }
    }, "height", false),
    MAX_HEIGHT(DnxCssHelper.INT, (ctx, p , c) -> {
        c.setMaxHeight(p.getValue());
    }, "max-height", false),
    MIN_HEIGHT(DnxCssHelper.INT, (ctx, p , c) -> {
        c.setMinHeight(p.getValue());
    }, "min-height", false),
    BORDER_STYLE(DnxCssHelper.STRING, (ctx, p, c) -> {if(!p.getValue().equals("solid")) throw new IllegalArgumentException("Border style "+p.getValue()+" is not supported !");}, "border-style"),
    BORDER_POSITION(DnxCssHelper.STRING, (ctx, p, c) -> {if(p.getValue().equals("internal")) c.setBorderPosition(ComponentStyleManager.BORDER_POSITION.INTERNAL); else c.setBorderPosition(ComponentStyleManager.BORDER_POSITION.EXTERNAL);}, "border-position", true),
    BACKGROUND_REPEAT(DnxCssHelper.STRING, (ctx, p, c) -> {
        switch (p.getValue())
        {
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
            case "none":
                c.setRepeatBackgroundX(false);
                c.setRepeatBackgroundY(false);
                break;
            default:
                throw new IllegalArgumentException("Background repeat "+p.getValue()+" is not supported !");
        }
    }, "background-repeat"),
    BACKGROUND_POSITION(DnxCssHelper.STRING, (ctx, p, c) -> {if(!p.getValue().equals("top left")) throw new IllegalArgumentException("Background position property is not supported ! It should be defined in the texture property");}, "background-position"),
    BACKGROUND_ATTACHMENT(DnxCssHelper.STRING, (ctx, p, c) -> {/* ignore */}, "background-attachment"),
    BACKGROUND_ORIGIN(DnxCssHelper.STRING, (ctx, p, c) -> {/* ignore */}, "background-origin"),
    BACKGROUND_SIZE(DnxCssHelper.STRING, (ctx, p, c) -> {if(!p.getValue().equals("auto auto")) throw new IllegalArgumentException("Background size property is not supported ! It should be defined in the texture property");}, "background-size"),
    BACKGROUND_CLIP(DnxCssHelper.STRING, (ctx, p, c) -> {/* ignore */}, "background-clip"),
    BORDER_RADIUS(DnxCssHelper.CSS_INT, (ctx, p, c) -> c.setBorderRadius(p.getValue()), "border-radius"),
    /*POSITION(DnxCssHelper.STRING, (ctx, p, c) -> {
        if(p.getValue().equalsIgnoreCase("absolute")) {
            c.setHorizontalAlignment(GuiConstants.ENUM_POSITION.ABSOLUTE);
            c.setVerticalAlignment(GuiConstants.ENUM_POSITION.ABSOLUTE);
        }
        else if(p.getValue().equalsIgnoreCase("relative")) {
            c.setHorizontalAlignment(GuiConstants.ENUM_POSITION.RELATIVE);
            c.setVerticalAlignment(GuiConstants.ENUM_POSITION.RELATIVE);
        }
        else
            throw new IllegalArgumentException("Position property "+p.getValue()+" is not supported");
    }, "position"),*/
    LEFT(DnxCssHelper.CSS_INT, (ctx, p, c) -> {
        if(p.getValue().getUnit() == ECSSUnit.PERCENTAGE)
            c.setRelativeX(p.getValue().getAsIntValue()/100f, GuiConstants.ENUM_RELATIVE_X.LEFT);
        else
            c.setX(p.getValue().getAsIntValue(), GuiConstants.ENUM_RELATIVE_X.LEFT);
    }, "left"),
    RIGHT(DnxCssHelper.CSS_INT, (ctx, p, c) -> {
        if(p.getValue().getUnit() == ECSSUnit.PERCENTAGE)
            c.setRelativeX(p.getValue().getAsIntValue()/100f, GuiConstants.ENUM_RELATIVE_X.RIGHT);
        else
            c.setX(p.getValue().getAsIntValue(), GuiConstants.ENUM_RELATIVE_X.RIGHT);
    }, "right"),
    TOP(DnxCssHelper.CSS_INT, (ctx, p, c) -> {
        if(p.getValue().getUnit() == ECSSUnit.PERCENTAGE)
            c.setRelativeY(p.getValue().getAsIntValue()/100f, GuiConstants.ENUM_RELATIVE_Y.TOP);
        else
            c.setY(p.getValue().getAsIntValue(), GuiConstants.ENUM_RELATIVE_Y.TOP);
    }, "top"),
    BOTTOM(DnxCssHelper.CSS_INT, (ctx, p, c) -> {
        if(p.getValue().getUnit() == ECSSUnit.PERCENTAGE)
            c.setRelativeY(p.getValue().getAsIntValue()/100f, GuiConstants.ENUM_RELATIVE_Y.BOTTOM);
        else
            c.setY(p.getValue().getAsIntValue(), GuiConstants.ENUM_RELATIVE_Y.BOTTOM);
    }, "bottom"),
    TEXT_SHADOW(DnxCssHelper.STRING, (ctx, p, c) -> {
        if(c instanceof TextComponentStyleManager) {
            if(p.getValue().equals("enable")) { //TODO IMPROVE SHADOWS
                ShadowEffect effect = new ShadowEffect();
                effect.setColor(Color.BLACK);
                ((TextComponentStyleManager) c).addEffect(effect);
            }
            else
                ((TextComponentStyleManager) c).removeEffect(ShadowEffect.class);
        }
    }, "text-shadow", false),
    Z_INDEX(DnxCssHelper.INT, (ctx, p, c) -> c.setZLevel(p.getValue()), "z-index"),
    PADDING_LEFT(DnxCssHelper.CSS_INT, (ctx, p, c) -> {
        if(c instanceof TextComponentStyleManager) {
            if (p.getValue().getUnit() == ECSSUnit.PERCENTAGE)
                ((TextComponentStyleManager) c).setPaddingLeft((int) (p.getValue().getAsIntValue() * c.getWidth() / 100f));
            else
                ((TextComponentStyleManager) c).setPaddingLeft(p.getValue().getAsIntValue());
        }
    }, "padding-left"),
    PADDING_TOP(DnxCssHelper.CSS_INT, (ctx, p, c) -> {
        if(c instanceof TextComponentStyleManager) {
            if (p.getValue().getUnit() == ECSSUnit.PERCENTAGE)
                ((TextComponentStyleManager) c).setPaddingTop((int) (p.getValue().getAsIntValue() * c.getWidth() / 100f));
            else
                ((TextComponentStyleManager) c).setPaddingTop(p.getValue().getAsIntValue());
        }
    }, "padding-top"),
    PADDING_RIGHT(DnxCssHelper.CSS_INT, (ctx, p, c) -> {
        if(c instanceof TextComponentStyleManager) {
            if (p.getValue().getUnit() == ECSSUnit.PERCENTAGE)
                ((TextComponentStyleManager) c).setPaddingRight((int) (p.getValue().getAsIntValue() * c.getWidth() / 100f));
            else
                ((TextComponentStyleManager) c).setPaddingRight(p.getValue().getAsIntValue());
        }
    }, "padding-right"),
    PADDING_BOTTOM(DnxCssHelper.CSS_INT, (ctx, p, c) -> {
        if(c instanceof TextComponentStyleManager) {
            if (p.getValue().getUnit() == ECSSUnit.PERCENTAGE)
                ((TextComponentStyleManager) c).setPaddingBottom((int) (p.getValue().getAsIntValue() * c.getWidth() / 100f));
            else
                ((TextComponentStyleManager) c).setPaddingBottom(p.getValue().getAsIntValue());
        }
    }, "padding-bottom"),
    TEXT_ALIGN_HORIZONTAL(DnxCssHelper.STRING, (ctx, p, c) -> {
        if(c instanceof TextComponentStyleManager) {
            switch (p.getValue())
            {
                case "left":
                    ((TextComponentStyleManager) c).setHorizontalTextAlignment(GuiConstants.HORIZONTAL_TEXT_ALIGNMENT.LEFT);
                    break;
                case "right":
                    ((TextComponentStyleManager) c).setHorizontalTextAlignment(GuiConstants.HORIZONTAL_TEXT_ALIGNMENT.RIGHT);
                    break;
                case "center":
                    ((TextComponentStyleManager) c).setHorizontalTextAlignment(GuiConstants.HORIZONTAL_TEXT_ALIGNMENT.CENTER);
                    break;
                case "justify":
                    ((TextComponentStyleManager) c).setHorizontalTextAlignment(GuiConstants.HORIZONTAL_TEXT_ALIGNMENT.JUSTIFY);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown (horizontal) text align : "+p.getValue());
            }
        }
    }, "text-align"),
    TEXT_ALIGN_VERTICAL(DnxCssHelper.STRING, (ctx, p, c) -> {
        if(c instanceof TextComponentStyleManager) {
            switch (p.getValue())
            {
                case "top":
                    ((TextComponentStyleManager) c).setVerticalTextAlignment(GuiConstants.VERTICAL_TEXT_ALIGNMENT.TOP);
                    break;
                case "bottom":
                    ((TextComponentStyleManager) c).setVerticalTextAlignment(GuiConstants.VERTICAL_TEXT_ALIGNMENT.BOTTOM);
                    break;
                case "center":
                    ((TextComponentStyleManager) c).setVerticalTextAlignment(GuiConstants.VERTICAL_TEXT_ALIGNMENT.CENTER);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown vertical text align : "+p.getValue());
            }
        }
    }, "text-align-vertical");

    /*
    overflow, border style, asymetric borders.....
     */

    public final String key;
    public final DefinitionType<?> parser;
    public final CssStyleApplier<?> applyFunction;
    public final boolean inheritable;

    <T> EnumCssStyleProperties(DefinitionType<T> parser, CssStyleApplier<T> applyFunction, String key)
    {
        this(parser, applyFunction, key, false);
    }
    <T> EnumCssStyleProperties(DefinitionType<T> parser, CssStyleApplier<T> applyFunction, String key, boolean inheritable)
    {
        this.applyFunction = applyFunction;
        this.key = key;
        this.parser = parser;
        this.inheritable = inheritable;
    }

    public static EnumCssStyleProperties fromKey(String property) {
        for(EnumCssStyleProperties prop : values())
        {
            if(prop.key.equals(property))
                return prop;
        }
        return null;
    }
}
