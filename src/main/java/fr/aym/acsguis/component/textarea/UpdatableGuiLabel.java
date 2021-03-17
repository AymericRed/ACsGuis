package fr.aym.acsguis.component.textarea;

import java.util.function.Function;

public class UpdatableGuiLabel extends GuiLabel
{
    private final Function<String, String> formatter;

    public UpdatableGuiLabel(String pattern, Function<String, String> formatter) {
        super(pattern);
        this.formatter = formatter;
    }

    public Function<String, String> getFormatter() {
        return formatter;
    }

    @Override
    public String getText() {
        return formatter.apply(super.getText());
    }
}
