package fr.aym.acsguis.sqript.actions;

import fr.aym.acsguis.api.ACsGuiApi;
import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.actions.ScriptAction;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.primitive.TypeResource;
import net.minecraft.util.ResourceLocation;

@Action(name = "Register css sheet",
        description ="Register a css sheet",
        examples = "register sheet \"dynamx:css/lol.css\"",
        patterns = "register css sheet {resource}"
)
public class ActionRegisterSheet extends ScriptAction
{
    @Override
    public void execute(ScriptContext context) throws ScriptException {
        ScriptExpression firstParameter = getParameters().get(0);
        //On peut accéder aux éléments du contexte depuis le code java également.
        ScriptManager.log.info(context.get("script file")+" : "+firstParameter.get(context));
        System.out.println(firstParameter.get(context).getClass());
        ACsGuiApi.registerStyleSheetToPreload(new ResourceLocation(firstParameter.get(context).toString()));
    }
}