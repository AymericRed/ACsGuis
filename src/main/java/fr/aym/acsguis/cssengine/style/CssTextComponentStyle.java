package fr.aym.acsguis.cssengine.style;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.style.AutoStyleHandler;
import fr.aym.acsguis.component.style.InternalComponentStyle;
import fr.aym.acsguis.component.style.TextComponentStyle;
import fr.aym.acsguis.cssengine.font.CssFontHelper;
import fr.aym.acsguis.utils.GuiConstants;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.newdawn.slick.font.effects.Effect;
import org.newdawn.slick.font.effects.ShadowEffect;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class CssTextComponentStyle extends CssComponentStyle implements TextComponentStyle, TextComponentStyle.InternalStyle {
    protected Map<Class<? extends Effect>, Effect> effects;
    protected int fontSize = 9;

    /**
     * Text horizontal alignment, relative to the GuiLabel {@link GuiConstants.HORIZONTAL_TEXT_ALIGNMENT}
     **/
    protected GuiConstants.HORIZONTAL_TEXT_ALIGNMENT horizontalTextAlignment = GuiConstants.HORIZONTAL_TEXT_ALIGNMENT.LEFT;
    /**
     * Text horizontal alingment, relative to the GuiLabel {@link GuiConstants.VERTICAL_TEXT_ALIGNMENT}
     **/
    protected GuiConstants.VERTICAL_TEXT_ALIGNMENT verticalTextAlignment = GuiConstants.VERTICAL_TEXT_ALIGNMENT.TOP;

    @Nullable
    protected TextFormatting fontStyle;
    @Nullable
    protected ResourceLocation fontFamily;

    public CssTextComponentStyle(GuiComponent component) {
        super(component, style -> new CssTextComponentStyleCustomizer(component, style));
    }

    @Override
    public TextComponentStyleCustomizer getCustomizer() {
        return (TextComponentStyleCustomizer) super.getCustomizer();
    }

    @Override
    public InternalStyle addEffect(Effect effect) {
        if (effects == null)
            effects = new HashMap<>();
        effects.put(effect.getClass(), effect); //The class guaranties unity
        return this;
    }

    @Override
    public InternalStyle removeEffect(Class<? extends Effect> effectType) {
        if (effects != null)
            effects.remove(effectType);
        return this;
    }

    @Override
    public Collection<Effect> getEffects() {
        return effects == null ? null : effects.values();
    }

    @Override
    public InternalStyle setFontSize(int size) {
        this.fontSize = size;
        return this;
    }

    @Override
    public int getFontSize() {
        return fontSize;
    }

    @Override
    public int getFontHeight(String text) {
        return CssFontHelper.getDrawingFontHeight(text);
    }

    @Override
    public InternalStyle setHorizontalTextAlignment(GuiConstants.HORIZONTAL_TEXT_ALIGNMENT horizontalTextAlignment) {
        this.horizontalTextAlignment = horizontalTextAlignment;
        return this;
    }

    @Override
    public GuiConstants.HORIZONTAL_TEXT_ALIGNMENT getHorizontalTextAlignment() {
        return horizontalTextAlignment;
    }

    @Override
    public InternalStyle setVerticalTextAlignment(GuiConstants.VERTICAL_TEXT_ALIGNMENT verticalTextAlignment) {
        this.verticalTextAlignment = verticalTextAlignment;
        return this;
    }

    @Override
    public InternalStyle setFontStyle(TextFormatting value) {
        this.fontStyle = value;
        return this;
    }

    @Override
    public TextFormatting getFontStyle() {
        return fontStyle;
    }

    @Override
    public InternalStyle setFontFamily(ResourceLocation value) {
        this.fontFamily = value;
        return this;
    }

    @Override
    public ResourceLocation getFontFamily() {
        return fontFamily;
    }

    @Override
    public GuiConstants.VERTICAL_TEXT_ALIGNMENT getVerticalTextAlignment() {
        return verticalTextAlignment;
    }

    public static class CssTextComponentStyleCustomizer extends CssComponentStyleCustomizer implements TextComponentStyleCustomizer {
        public CssTextComponentStyleCustomizer(GuiComponent component, InternalComponentStyle style) {
            super(component, style);
        }

        @Override
        public TextComponentStyleCustomizer setTextShadow(boolean enableShadow) {
            getStyleOverrides().put(EnumCssStyleProperty.TEXT_SHADOW, style -> {
                if (enableShadow)
                    ((InternalStyle) style).addEffect(new ShadowEffect());
                else
                    ((InternalStyle) style).removeEffect(ShadowEffect.class);
            });
            return this;
        }

        @Override
        public TextComponentStyleCustomizer setFontSize(int size) {
            getStyleOverrides().put(EnumCssStyleProperty.FONT_SIZE, style -> ((InternalStyle) style).setFontSize(size));
            return this;
        }

        @Override
        public TextComponentStyleCustomizer setPaddingTop(int paddingTop) {
            getStyleOverrides().put(EnumCssStyleProperty.PADDING_TOP, style -> ((InternalStyle) style).setPaddingTop(paddingTop));
            return this;
        }

        @Override
        public TextComponentStyleCustomizer setPaddingBottom(int paddingBottom) {
            getStyleOverrides().put(EnumCssStyleProperty.PADDING_BOTTOM, style -> ((InternalStyle) style).setPaddingBottom(paddingBottom));
            return this;
        }

        @Override
        public TextComponentStyleCustomizer setPaddingLeft(int paddingLeft) {
            getStyleOverrides().put(EnumCssStyleProperty.PADDING_LEFT, style -> ((InternalStyle) style).setPaddingLeft(paddingLeft));
            return this;
        }

        @Override
        public TextComponentStyleCustomizer setPaddingRight(int paddingRight) {
            getStyleOverrides().put(EnumCssStyleProperty.PADDING_RIGHT, style -> ((InternalStyle) style).setPaddingRight(paddingRight));
            return this;
        }

        @Override
        public TextComponentStyleCustomizer setHorizontalTextAlignment(GuiConstants.HORIZONTAL_TEXT_ALIGNMENT left) {
            getStyleOverrides().put(EnumCssStyleProperty.TEXT_ALIGN_HORIZONTAL, style -> ((InternalStyle) style).setHorizontalTextAlignment(left));
            return this;
        }

        @Override
        public TextComponentStyleCustomizer setVerticalTextAlignment(GuiConstants.VERTICAL_TEXT_ALIGNMENT top) {
            getStyleOverrides().put(EnumCssStyleProperty.TEXT_ALIGN_VERTICAL, style -> ((InternalStyle) style).setVerticalTextAlignment(top));
            return this;
        }

        @Override
        public TextComponentStyleCustomizer setFontStyle(@Nullable TextFormatting value) {
            getStyleOverrides().put(EnumCssStyleProperty.FONT_STYLE, style -> ((InternalStyle) style).setFontStyle(value));
            return this;
        }

        @Override
        public TextComponentStyleCustomizer setFontFamily(ResourceLocation value) {
            getStyleOverrides().put(EnumCssStyleProperty.FONT_FAMILY, style -> ((InternalStyle) style).setFontFamily(value));
            return this;
        }
    }
}
