package fr.aym.acsguis.cssengine.style;

import fr.aym.acsguis.GuiConstants;
import fr.aym.acsguis.cssengine.parsing.DnxCssParser;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.newdawn.slick.font.effects.Effect;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * The style manager of text elements, like labels, buttons or text fields
 */
public interface TextComponentStyleManager extends ComponentStyleManager
{
    /**
     * Adds an effect, only {@link org.newdawn.slick.font.effects.ShadowEffect} is supported for the vanilla font, in other cases, use a ttf font
     */
    TextComponentStyleManager addEffect(Effect effect);

    /**
     * Removes an effect
     *
     * @param effectType The class of the effect to remove
     */
    TextComponentStyleManager removeEffect(Class<? extends Effect> effectType);

    /**
     * @return All effects to display
     */
    @Nullable
    Collection<Effect> getEffects();

    TextComponentStyleManager setFontSize(int size);
    int getFontSize();
    /**
     * The font must have been pushed into {@link com.helger.css.utils.CSSColorHelper} !
     * @return The height, in pixels, of the given text, regardless of the scale (font size)
     */
    int getFontHeight(String text);

    TextComponentStyleManager setPaddingTop(int paddingTop);
    int getPaddingTop();

    TextComponentStyleManager setPaddingBottom(int paddingBottom);
    int getPaddingBottom();

    TextComponentStyleManager setPaddingLeft(int paddingLeft);
    int getPaddingLeft();

    TextComponentStyleManager setPaddingRight(int paddingRight);
    int getPaddingRight();

    TextComponentStyleManager setHorizontalTextAlignment(GuiConstants.HORIZONTAL_TEXT_ALIGNMENT left);
    GuiConstants.HORIZONTAL_TEXT_ALIGNMENT getHorizontalTextAlignment();

    TextComponentStyleManager setVerticalTextAlignment(GuiConstants.VERTICAL_TEXT_ALIGNMENT top);
    GuiConstants.VERTICAL_TEXT_ALIGNMENT getVerticalTextAlignment();

    /**
     * @param value Null for no formatting
     */
    TextComponentStyleManager setFontColor(@Nullable TextFormatting value);
    TextFormatting getFontColor();

    /**
     * Sets the font family name
     * @see DnxCssParser
     */
    TextComponentStyleManager setFontFamily(ResourceLocation value);
    ResourceLocation getFontFamily();
}
