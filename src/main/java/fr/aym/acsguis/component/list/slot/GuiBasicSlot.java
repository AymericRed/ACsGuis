package fr.aym.acsguis.component.list.slot;

import fr.aym.acsguis.component.list.GuiList;
import fr.aym.acsguis.component.style.ComponentStyleManager;
import fr.aym.acsguis.component.textarea.GuiLabel;
import fr.aym.acsguis.cssengine.parsing.core.objects.CssIntValue;
import fr.aym.acsguis.utils.GuiConstants;

import java.awt.*;

public class GuiBasicSlot extends GuiSlot
{
	protected GuiLabel entryNameLabel;
	
	public GuiBasicSlot(GuiList list, int n, String entryName) {
		super(list, 0, 30 * n + list.getListPaddingTop(), 0, 25, n, entryName);

		style.getWidth().setRelative(0.5f);
		style.getXPos().setRelative(0.25f);

		style.setBackgroundColor(new Color(0,0,0,0.3f).getRGB());
		style.setBorderSize(new CssIntValue(1));
		style.setBorderPosition(ComponentStyleManager.BORDER_POSITION.INTERNAL);
		style.setBorderColor(new Color(206, 206, 206,255).getRGB());
		
		entryNameLabel = new GuiLabel(0,8, 0,9, entryName);
		entryNameLabel.getStyle().getWidth().setRelative(1);
		entryNameLabel.getStyle().setHorizontalTextAlignment(GuiConstants.HORIZONTAL_TEXT_ALIGNMENT.CENTER);
		
		add(entryNameLabel);
	}
	
	@Override
	public void onFocus() {
		super.onFocus();
		style.setBackgroundColor(new Color(0,0,0,0.6f).getRGB());
	}
	
	@Override
	public void onFocusLoose() {
		style.setBackgroundColor(new Color(0,0,0,0.3f).getRGB());
	}
}
