package fr.aym.acsguis.component.list.slot;

import com.helger.css.ECSSUnit;
import com.helger.css.propertyvalue.CSSSimpleValueWithUnit;
import fr.aym.acsguis.GuiConstants;
import fr.aym.acsguis.component.textarea.GuiLabel;
import fr.aym.acsguis.cssengine.style.ComponentStyleManager;
import fr.aym.acsguis.gui.GuiList;

import java.awt.*;

public class GuiBasicSlot extends GuiSlot
{
	protected GuiLabel entryNameLabel;
	
	public GuiBasicSlot(GuiList list, int n, String entryName) {
		super(list, 0, 30 * n + list.getListPaddingTop(), 0, 25, n, entryName);

		style.setRelativeWidth(0.5f).setRelativeX(0.25f);

		style.setBackgroundColor(new Color(0,0,0,0.3f).getRGB());
		style.setBorderSize(new CSSSimpleValueWithUnit(1, ECSSUnit.PX));
		style.setBorderPosition(ComponentStyleManager.BORDER_POSITION.INTERNAL);
		style.setBorderColor(new Color(206, 206, 206,255).getRGB());
		
		entryNameLabel = new GuiLabel(0,8, 0,9, entryName);
		entryNameLabel.getStyle().setRelativeWidth(1);
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
