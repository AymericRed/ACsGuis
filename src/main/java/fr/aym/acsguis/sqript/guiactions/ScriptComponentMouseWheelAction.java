package fr.aym.acsguis.sqript.guiactions;

import fr.aym.acsguis.component.GuiComponent;
import fr.nico.sqript.meta.Loop;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.primitive.TypeNumber;

import java.util.concurrent.Callable;

@Loop(name = "gui_component_on_mouse_wheel",
        pattern = "on component mouse wheel:",
        side = Side.CLIENT
)
public class ScriptComponentMouseWheelAction extends GuiActionScriptLoop {
    @Override
    public void appendListener(Callable<ScriptContext> contextProvider, GuiComponent<?> component) {
        component.addWheelListener((dWheel) -> {
            System.out.println("Running button action !!");
            ScriptContext context1;
            try {
                context1 = contextProvider.call();
                context1.put(new ScriptTypeAccessor(new TypeNumber(dWheel), "dWheel"));
                executeAction(context1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
