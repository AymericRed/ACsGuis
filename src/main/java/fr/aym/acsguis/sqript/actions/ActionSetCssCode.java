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
        patterns = {"override css [code] of {gui_component} with {string}",
                "override css id of {gui_component} with {string}",
                "override css class of {gui_component} with {string}", //TODO TEST IT
                "override label of {gui_component} with {string}"
        }
)
public class ActionSetCssCode extends ScriptAction
{
    @Override
    @SuppressWarnings("unchecked")
    public void execute(ScriptContext context) throws ScriptException {
        ScriptType<GuiComponent<?>> param = getParameter(0).get(context);
        switch (getMatchedIndex()) {
            case 0:
                param.getObject().setCssCode(getParameter(1).get(context).toString());
                break;
            case 1:
                param.getObject().setCssId(getParameter(1).get(context).toString());
                break;
            case 2:
                param.getObject().setCssClass(getParameter(1).get(context).toString());
                break;
            case 3:
                if(param.getObject() instanceof TextComponent)
                    ((TextComponent) param.getObject()).setText(getParameter(1).get(context).toString());
                else
                    throw new IllegalArgumentException(param.getObject()+" is not a TextComponent");
                break;
        }
    }
}