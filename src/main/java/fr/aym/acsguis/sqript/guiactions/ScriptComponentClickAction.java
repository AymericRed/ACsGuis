package fr.aym.acsguis.sqript.guiactions;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.sqript.ComponentUtils;
import fr.aym.acsguis.sqript.block.ParseableComponent;
import fr.aym.acsguis.sqript.expressions.TypeComponent;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Loop;
import fr.nico.sqript.structures.*;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.primitive.TypeNumber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

@Loop(name = "gui_component_on_click",
        pattern = "on component click:",
        side = Side.CLIENT
)
public class ScriptComponentClickAction extends GuiActionScriptLoop {
    @Override
    public void appendListener(Callable<ScriptContext> contextProvider, GuiComponent<?> component) {
        component.addClickListener((x, y, b) -> {
            System.out.println("Running button action !!");
            ScriptContext context1;
            try {
                context1 = contextProvider.call();
                context1.put(new ScriptTypeAccessor(new TypeNumber(x), "click_x"));
                context1.put(new ScriptTypeAccessor(new TypeNumber(y), "click_y"));
                context1.put(new ScriptTypeAccessor(new TypeNumber(b), "click_button"));
                executeAction(context1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
