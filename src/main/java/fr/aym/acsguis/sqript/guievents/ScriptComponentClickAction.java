package fr.aym.acsguis.sqript.guievents;

import fr.aym.acsguis.component.GuiComponent;
import fr.nico.sqript.compiling.ScriptCompilationContext;
import fr.nico.sqript.compiling.ScriptToken;
import fr.nico.sqript.meta.Loop;
import fr.nico.sqript.structures.*;
import fr.nico.sqript.types.primitive.TypeNumber;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.concurrent.Callable;

@Loop(name = "gui_component_on_click",
        pattern = "on component click:",
        side = Side.CLIENT
)
@SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
public class ScriptComponentClickAction extends GuiActionScriptLoop {
    @Override
    public void build(ScriptToken line, ScriptCompilationContext compileGroup) {
        compileGroup.add("click_x", TypeNumber.class);
        compileGroup.add("click_y", TypeNumber.class);
        compileGroup.add("click_button", TypeNumber.class);
    }

    @Override
    public void appendListener(Callable<ScriptContext> contextProvider, GuiComponent component) {
        component.addClickListener((x, y, b) -> {
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
