package fr.aym.acsguis.component.list.slot;

import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.event.listeners.IFocusListener;
import fr.aym.acsguis.event.listeners.mouse.IMouseClickListener;
import fr.aym.acsguis.component.list.GuiList;

public abstract class GuiSlot extends GuiPanel implements IFocusListener, IMouseClickListener {
	
	protected final GuiList list;
	protected final String entryName;
	protected final int entryId;
	
	public GuiSlot(GuiList list, int entryId, String entryName)
	{
		this.list = list;
		this.entryId = entryId;
		this.entryName = entryName;
		setCanLooseFocus(false);
		addFocusListener(this);
		addClickListener(this);
	}
	
	@Override
	public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
		list.updateFocus(entryId);
	}
	
	@Override
	public void onFocus() {
		list.setSelectedEntryId(entryId);
	}
	
	@Override
	public void onFocusLoose() {
		list.setSelectedEntryId(-1);
	}
	
	public String getEntryName() {
		return entryName;
	}
	
	public int getEntryId() {
		return entryId;
	}
	
}