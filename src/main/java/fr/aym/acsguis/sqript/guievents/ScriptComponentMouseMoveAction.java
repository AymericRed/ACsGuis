package fr.aym.acsguis.sqript.guievents;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.event.listeners.mouse.IMouseMoveListener;
import fr.nico.sqript.compiling.ScriptCompilationContext;
import fr.nico.sqript.compiling.ScriptToken;
import fr.nico.sqript.meta.Loop;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.primitive.TypeNumber;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.concurrent.Callable;

@Loop(name = "gui_component_mouse_move",
        pattern = "on component mouse move:",
        side = Side.CLIENT
)
@SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
public class ScriptComponentMouseMoveAction extends GuiActionScriptLoop {
    @Override
    public void build(ScriptToken line, ScriptCompilationContext compileGroup) {
        compileGroup.add("type", TypeString.class);
        compileGroup.add("mouse_x", TypeNumber.class);
        compileGroup.add("mouse_y", TypeNumber.class);
    }

    @Override
    public void appendListener(Callable<ScriptContext> contextProvider, GuiComponent component) {
        component.addMoveListener(new IMouseMoveListener() {
            @Override
            public void onMouseMoved(int mouseX, int mouseY) {
                ScriptContext context1;//new ScriptContext(context); // ça empêche de "faire sortir" des variables : à ne pas utiliser
                try {
                    context1 = contextProvider.call();
                    context1.put(new ScriptTypeAccessor(new TypeString("mouse_moved"), "type"));
                    context1.put(new ScriptTypeAccessor(new TypeNumber(mouseX), "mouse_x"));
                    context1.put(new ScriptTypeAccessor(new TypeNumber(mouseY), "mouse_y"));
                    executeAction(context1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMouseHover(int mouseX, int mouseY) {
                ScriptContext context1;//new ScriptContext(context); // ça empêche de "faire sortir" des variables : à ne pas utiliser
                try {
                    context1 = contextProvider.call();
                    context1.put(new ScriptTypeAccessor(new TypeString("mouse_hover"), "type"));
                    context1.put(new ScriptTypeAccessor(new TypeNumber(mouseX), "mouse_x"));
                    context1.put(new ScriptTypeAccessor(new TypeNumber(mouseY), "mouse_y"));
                    executeAction(context1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMouseUnhover(int mouseX, int mouseY) {
                ScriptContext context1;//new ScriptContext(context); // ça empêche de "faire sortir" des variables : à ne pas utiliser
                try {
                    context1 = contextProvider.call();
                    context1.put(new ScriptTypeAccessor(new TypeString("mouse_unhover"), "type"));
                    context1.put(new ScriptTypeAccessor(new TypeNumber(mouseX), "mouse_x"));
                    context1.put(new ScriptTypeAccessor(new TypeNumber(mouseY), "mouse_y"));
                    executeAction(context1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
