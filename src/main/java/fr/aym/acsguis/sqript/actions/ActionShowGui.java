package fr.aym.acsguis.sqript.actions;

import fr.aym.acsguis.api.ACsGuiApi;
import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.layout.GuiScaler;
import fr.aym.acsguis.component.panel.GuiFrame;
import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.actions.ScriptAction;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.TypeArray;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Action(name = "Register css sheet",
        description ="Register a css sheet",
        examples = "register sheet \"dynamx:css/lol.css\"",
        patterns = "display css gui {gui_component} {string} {array}",
        side = Side.CLIENT
)
public class ActionShowGui extends ScriptAction
{
    @Override
    public void execute(ScriptContext context) throws ScriptException {
        ScriptExpression firstParameter = getParameters().get(0);
        //On peut accéder aux éléments du contexte depuis le code java également.
        ScriptManager.log.info(context.get("script file")+" : "+firstParameter.get(context));

        System.out.println("Value is "+firstParameter.getClass()+" "+firstParameter.get(context).getClass());
        System.out.println("Value is 2 "+firstParameter.getReturnType()+" "+firstParameter.get(context).getType());

        List<ResourceLocation> lt = new ArrayList<>();
        ((TypeArray)getParameters().get(2).get(context)).getObject().forEach(t -> {
            lt.add(new ResourceLocation(t.getObject().toString()));
        });

        System.out.println("Styles are "+lt);
        ACsGuiApi.asyncLoadThenShowGui(getParameters().get(1).get(context).toString(), () -> (GuiFrame) new GuiFrame(new GuiScaler.Identity()) {
            @Override
            public List<ResourceLocation> getCssStyles() {
                return lt;
            }
        }.add((GuiComponent<?>) firstParameter.get(context).getObject()));
    }
}