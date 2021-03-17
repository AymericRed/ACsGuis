package fr.aym.acsguis.component.textarea;

public class GuiColorField extends GuiTextField {

    public GuiColorField() {
        super();
        setText(String.valueOf(0));
    }

    @Override
    public GuiTextArea setText(String text) {
        if (!text.isEmpty()) {
            int color = (int) Float.parseFloat(text);
            if (color >= 255) {
                this.text = "255";
            } else if (color <= 0) {
                this.text = "0";
            } else {
                this.text = text;
            }
        } else
            this.text = text;
        return this;
    }
}
