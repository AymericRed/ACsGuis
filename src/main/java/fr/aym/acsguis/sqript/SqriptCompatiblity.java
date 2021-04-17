package fr.aym.acsguis.sqript;

import com.helger.commons.functional.ITriConsumer;
import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.textarea.GuiTextArea;
import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.blocks.ScriptBlock;

import java.util.regex.Pattern;

public class SqriptCompatiblity implements SqriptSupport
{
    public static final ITriConsumer<GuiComponent<?>, String, ScriptBlock.ScriptLineBlock> TEXT_AREA_PROPERTIES_PARSER = (c, n, b) -> {
        if(n.equalsIgnoreCase("text")) {
            ((GuiTextArea)c).setText(b.getRawContent());
        }
        else if(n.equalsIgnoreCase("max_text_length")) {
            ((GuiTextArea)c).setMaxTextLength(Integer.parseInt(b.getRawContent()));
        }
        else if(n.equalsIgnoreCase("hint_text")) {
            ((GuiTextArea)c).setHintText(b.getRawContent());
        }
        else if(n.equalsIgnoreCase("regex")) {
            ((GuiTextArea)c).setRegexPattern(Pattern.compile(b.getRawContent()));
        }
        else
            throw new IllegalArgumentException("Cannot set "+n+" on component of type text_area, text_field or passworld_field");
    };

    @Override
    public boolean isSqriptLoaded() {
        return true;
    }

    @Override
    public void onCssInit() {
        ScriptManager.callEvent(new EventOnCssInit());
    }
}
