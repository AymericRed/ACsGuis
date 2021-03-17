package fr.aym.acsguis.component.textarea;

import fr.aym.acsguis.component.EnumComponentType;

public class GuiTextField extends GuiTextArea
{
	public GuiTextField() {
		super(0, 0, 0, 0);
	}

	public GuiTextField(int x, int y, int width, int height) {
		super(x, y, width, height);
		setMaxTextLength(50);
	}

	@Override
	public EnumComponentType getType() {
		return EnumComponentType.TEXT_FIELD;
	}

	@Override
	public boolean allowLineBreak() {
		return false;
	}

	@Override
	public boolean isScrollable() {
		return false;
	}
}
