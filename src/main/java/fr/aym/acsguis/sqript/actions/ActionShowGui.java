package fr.aym.acsguis.sqript.actions;

import fr.aym.acsguis.api.ACsGuiApi;
import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.layout.GuiScaler;
import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.sqript.block.ScriptBlockGuiFrame;
import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.actions.ScriptAction;
import fr.nico.sqript.blocks.ScriptBlock;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.TypeArray;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

@Action(name = "Show css gui",
        features = {
                @Feature(
                        name = "Fetaure 1",
                        description = "Register a css sheet",
                        examples = "register sheet \"dynamx:css/lol.css\"",
                        pattern = "display css gui {gui_component} {string} {array}",
                        side = Side.CLIENT
                ), //TODO
                @Feature(
                        name = "Fetaure 2",
                        description = "Register a css sheet",
                        examples = "register sheet \"dynamx:css/lol.css\"",
                        pattern = "display css gui {string}",
                        side = Side.CLIENT
                )})
public class ActionShowGui extends ScriptAction {
    @Override
    public void execute(ScriptContext context) throws ScriptException {
        if (getMatchedIndex() == 1) //TODO TEST WORKING GOOD
        {
            System.out.println("Line is " + getLine());
            System.out.println("SC " + ScriptManager.getScriptFromName("salemoche"));
            System.out.println("SC2 " + ScriptManager.getScriptFromName("salemoche").getBlocksOfClass(ScriptBlockGuiFrame.class));
            System.out.println(ScriptManager.getScriptFromName("salemoche").getBlocksOfClass(ScriptBlock.class));
            System.out.println(getParameters());
            ScriptBlockGuiFrame f = (ScriptBlockGuiFrame) ScriptManager.getScriptFromName("salemoche").getBlocksOfClass(ScriptBlockGuiFrame.class).stream().filter(g -> {
                try {
                    return ((ScriptBlockGuiFrame) g).getName().equalsIgnoreCase(getParameter(1).get(context).toString());
                } catch (ScriptException e) {
                    e.printStackTrace();
                    return false;
                }
            }).findFirst().get();
            f.execute(context);
        } else {
            ScriptExpression firstParameter = getParameters().get(0);
            //On peut accéder aux éléments du contexte depuis le code java également.
            ScriptManager.log.info(context.getVariable("script file") + " : " + firstParameter.get(context));

            System.out.println("Value is " + firstParameter.getClass() + " " + firstParameter.get(context).getClass());
            System.out.println("Value is 2 " + firstParameter.getReturnType() + " " + firstParameter.get(context).getType());

            List<ResourceLocation> lt = new ArrayList<>();
            ((TypeArray) getParameters().get(2).get(context)).getObject().forEach(t -> {
                lt.add(new ResourceLocation(t.getObject().toString()));
            });

            System.out.println("Styles are " + lt);
            ACsGuiApi.asyncLoadThenShowGui(getParameters().get(1).get(context).toString(), () -> (GuiFrame) new GuiFrame(new GuiScaler.Identity()) {
                @Override
                public List<ResourceLocation> getCssStyles() {
                    return lt;
                }
            }.add((GuiComponent<?>) firstParameter.get(context).getObject()));
        }
    }
}