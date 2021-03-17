package fr.aym.acsguis.component.textarea;

import java.util.regex.Pattern;

public class GuiNumberField extends GuiTextField
{
    public GuiNumberField() {
        super();
        setText("0");
        setRegexPattern(Pattern.compile("([-0-9.]+)"));
    }

    @Override
    public void writeText(String text) {
        super.writeText(text);
    }

    public float getNumberFromText() {
        if (getText() == null) return 0;
        float number;
        try {
            number = Float.parseFloat(getText());
        } catch (NumberFormatException nfe) {
            return 0;
        }
        return number;
    }
}
