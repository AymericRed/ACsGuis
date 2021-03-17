package fr.aym.acsguis.component.entity.list;

import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.event.listeners.IFocusListener;
import fr.aym.acsguis.gui.GuiList;
import fr.aym.acsguis.component.list.slot.GuiSlot;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GuiSlotList extends GuiPanel {
	
	protected final GuiList list;
	protected final List<GuiSlot> slots = new ArrayList<GuiSlot>();
	
	public GuiSlotList(GuiList list) {
		super(0, 0, 0, 0);
		this.list = list;
		style.setRelativeWidth(1);
		style.setBackgroundColor(new Color(0,0,0,0).getRGB());
	}
	
	public GuiSlot getSlotInstance(int n, String entryName) {
		return list.getSlotInstance(n, entryName);
	}
	
	@Override
	public int getHeight() {
		return list.getListPaddingBottom() + getListSlotsHeight();
	}
	
	protected int getListSlotsHeight()
	{
		int maxHeight = 0;
		
		for(GuiSlot slot : slots)
		{
			int h = slot.getY() + slot.getHeight();
			
			if(h > maxHeight) {
				maxHeight = h;
			}
		}
		
		return maxHeight;
	}
	
	public void updateSlotList()
	{
		for(GuiSlot slot : slots) {
			remove(slot);
		}
		
		slots.clear();
		
		for(int i = 0; i < list.getEntries().size(); i++) {
			GuiSlot listSlot = getSlotInstance(i, list.getEntries().get(i));
			slots.add(listSlot);
			add(listSlot);
		}
		
		list.updateSlidersVisibility();
	}
	
	public void updateFocus(int n)
	{
		for(GuiSlot slot : slots) {
			boolean wasFocused = slot.isFocused();
			slot.setFocused(slot.getEntryId() == n);
			
			if(slot.isFocused() && !wasFocused) {
				for(IFocusListener focusListener : slot.getFocusListeners()) {
					focusListener.onFocus();
				}
			} else if(!slot.isFocused() && wasFocused) {
				for(IFocusListener focusListener : slot.getFocusListeners()) {
					focusListener.onFocusLoose();
				}
			}
		}
	}
	
	public List<GuiSlot> getSlots() {
		return slots;
	}
	
}