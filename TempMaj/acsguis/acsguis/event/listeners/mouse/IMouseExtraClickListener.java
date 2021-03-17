package fr.aym.acsguis.event.listeners.mouse;

public interface IMouseExtraClickListener {
	
	void onMouseDoubleClicked(int mouseX, int mouseY, int mouseButton);
	void onMousePressed(int mouseX, int mouseY, int mouseButton);
	void onMouseReleased(int mouseX, int mouseY, int mouseButton);
	
}
