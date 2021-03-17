package fr.aym.acsguis.component.textarea;

import fr.aym.acsguis.GuiAPIClientHelper;
import fr.aym.acsguis.GuiConstants;
import fr.aym.acsguis.component.EnumComponentType;
import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.cssengine.font.CssFontHelper;
import fr.aym.acsguis.cssengine.selectors.EnumSelectorContext;
import fr.aym.acsguis.cssengine.style.AutoStyleHandler;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperties;
import fr.aym.acsguis.cssengine.style.TextComponentStyleManager;
import net.minecraft.client.renderer.GlStateManager;

import java.util.Arrays;
import java.util.List;

public class GuiLabel extends GuiTextArea implements AutoStyleHandler<TextComponentStyleManager>
{
	public GuiLabel(String text) {
		super(0, 0, 0, 0);
		setEditable(false);
		setText(text);
	}

	public GuiLabel(int x, int y, int width, int height, String text) {
		super(x, y, width, height);
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
        for(int i = 0; i < lines.size(); i++) {
            String line = formatting+lines.get(i);

			if(getStyle().getHorizontalTextAlignment() == GuiConstants.HORIZONTAL_TEXT_ALIGNMENT.JUSTIFY) {
				
				String[] words = lines.get(i).split(" ");
				
				int lineLength = GuiComponent.mc.fontRenderer.getStringWidth(line);
				int spacesLeft = getMaxLineLength() - lineLength;
				
				if(i == lines.size() - 1 || lines.get(i+1).trim().isEmpty())
					spacesLeft = 0;
				
				int x = 0;
				
				for(int j = 0; j < words.length; j++) {
					x = j == 0 ? 0 : x + GuiComponent.mc.fontRenderer.getStringWidth(words[j-1] + " ") + spacesLeft / (words.length - 1);
					
					/*drawString(mc.fontRenderer, words[j],
							getScreenX() + getPaddingLeft() + x - getLineScrollOffsetX(),
							getScreenY() + getPaddingTop() + GuiAPIClientHelper.getRelativeTextY(i, lines.size(), getHeight() - (getPaddingTop() + getPaddingBottom()), verticalTextAlignment) - getLineScrollOffsetY(),
							isEnabled() ? getEnabledTextColor() : getDisabledTextColor());*/
					float x1 = getRenderMinX() + getPaddingLeft() + x - getLineScrollOffsetX();
					float y1 = getRenderMinY() + getPaddingTop() + GuiAPIClientHelper.getRelativeTextY(i, lines.size(), getHeight() - (getPaddingTop() + getPaddingBottom()), getStyle().getVerticalTextAlignment(), scale*getStyle().getFontHeight(line)) - getLineScrollOffsetY();
					/*GuiComponent.mc.fontRenderer.drawString(words[j],
							x1/scale,
							y1/scale,
							getStyle().getForegroundColor(),
							getStyle().isShadowed());*/
					CssFontHelper.draw( x1/scale, y1/scale, words[j], getStyle().getForegroundColor());
				}
				
			} else {
				/*drawString(mc.fontRenderer, line,
						getScreenX() + getPaddingLeft() + GuiAPIClientHelper.getRelativeTextX(line, getWidth() - (getPaddingLeft() + getPaddingRight()), horizontalTextAlignment) - getLineScrollOffsetX(),
						getScreenY() + getPaddingTop() + GuiAPIClientHelper.getRelativeTextY(i, lines.size(), getHeight() - (getPaddingTop() + getPaddingBottom()), verticalTextAlignment) - getLineScrollOffsetY(),
						isEnabled() ? getEnabledTextColor() : getDisabledTextColor());*/

				float x = getRenderMinX() /*Changed from getScreenX() for a shelou bug*/+ getPaddingLeft() + GuiAPIClientHelper.getRelativeTextX(line, getWidth() - (getPaddingLeft() + getPaddingRight()), getStyle().getHorizontalTextAlignment(), CssFontHelper.getBoundFont(), scale) - getLineScrollOffsetX();
				float y = getRenderMinY() + getPaddingTop() + GuiAPIClientHelper.getRelativeTextY(i, lines.size(), getHeight() - (getPaddingTop() + getPaddingBottom()), getStyle().getVerticalTextAlignment(), scale*getStyle().getFontHeight(line)) - getLineScrollOffsetY();
				/*GuiComponent.mc.fontRenderer.drawString(line,
						x/scale,
						y/scale,
						getStyle().getForegroundColor(),
						getStyle().isShadowed());*/
				CssFontHelper.draw(x/scale, y/scale, line, getStyle().getForegroundColor());

				/*GlStateManager.disableDepth();
				GlStateManager.disableAlpha();
				Gui.drawRect(0, getRenderMinY(), mc.displayWidth, getRenderMinY()+10, Color.RED.getRGB());
				Gui.drawRect(0, (int) y, mc.displayWidth, (int) (y+1), Color.GREEN.getRGB());
				Gui.drawRect(0, (int) y+getStyle().getFontHeight(line), mc.displayWidth, (int) (y+getStyle().getFontHeight(line)+1), Color.BLUE.getRGB());
				Gui.drawRect(0, (int) getRenderMinY()+getHeight()/2-1, mc.displayWidth, (int) (getRenderMinY()+getHeight()/2+1), Color.PINK.getRGB());
				GlStateManager.enableAlpha();
				GlStateManager.enableDepth();*/
				//TODO VISUAL PLACING DEBUG
			}
        }
    }

	@Override
	public int getMaxLineLength() {
    	return (int) ((getWidth() - (getPaddingLeft() + getPaddingRight()))*textScale);
	}

	@Override
	public GuiTextArea setText(String text) {
		super.setText(text);
		getStyle().refreshCss(false, "set_text"); //Refresh style, for auto width and height
		return this;
	}

	@Override
	public boolean handleProperty(EnumCssStyleProperties property, EnumSelectorContext context, TextComponentStyleManager target) {
		if(property == EnumCssStyleProperties.WIDTH)
		{
			target.setHorizontalSize(GuiConstants.ENUM_SIZE.ABSOLUTE);
			target.setWidth((int) ((getPaddingLeft()+getPaddingRight()+5+mc.fontRenderer.getStringWidth(getText()))*((float)target.getFontSize()/mc.fontRenderer.FONT_HEIGHT)));
			return true;
		}
		if(property == EnumCssStyleProperties.HEIGHT)
		{
			target.setVerticalSize(GuiConstants.ENUM_SIZE.ABSOLUTE);
			float scale = (float)(getStyle().getFontSize())/mc.fontRenderer.FONT_HEIGHT;
			CssFontHelper.pushDrawing(target.getFontFamily(), target.getEffects());
			target.setHeight((int) (getPaddingTop()+getPaddingBottom()+target.getFontHeight(getText())*scale));
			CssFontHelper.popDrawing();
			return true;
		}
		return false;
	}

	private static final List<EnumCssStyleProperties> affectedProperties = Arrays.asList(EnumCssStyleProperties.WIDTH, EnumCssStyleProperties.HEIGHT);
	@Override
	public List<EnumCssStyleProperties> getModifiedProperties(TextComponentStyleManager target) {
		return affectedProperties;
	}
}
