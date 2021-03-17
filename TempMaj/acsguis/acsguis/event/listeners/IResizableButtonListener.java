package fr.aym.acsguis.event.listeners;

import fr.aym.acsguis.component.GuiResizableButton;

public interface IResizableButtonListener {
	
	void onButtonUpdated(GuiResizableButton.ENUM_RESIZE_SIDE enumResizeSide);
	
}
