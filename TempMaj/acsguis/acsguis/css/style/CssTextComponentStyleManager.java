package fr.aym.acsguis.cssengine.style;

import fr.aym.acsguis.GuiConstants;
import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.cssengine.font.CssFontHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.newdawn.slick.font.effects.Effect;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CssTextComponentStyleManager extends CssComponentStyleManager implements TextComponentStyleManager
{
    protected Map<Class<? extends Effect>, Effect> effects;
    protected int fontSize = 9;

    /** Text horizontal alignment, relative to the GuiLabel {@link GuiConstants.HORIZONTAL_TEXT_ALIGNMENT} **/
    protected GuiConstants.HORIZONTAL_TEXT_ALIGNMENT horizontalTextAlignment = GuiConstants.HORIZONTAL_TEXT_ALIGNMENT.LEFT;
    /** Text horizontal alingment, relative to the GuiLabel {@link GuiConstants.VERTICAL_TEXT_ALIGNMENT} **/
    protected GuiConstants.VERTICAL_TEXT_ALIGNMENT verticalTextAlignment = GuiConstants.VERTICAL_TEXT_ALIGNMENT.TOP;

    protected int paddingLeft = 0, paddingRight = 0, paddingTop = 0, paddingBottom = 0;

    @Nullable
    protected TextFormatting fontStyle;
    @Nullable
    protected ResourceLocation fontFamily;

    public CssTextComponentStyleManager(GuiComponent<?> component) {
        super(component);
    }

    @Override
    public TextComponentStyleManager addEffect(Effect effect) {
        if(effects == null)
            effects = new HashMap<>();
        effects.put(effect.getClass(), effect); //The class guaranties unity
        return this;
    }

    @Override
    public TextComponentStyleManager removeEffect(Class<? extends Effect>effectType) {
        if(effects != null)
            effects.remove(effectType);
        return this;
    }

    @Override
    public Collection<Effect> getEffects() {
        return effects == null ? null : effects.values();
    }

    @Override
    public TextComponentStyleManager setFontSize(int size) {
        this.fontSize = size;
        return this;
    }

    @Override
    public int getFontSize() {
        return fontSize;
    }

    @Override
    public int getFontHeight(String text) {
        return CssFontHelper.getFontHeight(text);
    }

    @Override
    public TextComponentStyleManager setHorizontalTextAlignment(GuiConstants.HORIZONTAL_TEXT_ALIGNMENT horizontalTextAlignment) {
        this.horizontalTextAlignment = horizontalTextAlignment;
        return this;
    }

    @Override
    public GuiConstants.HORIZONTAL_TEXT_ALIGNMENT getHorizontalTextAlignment() {
        return horizontalTextAlignment;
    }

    @Override
    public TextComponentStyleManager setVerticalTextAlignment(GuiConstants.VERTICAL_TEXT_ALIGNMENT verticalTextAlignment) {
        this.verticalTextAlignment = verticalTextAlignment;
        return this;
    }

    @Override
    public TextComponentStyleManager setFontColor(TextFormatting value) {
        this.fontStyle = value;
        return this;
    }

    @Override
    public TextFormatting getFontColor() {
        return fontStyle;
    }

    @Override
    public TextComponentStyleManager setFontFamily(ResourceLocation value) {
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

    @Override
    public TextComponentStyleManager setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
        return this;
    }

    @Override
    public TextComponentStyleManager setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
        return this;
    }

    @Override
    public TextComponentStyleManager setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
        return this;
    }

    @Override
    public TextComponentStyleManager setPaddingRight(int paddingRight) {
        this.paddingRight = paddingRight;
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
}
