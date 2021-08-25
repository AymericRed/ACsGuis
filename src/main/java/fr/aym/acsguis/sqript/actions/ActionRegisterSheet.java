package fr.aym.acsguis.sqript.actions;

import fr.aym.acsguis.api.ACsGuiApi;
import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.actions.ScriptAction;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import net.minecraft.util.ResourceLocation;

@Action(name = "Register css sheet",
        features = @Feature(
                name = "Register a css sheet",
                description = "Registers a css sheet for your css guis. You it in the \"on css load event\".",
                examples = "register sheet \"dynamx:css/test.css\"",
                pattern = "register css sheet {resource}")
)
public class ActionRegisterSheet extends ScriptAction {
    @Override
    public void execute(ScriptContext context) throws ScriptException {
        ScriptExpression firstParameter = getParameters().get(0);
        //ScriptManager.log.info(context.getVariable("script file") + " : " + firstParameter.get(context));
        //System.out.println(firstParameter.get(context).getClass());
        ACsGuiApi.registerStyleSheetToPreload(new ResourceLocation(firstParameter.get(context).toString()));
    }
}