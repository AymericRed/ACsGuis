package fr.aym.acsguis.component.textarea;

import java.util.List;
import java.util.function.Function;

public class UpdatableGuiLabel extends GuiLabel
{
    private final Function<String, String> formatter;
    private String lastText;

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

    @Override
    public List<String> getCachedTextLines() {
        String text = getRenderedText();
        if(!text.equals(lastText)) {
            lastText = text;
            cachedTextLines = null;
        }
        return super.getCachedTextLines();
    }
}
