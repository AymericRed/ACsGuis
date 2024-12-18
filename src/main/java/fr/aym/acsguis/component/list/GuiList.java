package fr.aym.acsguis.component.list;

import fr.aym.acsguis.component.panel.GuiScrollPane;
import fr.aym.acsguis.component.list.slot.GuiBasicSlot;
import fr.aym.acsguis.component.list.slot.GuiSlot;

import java.util.ArrayList;
import java.util.List;

public class GuiList extends GuiScrollPane {
	
	protected int selectedEntryId = -1;
	protected GuiSlotList slotList;
	protected List<String> entries = new ArrayList<String>();
	
	public GuiList(List<String> entries)
	{
		if(entries != null) {
			this.entries.addAll(entries);
		}
		slotList = new GuiSlotList(this);
		slotList.updateSlotList();
		add(slotList);
	}
	
	public GuiSlot getSlotInstance(int n, String entryName) {
		return new GuiBasicSlot(this, n, entryName);
	}
	
	public void addEntry(String entry) {
		entries.add(entry);
		slotList.updateSlotList();
	}
	
	public GuiList setEntries(List<String> entries)
	{
		this.entries.clear();
		
		if(entries != null) {
			this.entries.addAll(entries);
		}
		
		slotList.updateSlotList();
		return this;
	}
	
	public GuiList removeEntry(String entry) {
		if(entry != null && entries.contains(entry)) {
			entries.remove(entry);
			slotList.updateSlotList();
		}
		return this;
	}
	
	public List<String> getEntries() {
		return entries;
	}
	
	public int getSelectedEntryId() {
		return selectedEntryId;
	}
	
	public GuiList setSelectedEntryId(int selectedEntryId) {
		this.selectedEntryId = selectedEntryId;
		return this;
	}
	
	public void updateFocus(int n) {
		slotList.updateFocus(n);
	}
	
	public int getListPaddingTop() {
		return 25;
	}
	
	public int getListPaddingBottom() {
		return 25;
	}
	
	public GuiSlotList getSlotList() {
		return slotList;
	}
}
