package fr.aym.acsguis.sqript.expressions;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.textarea.TextComponent;
import fr.aym.acsguis.sqript.component.ComponentUtils;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraftforge.fml.relauncher.SideOnly;

@Expression(name = "Modify a gui component properties",
        priority = 20,
        features = {
                @Feature(
                        name = "Set style of a gui component",
                        description = "The contained string should be a valid ACsGuis css code, see the specific doc",
                        examples = "set style of this component to \"color: green; width: 240px; horizontal-position: center;\"",
                        pattern = "style of {gui_component}",
                        side = Side.CLIENT),
                @Feature(
                        name = "Set css id of a gui component",
                        description = "Sets the css id of this component, so you can refer to it in your .css file",
                        examples = "set css id of this component to \"root\"",
                        pattern = "[css] id of {gui_component}",
                        side = Side.CLIENT),
                @Feature(
                        name = "Set css class of a gui component",
                        description = "Sets the css class of this component, so you can refer to it in your .css file",
                        examples = "set css class of this component to \"option_button\"",
                        pattern = "[css] class of {gui_component}",
                        side = Side.CLIENT),
                @Feature(
                        name = "Set text of a gui component that can contain text",
                        description = "Sets the text of this component only if it can contain text",
                        examples = "set text of this component to \"Hello World !\"",
                        pattern = "text of {gui_component}",
                        side = Side.CLIENT)
        }
)
@SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
public class ExprCssCode extends ScriptExpression {

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {
        ScriptType<GuiComponent> param = parameters[0];
        switch (getMatchedIndex()) {
            case 0:
                return new TypeString(param.getObject().getStyle().toString());
            case 1:
                return new TypeString(param.getObject().getCssId());
            case 2:
                return new TypeString(param.getObject().getCssClass());
            case 3:
                if (param.getObject() instanceof TextComponent)
                    return new TypeString(((TextComponent) param.getObject()).getText());
                else
                    throw new IllegalArgumentException(param.getObject() + " is not a TextComponent");
        }
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) {
        ScriptType<GuiComponent> param = parameters[0];
        boolean isReturn = false;
        switch (getMatchedIndex()) {
            case 0:
                param.getObject().setCssCode(to.getObject().toString());
                isReturn = true;
                break;
            case 1:
                param.getObject().setCssId(to.getObject().toString());
                isReturn = true;
                break;
            case 2:
                param.getObject().setCssClass(to.getObject().toString());
                isReturn = true;
                break;
            case 3:
                if (param.getObject() instanceof TextComponent) {
                    ((TextComponent) param.getObject()).setText(to.getObject().toString());
                    isReturn = true;
                } else {
                    isReturn = false;
                    throw new IllegalArgumentException(param.getObject() + " is not a TextComponent");
                }
                break;
        }
        if(isReturn){
            //Update component building context if we are compiling it
            if(ComponentUtils.lastAddedComponent == param.getObject()) {
                ComponentUtils.setComponentContext(param.getObject(), context);
            }
            return true;
        }
        return false;
    }
}
