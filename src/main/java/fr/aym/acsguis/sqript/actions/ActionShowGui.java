package fr.aym.acsguis.sqript.actions;

import fr.aym.acsguis.sqript.block.ScriptBlockGuiFrame;
import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.actions.ScriptAction;
import fr.nico.sqript.blocks.ScriptBlock;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.Side;

@Action(name = "Show css gui",
        features = {
                @Feature(
                        name = "Show css gui",
                        description = "Shows the css frame gui matching the given name. The gui must be created in the same .sq file.",
                        examples = "display css gui \"monGui\"",
                        pattern = "display css gui {string}",
                        side = Side.CLIENT
                ),
                @Feature(
                        name = "Show css gui in another .sq file",
                        description = "Shows the css frame gui matching the given name. The second parameter is the .sq file name.",
                        examples = "display css gui \"monGui\" \"example_script\"",
                        pattern = "display css gui {string} {string}",
                        side = Side.CLIENT
                )})
public class ActionShowGui extends ScriptAction {
    @Override
    public void execute(ScriptContext context) throws ScriptException {
        String scriptName;
        if (getMatchedIndex() == 0) {
            //System.out.println("Line is " + getLine());
            //System.out.println("In script : " + getLine().getScriptInstance().getName());
            scriptName = getLine().getScriptInstance().getName();
        } else {
            //System.out.println("Line is " + getLine());
            //System.out.println("In script over : " + getParameter(2));
            scriptName = getParameter(2).get(context).toString();
        }
        showGui(scriptName, context);
    }

    private void showGui(String scriptName, ScriptContext context) {
        /*System.out.println("SC " + ScriptManager.getScriptFromName(scriptName));
        System.out.println("SC2 " + ScriptManager.getScriptFromName(scriptName).getBlocksOfClass(ScriptBlockGuiFrame.class));
        System.out.println(ScriptManager.getScriptFromName(scriptName).getBlocksOfClass(ScriptBlock.class));
        System.out.println(getParameters());*/

        //TODO DIRECT REFERENCE TO THE GUI FRAME WITH A CUSTOM PARSER IN ScriptDecoder
        ScriptBlockGuiFrame f = (ScriptBlockGuiFrame) ScriptManager.getScriptFromName(scriptName).getBlocksOfClass(ScriptBlockGuiFrame.class).stream().filter(g -> {
            try {
                return ((ScriptBlockGuiFrame) g).getName().equalsIgnoreCase(getParameter(1).get(context).toString());
            } catch (ScriptException e) {
                e.printStackTrace();
                return false;
            }
        }).findFirst().get();
        f.execute(context);
    }
}