package fr.aym.acsguis.component;

import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.event.listeners.mouse.IMouseClickListener;

public class GuiCheckBox extends GuiPanel implements IMouseClickListener
{
    protected String text;
    protected GuiCheckBoxButton checkButton;
    protected boolean isChecked;
	
	public GuiCheckBox() {
		this("");
	}
	
	public GuiCheckBox(String text) {
		super();
		add(checkButton = new GuiCheckBoxButton(0, 0, 11, 11));
		setText(text);

		addClickListener(this);
	}

    @Override
    public EnumComponentType getType() {
        return EnumComponentType.CHECKBOX;
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTicks) {
        
        if(text != null && !text.isEmpty()) {
            String str = mc.fontRenderer.trimStringToWidth(text, getWidth() - (checkButton.getWidth() + 2));

            if(str.length() < text.length()) {
                str = mc.fontRenderer.trimStringToWidth(text, getWidth() - (checkButton.getWidth() + 8)) + "...";
            }

            mc.fontRenderer.drawString(str, getScreenX() + checkButton.getWidth() + 2, getScreenY() + 2, getStyle().getForegroundColor());
            //drawString(mc.fontRenderer, str, getScreenX() + checkButton.getWidth() + 2, getScreenY() + 2, textColor);
        }
        
        super.drawForeground(mouseX, mouseY, partialTicks);
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        setChecked(!isChecked);
        checkButton.onMouseClicked(mouseX, mouseY, mouseButton);
    }
    
    public class GuiCheckBoxButton extends GuiButton
    {
        protected GuiCheckBoxButton(int x, int y, int width, int height) {
            super(x, y, width, height);
        }

        @Override
        public void drawForeground(int mouseX, int mouseY, float partialTicks) {
            if (isChecked())
                drawCenteredString(mc.fontRenderer, "x", getScreenX() + getWidth() / 2 + 1, getScreenY() + 1, GuiCheckBox.this.getStyle().getForegroundColor());
        }
    }

    public boolean isChecked() {
        return isChecked;
    }

    public GuiCheckBox setChecked(boolean isChecked) {
        this.isChecked = isChecked;
        return this;
    }

    public GuiCheckBox setText(String text) {
        this.text = text;
        return this;
    }

    public String getText(){
	    return text;
    }
    
    public GuiCheckBoxButton getCheckButton() {
		return checkButton;
	}
}
