package fr.aym.acsguis.sqript.guiactions;

import fr.aym.acsguis.component.GuiComponent;
import fr.nico.sqript.meta.Loop;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.primitive.TypeNumber;

@Loop(name = "gui_component_on_tick",
        pattern = "on component tick",
        side = Side.CLIENT
)
public class ScriptComponentTickAction extends GuiActionScriptLoop {
    @Override
    public void appendListener(ScriptContext context, GuiComponent<?> component) {
        component.addTickListener(() -> {
            executeAction(context);
        });
    }
}
