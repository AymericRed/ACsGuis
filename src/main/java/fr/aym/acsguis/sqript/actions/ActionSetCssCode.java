package fr.aym.acsguis.sqript.actions;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.textarea.TextComponent;
import fr.aym.acsguis.sqript.component.ComponentProperties;
import fr.aym.acsguis.sqript.component.ParseableComponent;
import fr.nico.sqript.actions.ScriptAction;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;

@Action(name = "Modify a gui component properties",
        priority = 20, //Override ActDefinition
        features = {
                @Feature(
                        name = "Set style of a gui component",
                        description = "The contained string should be a valid ACsGuis css code, see the specific doc",
                        examples = "set style of this component to \"color: green; width: 240px; horizontal-position: center;\"",
                        pattern = "set style of {gui_component} to {string}"),
                @Feature(
                        name = "Set css id of a gui component",
                        description = "Sets the css id of this component, so you can refer to it in your .css file",
                        examples = "set css id of this component to \"root\"",
                        pattern = "set [css] id of {gui_component} to {string}"),
                @Feature(
                        name = "Set css class of a gui component",
                        description = "Sets the css class of this component, so you can refer to it in your .css file",
                        examples = "set css class of this component to \"option_button\"",
                        pattern = "set [css] class of {gui_component} to {string}"),
                @Feature(
                        name = "Set text of a gui component that can contain text",
                        description = "Sets the text of this component only if it can contain text",
                        examples = "set text of this component to \"Hello World !\"",
                        pattern = "set text of {gui_component} to {string}"),
                @Feature(
                        name = "Set other properties of gui components",
                        description = "Sets other properties of gui components, list in the doc", //FIXME LIST IN DOC
                        examples = "set checked_state of this component to true",
                        pattern = "set {string} of {gui_component} to {string}")}
)
public class ActionSetCssCode extends ScriptAction {
    @Override
    @SuppressWarnings("unchecked")
    public void execute(ScriptContext context) throws ScriptException {
        ScriptType<GuiComponent<?>> param = getParameter(getMatchedIndex() == 4 ? 2 : 1).get(context);
        System.out.println("SET ON " + getMatchedIndex() + " " + param.getObject());
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
                if (param.getObject() instanceof TextComponent)
                    ((TextComponent) param.getObject()).setText(getParameter(2).get(context).toString());
                else
                    throw new IllegalArgumentException(param.getObject() + " is not a TextComponent");
                System.out.println("So text: " + ((TextComponent) param.getObject()).getText());
                break;
            case 4:
                String optn = getParameter(1).toString();
                System.out.println("Try change " + optn + " : WIP");
                boolean found = false;
                ParseableComponent componentType = ParseableComponent.find(param.getObject());
                for (ComponentProperties<?, ?> property : componentType.getProperties()) {
                    if (property.getName().equals(optn)) {
                        ((ComponentProperties<?, Object>) property).setValueOnComponent(param.getObject(), getParameter(2).get(context).getObject());
                        System.out.println("Success on " + property.getName());
                        found = true;
                        break;
                    }
                }
                if (!found)
                    throw new ScriptException.ScriptBadVariableNameException(getLine());
                break;
        }
    }
}