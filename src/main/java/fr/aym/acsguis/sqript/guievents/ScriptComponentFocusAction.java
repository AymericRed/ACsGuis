package fr.aym.acsguis.sqript.guievents;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.event.listeners.IFocusListener;
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

@Loop(name = "gui_component_on_focus",
        pattern = "on component focus:",
        side = Side.CLIENT
)
@SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
public class ScriptComponentFocusAction extends GuiActionScriptLoop {
    @Override
    public void build(ScriptToken line, ScriptCompilationContext compileGroup) {
        compileGroup.add("type", TypeString.class);
    }

    @Override
    public void appendListener(Callable<ScriptContext> contextProvider, GuiComponent component) {
        component.addFocusListener(new IFocusListener() {
            @Override
            public void onFocus() {
                ScriptContext context1;//new ScriptContext(context); // ça empêche de "faire sortir" des variables : à ne pas utiliser
                try {
                    context1 = contextProvider.call();
                    context1.put(new ScriptTypeAccessor(new TypeString("focus"), "type"));
                    executeAction(context1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFocusLoose() {
                ScriptContext context1;//new ScriptContext(context); // ça empêche de "faire sortir" des variables : à ne pas utiliser
                try {
                    context1 = contextProvider.call();
                    context1.put(new ScriptTypeAccessor(new TypeString("focus_loose"), "type"));
                    executeAction(context1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
