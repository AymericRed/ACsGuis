package fr.aym.acsguis.component.textarea;

import fr.aym.acsguis.api.GuiAPIClientHelper;
import fr.aym.acsguis.component.EnumComponentType;
import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.style.AutoStyleHandler;
import fr.aym.acsguis.component.style.TextComponentStyleManager;
import fr.aym.acsguis.cssengine.font.CssFontHelper;
import fr.aym.acsguis.cssengine.selectors.EnumSelectorContext;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperty;
import fr.aym.acsguis.utils.GuiConstants;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class GuiLabel extends GuiTextArea implements AutoStyleHandler<TextComponentStyleManager> {
    public GuiLabel(String text) {
        super(0, 0, 0, 0);
        setMaxTextLength(Integer.MAX_VALUE);
        setEditable(false);
        setText(text);
    }

    public GuiLabel(int x, int y, int width, int height, String text) {
        super(x, y, width, height);
        setMaxTextLength(Integer.MAX_VALUE);
        setEditable(false);
        setText(text);
    }

    @Override
    protected TextComponentStyleManager createStyleManager() {
        TextComponentStyleManager s = super.createStyleManager();
        s.addAutoStyleHandler(this);
        return s;
    }

    @Override
    public EnumComponentType getType() {
        return EnumComponentType.LABEL;
    }

    @Override
    public boolean allowLineBreak() {
        return false;
    }

    @Override
    public boolean isScrollable() {
        return false;
    }

    @Override
    protected void drawTextLines(List<String> lines, float scale) {

        GlStateManager.enableTexture2D();

        String formatting = getStyle().getFontColor() == null ? "" : getStyle().getFontColor().toString();
        //System.out.println("Draw "+formatting+" "+lines+" in "+this+" "+getWidth()+" "+getHeight()+" "+getX()+" "+getScreenX());
        for (int i = 0; i < lines.size(); i++) {
            String line = formatting + lines.get(i);

            if (getStyle().getHorizontalTextAlignment() == GuiConstants.HORIZONTAL_TEXT_ALIGNMENT.JUSTIFY) {

                String[] words = lines.get(i).split(" ");

                int lineLength = GuiComponent.mc.fontRenderer.getStringWidth(line);
                int spacesLeft = getMaxLineLength() - lineLength;

                if (i == lines.size() - 1 || lines.get(i + 1).trim().isEmpty())
                    spacesLeft = 0;

                int x = 0;

                for (int j = 0; j < words.length; j++) {
                    x = j == 0 ? 0 : x + GuiComponent.mc.fontRenderer.getStringWidth(words[j - 1] + " ") + spacesLeft / (words.length - 1);
					
					/*drawString(mc.fontRenderer, words[j],
							getScreenX() + getPaddingLeft() + x - getLineScrollOffsetX(),
							getScreenY() + getPaddingTop() + GuiAPIClientHelper.getRelativeTextY(i, lines.size(), getHeight() - (getPaddingTop() + getPaddingBottom()), verticalTextAlignment) - getLineScrollOffsetY(),
							isEnabled() ? getEnabledTextColor() : getDisabledTextColor());*/
                    float x1 = getRenderMinX() + getPaddingLeft() + x - getLineScrollOffsetX();
                    float y1 = getRenderMinY() + getPaddingTop() + GuiAPIClientHelper.getRelativeTextY(i, lines.size(), getHeight() - (getPaddingTop() + getPaddingBottom()), getStyle().getVerticalTextAlignment(), scale * getStyle().getFontHeight(line)) - getLineScrollOffsetY();
					/*GuiComponent.mc.fontRenderer.drawString(words[j],
							x1/scale,
							y1/scale,
							getStyle().getForegroundColor(),
							getStyle().isShadowed());*/
                    CssFontHelper.draw(x1 / scale, y1 / scale, words[j], getStyle().getForegroundColor());
                }

            } else {
				/*drawString(mc.fontRenderer, line,
						getScreenX() + getPaddingLeft() + GuiAPIClientHelper.getRelativeTextX(line, getWidth() - (getPaddingLeft() + getPaddingRight()), horizontalTextAlignment) - getLineScrollOffsetX(),
						getScreenY() + getPaddingTop() + GuiAPIClientHelper.getRelativeTextY(i, lines.size(), getHeight() - (getPaddingTop() + getPaddingBottom()), verticalTextAlignment) - getLineScrollOffsetY(),
						isEnabled() ? getEnabledTextColor() : getDisabledTextColor());*/

                float x = getRenderMinX() /*Changed from getScreenX() for a shelou bug*/ + getPaddingLeft() + GuiAPIClientHelper.getRelativeTextX(line, getWidth() - (getPaddingLeft() + getPaddingRight()), getStyle().getHorizontalTextAlignment(), CssFontHelper.getBoundFont(), scale) - getLineScrollOffsetX();
                float y = getRenderMinY() + getPaddingTop() + GuiAPIClientHelper.getRelativeTextY(i, lines.size(), getHeight() - (getPaddingTop() + getPaddingBottom()), getStyle().getVerticalTextAlignment(), scale * getStyle().getFontHeight(line)) - getLineScrollOffsetY();
				/*GuiComponent.mc.fontRenderer.drawString(line,
						x/scale,
						y/scale,
						getStyle().getForegroundColor(),
						getStyle().isShadowed());*/
                CssFontHelper.draw(x / scale, y / scale, line, getStyle().getForegroundColor());
            }
        }
    }

    @Override
    public GuiTextArea setText(String text) {
        super.setText(text);
        getStyle().refreshCss(getGui(), false); //Refresh style, for auto width and height
        return this;
    }

    @Override
    public int getMaxTextHeight() {
        return (int) MathHelper.clamp((getHeight() - getPaddingTop() - getPaddingBottom()) / textScale, 0, Integer.MAX_VALUE);
    }

    @Override
    public boolean handleProperty(EnumCssStyleProperty property, EnumSelectorContext context, TextComponentStyleManager target) {
        if (property == EnumCssStyleProperty.WIDTH) {
            target.getWidth().setAbsolute((int) ((getPaddingLeft() + getPaddingRight() + 5 + mc.fontRenderer.getStringWidth(getText())) * ((float) target.getFontSize() / mc.fontRenderer.FONT_HEIGHT)));
            return true;
        }
        if (property == EnumCssStyleProperty.HEIGHT) {
            float scale = (float) (getStyle().getFontSize()) / mc.fontRenderer.FONT_HEIGHT;
            CssFontHelper.pushDrawing(target.getFontFamily(), target.getEffects());
            target.getHeight().setAbsolute((int) (getPaddingTop() + getPaddingBottom() + target.getFontHeight(getText()) * scale));
            CssFontHelper.popDrawing();
            return true;
        }
        return false;
    }

    private static final List<EnumCssStyleProperty> affectedProperties = Arrays.asList(EnumCssStyleProperty.WIDTH, EnumCssStyleProperty.HEIGHT);

    @Override
    public Collection<EnumCssStyleProperty> getModifiedProperties(TextComponentStyleManager target) {
        return affectedProperties;
    }
}
