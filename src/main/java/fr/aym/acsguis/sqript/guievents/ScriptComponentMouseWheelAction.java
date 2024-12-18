package fr.aym.acsguis.sqript.guievents;

import fr.aym.acsguis.component.GuiComponent;
import fr.nico.sqript.compiling.ScriptCompilationContext;
import fr.nico.sqript.compiling.ScriptToken;
import fr.nico.sqript.meta.Loop;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.primitive.TypeNumber;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.concurrent.Callable;

@Loop(name = "gui_component_on_mouse_wheel",
        pattern = "on component mouse wheel:",
        side = Side.CLIENT
)
@SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
public class ScriptComponentMouseWheelAction extends GuiActionScriptLoop {
    @Override
    public void build(ScriptToken line, ScriptCompilationContext compileGroup) {
        compileGroup.add("dWheel", TypeNumber.class);
    }

    @Override
    public void appendListener(Callable<ScriptContext> contextProvider, GuiComponent component) {
        component.addWheelListener((dWheel) -> {
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
