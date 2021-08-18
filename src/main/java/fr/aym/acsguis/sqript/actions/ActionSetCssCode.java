package fr.aym.acsguis.sqript.actions;

import fr.aym.acsguis.api.ACsGuiApi;
import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.textarea.GuiTextArea;
import fr.aym.acsguis.component.textarea.TextComponent;
import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.actions.ScriptAction;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import net.minecraft.util.ResourceLocation;
import scala.annotation.meta.param;

@Action(name = "Add component to panel",
        description ="",
        examples = "",
        patterns = {"set css [code] of {gui_component} to {string}",
                "set [css] id of {gui_component} to {string}",
                "set [css] class of {gui_component} to {string}",
                "set text of {gui_component} to {string}",
                "set {string} of {gui_component} to {string}"
                //TODO ETC
        }
)
public class ActionSetCssCode extends ScriptAction
{
    @Override
    @SuppressWarnings("unchecked")
    public void execute(ScriptContext context) throws ScriptException {
        ScriptType<GuiComponent<?>> param = getParameter(getMatchedIndex() == 4 ? 2 : 1).get(context);
        System.out.println("SET ON "+getMatchedIndex()+" "+param.getObject());
        switch (getMatchedIndex()) {
            case 0:
                param.getObject().setCssCode(getParameter(2).get(context).toString());
                break;
            case 1:
                param.getObject().setCssId(getParameter(2).get(context).toString());
                break;
            case 2:
                param.getObject().setCssClass(getParameter(2).get(context).toString());
                break;
            case 3:
                if(param.getObject() instanceof TextComponent)
                    ((TextComponent) param.getObject()).setText(getParameter(2).get(context).toString());
                else
                    throw new IllegalArgumentException(param.getObject()+" is not a TextComponent");
                System.out.println("So text: "+((TextComponent) param.getObject()).getText());
                break;
            case 4:
                //TODO PARSEABLE COMPONENTS ETC
                String optn = getParameter(1).toString();
                System.out.println("Try change "+optn+" : WIP");
                break;
        }
    }
}