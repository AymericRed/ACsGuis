package fr.aym.acsguis.sqript;

import fr.nico.sqript.ScriptManager;

public class SqriptCompatiblity implements SqriptSupport
{
    @Override
    public boolean isSqriptLoaded() {
        return true;
    }

    @Override
    public void onCssInit() {
        ScriptManager.callEvent(new EventOnCssInit());
    }
}
