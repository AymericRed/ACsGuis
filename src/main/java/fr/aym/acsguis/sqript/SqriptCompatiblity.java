package fr.aym.acsguis.sqript;

import fr.aym.acsguis.sqript.block.ComponentProperties;
import fr.aym.acsguis.sqript.events.EventOnCssInit;
import fr.nico.sqript.ScriptManager;

public class SqriptCompatiblity implements SqriptSupport
{
    public static final ComponentProperties<?, ?>[] TEXT_AREA_PROPERTIES_PARSER = new ComponentProperties[] {ComponentProperties.TEXT, ComponentProperties.MAX_TEXT_LENGTH, ComponentProperties.HINT_TEXT, ComponentProperties.REGEX};

    @Override
    public boolean isSqriptLoaded() {
        return true;
    }

    @Override
    public void onCssInit() {
        ScriptManager.callEvent(new EventOnCssInit());
    }
}
