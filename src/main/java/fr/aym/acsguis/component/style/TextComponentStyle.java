package fr.aym.acsguis.component.style;

import fr.aym.acsguis.cssengine.parsing.ACsGuisCssParser;
import fr.aym.acsguis.cssengine.style.CssTextComponentStyle;
import fr.aym.acsguis.utils.GuiConstants;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.newdawn.slick.font.effects.Effect;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * The style manager of text elements, like labels, buttons or text fields
 *
 * @see CssTextComponentStyle
 * @see ComponentStyle
 */
public interface TextComponentStyle extends ComponentStyle {
    @Override
    TextComponentStyleCustomizer getCustomizer();

    /**
     * @return All effects to display
     */
    @Nullable
    Collection<Effect> getEffects();

    int getFontSize();

    /**
     * The font must have been pushed into {@link fr.aym.acsguis.cssengine.font.CssFontHelper} !
     *
     * @return The height, in pixels, of the given text, regardless of the scale (font size)
     */
    int getFontHeight(String text);

    GuiConstants.HORIZONTAL_TEXT_ALIGNMENT getHorizontalTextAlignment();

    GuiConstants.VERTICAL_TEXT_ALIGNMENT getVerticalTextAlignment();

    TextFormatting getFontStyle();

    ResourceLocation getFontFamily();

    interface InternalStyle extends TextComponentStyle, InternalComponentStyle {
        /**
         * Adds an effect, only {@link org.newdawn.slick.font.effects.ShadowEffect} is supported for the vanilla font, in other cases, use a ttf font
         */
        InternalStyle addEffect(Effect effect);

        /**
         * Removes an effect
         *
         * @param effectType The class of the effect to remove
         */
        InternalStyle removeEffect(Class<? extends Effect> effectType);

        InternalStyle setFontSize(int size);

        InternalStyle setHorizontalTextAlignment(GuiConstants.HORIZONTAL_TEXT_ALIGNMENT left);

        InternalStyle setVerticalTextAlignment(GuiConstants.VERTICAL_TEXT_ALIGNMENT top);

        /**
         * @param value Null for no formatting
         */
        InternalStyle setFontStyle(@Nullable TextFormatting value);

        /**
         * Sets the font family name
         *
         * @see ACsGuisCssParser
         */
        InternalStyle setFontFamily(ResourceLocation value);
    }

    interface TextComponentStyleCustomizer extends ComponentStyleCustomizer {
        TextComponentStyleCustomizer setTextShadow(boolean enableShadow);

        TextComponentStyleCustomizer setFontSize(int size);

        TextComponentStyleCustomizer setPaddingTop(int paddingTop);

        TextComponentStyleCustomizer setPaddingBottom(int paddingBottom);

        TextComponentStyleCustomizer setPaddingLeft(int paddingLeft);

        TextComponentStyleCustomizer setPaddingRight(int paddingRight);

        TextComponentStyleCustomizer setHorizontalTextAlignment(GuiConstants.HORIZONTAL_TEXT_ALIGNMENT left);

        TextComponentStyleCustomizer setVerticalTextAlignment(GuiConstants.VERTICAL_TEXT_ALIGNMENT top);

        /**
         * @param value Null for no formatting
         */
        TextComponentStyleCustomizer setFontStyle(@Nullable TextFormatting value);

        /**
         * Sets the font family name
         *
         * @see ACsGuisCssParser
         */
        TextComponentStyleCustomizer setFontFamily(ResourceLocation value);
    }
}
