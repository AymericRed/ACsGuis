package fr.aym.acsguis.sqript.guiactions;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.sqript.expressions.TypeComponent;
import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.structures.IScript;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptLoop;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.types.primitive.TypeNumber;
import net.minecraft.client.gui.Gui;

public abstract class GuiActionScriptLoop extends ScriptLoop
{
    @Override
    public void execute(ScriptContext context) throws ScriptException {
        GuiComponent<?> component = ((TypeComponent) context.getAccessor("this_component").element).getObject();
        System.out.println("Action : component is " + component);
        appendListener(context, component);
    }

    @Override
    public IScript run(ScriptContext context) throws ScriptException {
        execute(context);
        return getNext(context);
    }
    
    public abstract void appendListener(ScriptContext context, GuiComponent<?> component);
    
    public void executeAction(ScriptContext context) {
        try {
            System.out.println("Wrapped is " + getWrapped());
            System.out.println("Wrapped next is " + getWrapped().next + " " + getWrapped().getNext(context));
            IScript next = getWrapped();
            int tab = ScriptDecoder.getTabLevel(this.getLine().getText());
            System.out.println("Running " + tab + " deep !");
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
            //TODO MESSAGE IN CHAT
            e.printStackTrace();
        }
    }
}
