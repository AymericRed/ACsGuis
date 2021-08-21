package fr.aym.acsguis.sqript.guievents;

import fr.aym.acsguis.component.GuiComponent;
import fr.nico.sqript.meta.Loop;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.Side;

import java.util.concurrent.Callable;

@Loop(name = "gui_component_close",
        pattern = "on component close:",
        side = Side.CLIENT
)
public class ScriptComponentCloseAction extends GuiActionScriptLoop {
    @Override
    public void appendListener(Callable<ScriptContext> contextProvider, GuiComponent<?> component) {
        component.addCloseListener(() -> {
            System.out.println("Running button action !!");
            try {
                executeAction(contextProvider.call());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
