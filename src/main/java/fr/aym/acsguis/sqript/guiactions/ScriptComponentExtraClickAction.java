package fr.aym.acsguis.sqript.guiactions;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.event.listeners.mouse.IMouseExtraClickListener;
import fr.nico.sqript.meta.Loop;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.primitive.TypeNumber;
import fr.nico.sqript.types.primitive.TypeString;

import java.util.concurrent.Callable;

@Loop(name = "gui_component_extra_click",
        pattern = "on component mouse click advanced:",
        side = Side.CLIENT
)
public class ScriptComponentExtraClickAction extends GuiActionScriptLoop {
    @Override
    public void appendListener(Callable<ScriptContext> contextProvider, GuiComponent<?> component) {
        component.addExtraClickListener(new IMouseExtraClickListener() {
            @Override
            public void onMouseDoubleClicked(int mouseX, int mouseY, int mouseButton) {
                System.out.println("Running button action !!");
                ScriptContext context1;//new ScriptContext(context); // ça empêche de "faire sortir" des variables : à ne pas utiliser
                try {
                    context1 = contextProvider.call();
                    context1.put(new ScriptTypeAccessor(new TypeString("double_click"), "type"));
                    context1.put(new ScriptTypeAccessor(new TypeNumber(mouseX), "click_x"));
                    context1.put(new ScriptTypeAccessor(new TypeNumber(mouseY), "click_y"));
                    context1.put(new ScriptTypeAccessor(new TypeNumber(mouseButton), "click_button"));
                    executeAction(context1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMousePressed(int mouseX, int mouseY, int mouseButton) {
                System.out.println("Running button action !!");
                ScriptContext context1;//new ScriptContext(context); // ça empêche de "faire sortir" des variables : à ne pas utiliser
                try {
                    context1 = contextProvider.call();
                    context1.put(new ScriptTypeAccessor(new TypeString("mouse_press"), "type"));
                    context1.put(new ScriptTypeAccessor(new TypeNumber(mouseX), "click_x"));
                    context1.put(new ScriptTypeAccessor(new TypeNumber(mouseY), "click_y"));
                    context1.put(new ScriptTypeAccessor(new TypeNumber(mouseButton), "click_button"));
                    executeAction(context1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {
                System.out.println("Running button action !!");
                ScriptContext context1;//new ScriptContext(context); // ça empêche de "faire sortir" des variables : à ne pas utiliser
                try {
                    context1 = contextProvider.call();
                    context1.put(new ScriptTypeAccessor(new TypeString("mouse_release"), "type"));
                    context1.put(new ScriptTypeAccessor(new TypeNumber(mouseX), "click_x"));
                    context1.put(new ScriptTypeAccessor(new TypeNumber(mouseY), "click_y"));
                    context1.put(new ScriptTypeAccessor(new TypeNumber(mouseButton), "click_button"));
                    executeAction(context1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
