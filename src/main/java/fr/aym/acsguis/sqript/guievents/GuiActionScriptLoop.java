package fr.aym.acsguis.sqript.guievents;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.sqript.expressions.TypeComponent;
import fr.nico.sqript.compiling.ScriptCompilationContext;
import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptToken;
import fr.nico.sqript.structures.IScript;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptLoop;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.concurrent.Callable;

@SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
public abstract class GuiActionScriptLoop extends ScriptLoop {
    @Override
    public abstract void build(ScriptToken line, ScriptCompilationContext compileGroup);

    @Override
    public void execute(ScriptContext context) throws ScriptException {
        GuiComponent component = ((TypeComponent) context.getAccessor("this_component").element).getObject();
        //System.out.println("Action : component is " + component);
        appendListener(() -> {
            ScriptContext context1 = new ScriptContext(context);
            context1.put(new ScriptTypeAccessor(new TypeComponent(component), "this_component"));
            return context1;
        }, component);
    }

    @Override
    public IScript run(ScriptContext context) throws ScriptException {
        execute(context);
        return getNext(context);
    }

    public abstract void appendListener(Callable<ScriptContext> contextProvider, GuiComponent component);

    public void executeAction(ScriptContext context) {
        try {
            //System.out.println("Wrapped is " + getWrapped());
            //System.out.println("Wrapped next is " + getWrapped().next + " " + getWrapped().getNext(context));
            IScript next = getWrapped();
            int tab = ScriptDecoder.getTabLevel(this.getLine().getText());
            //System.out.println("Running " + tab + " deep !");
            do {
                try {
                    //System.out.println(">> Executing : "+next.getClass().getSimpleName()+" with "+l(context.hashCode())+(next.line!=null?" at n°"+next.line.number:""));
                    next = next.run(context);
                    //System.out.println("> Next to run is : "+(next==null?"null":next.getClass().getSimpleName()));
                } catch (Exception e) {
                    if (e instanceof ScriptException)
                        throw e;
                    else {
                        e.printStackTrace();
                        throw new ScriptException.ScriptWrappedException(next.getLine(), e);
                    }
                }


            } while (next != null && ScriptDecoder.getTabLevel(next.getLine().getText()) > tab);
        } catch (ScriptException e) {
            e.printStackTrace();
            if(Minecraft.getMinecraft().player != null)
                Minecraft.getMinecraft().player.sendMessage(new TextComponentString("\247c"+e.getMessage()).setStyle(new Style().setColor(TextFormatting.RED)));
        }
    }
}
