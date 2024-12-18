package fr.aym.acsguis.sqript.guievents;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.event.listeners.IRenderListener;
import fr.nico.sqript.compiling.ScriptCompilationContext;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptToken;
import fr.nico.sqript.meta.Loop;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.primitive.TypeNumber;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.concurrent.Callable;

@Loop(name = "gui_component_on_render_background",
        pattern = "on component render background:",
        side = Side.CLIENT
)
@SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
public class ScriptComponentRenderBackgroundAction extends GuiActionScriptLoop {
    @Override
    public void build(ScriptToken line, ScriptCompilationContext compileGroup) {
        compileGroup.add("render_x", TypeNumber.class);
        compileGroup.add("render_y", TypeNumber.class);
        compileGroup.add("render_width", TypeNumber.class);
        compileGroup.add("render_height", TypeNumber.class);
    }

    @Override
    public void appendListener(Callable<ScriptContext> contextProvider, GuiComponent component) {
        component.addRenderListener(new IRenderListener() {
            @Override
            public void onRenderBackground() {
                ScriptContext context1;
                try {
                    context1 = contextProvider.call();
                    context1.put(new ScriptTypeAccessor(new TypeNumber(component.getRenderMinX()), "render_x"));
                    context1.put(new ScriptTypeAccessor(new TypeNumber(component.getRenderMinY()), "render_y"));
                    context1.put(new ScriptTypeAccessor(new TypeNumber(component.getWidth()), "render_width"));
                    context1.put(new ScriptTypeAccessor(new TypeNumber(component.getHeight()), "render_height"));
                    executeAction(context1);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to post render background of " + component, e);
                }
            }

            @Override
            public void onRenderForeground() {
            }
        });
    }
}
