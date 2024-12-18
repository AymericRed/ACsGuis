package fr.aym.acsguis.sqript.guievents;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.event.listeners.mouse.IMouseExtraClickListener;
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

@Loop(name = "gui_component_extra_click",
        pattern = "on component mouse click advanced:",
        side = Side.CLIENT
)
@SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
public class ScriptComponentExtraClickAction extends GuiActionScriptLoop {
    @Override
    public void build(ScriptToken line, ScriptCompilationContext compileGroup) {

        compileGroup.add("type", TypeString.class);
        compileGroup.add("click_x", TypeNumber.class);
        compileGroup.add("click_y", TypeNumber.class);
        compileGroup.add("click_button", TypeNumber.class);
    }

    @Override
    public void appendListener(Callable<ScriptContext> contextProvider, GuiComponent component) {
        component.addExtraClickListener(new IMouseExtraClickListener() {
            @Override
            public void onMouseDoubleClicked(int mouseX, int mouseY, int mouseButton) {
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
