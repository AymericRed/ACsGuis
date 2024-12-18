package fr.aym.acsguis.sqript.expressions;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.style.AutoStyleHandler;
import fr.aym.acsguis.component.style.InternalComponentStyle;
import fr.aym.acsguis.cssengine.parsing.core.objects.CssIntValue;
import fr.aym.acsguis.cssengine.parsing.core.objects.CssRelativeValue;
import fr.aym.acsguis.cssengine.parsing.core.objects.CssStringValue;
import fr.aym.acsguis.cssengine.parsing.core.objects.CssValue;
import fr.aym.acsguis.cssengine.style.CssStyleApplier;
import fr.aym.acsguis.cssengine.style.CssStyleProperty;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperty;
import fr.aym.acsguis.sqript.component.ComponentProperties;
import fr.aym.acsguis.sqript.component.ParseableComponent;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraftforge.fml.relauncher.SideOnly;

@Expression(name = "Css style manipulation expression",
        features = {
                @Feature(
                        name = "Set other properties of gui components",
                        description = "Sets other properties of gui components, list in the doc",
                        examples = "set css \"checked_state\" of this_component to \"true\"",
                        pattern = "css {string} of {gui_component}",
                        side = Side.CLIENT),
                @Feature(
                        name = "Set other properties of the current component",
                        description = "Sets other properties of the current component, list in the doc. You MUST be in a component block or in a component event !",
                        examples = "set css \"color\" to \"green\"",
                        pattern = "css {string}",
                        side = Side.CLIENT)
        }
)
@SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
public class ExprCssStyle extends ScriptExpression {
    @Override
    public ScriptType<String> get(ScriptContext context, ScriptType[] parameters) {
        ScriptType<GuiComponent> param = getMatchedIndex() == 1 ?
                (ScriptType<GuiComponent>) context.getVariable("this_component") : parameters[1];
        String optn = parameters[0].getObject().toString();
        //System.out.println("Try get " + optn + " : WIP ON " + param.getObject());

        ComponentProperties<?, ?> property = findComponentProperty(param.getObject(), optn);
        if (property != null) {
            return new TypeString(property.getValueFromComponent(param.getObject()).toString());
        }

        EnumCssStyleProperty properties = findCssProperty(param.getObject(), optn);
        if (properties != null) {
            AutoStyleHandler.SimpleStyleFunction styleOverride = param.getObject().getStyleCustomizer().getStyleOverride(properties);
            if (styleOverride instanceof SqriptStyleFunction) {
                return new TypeString(((SqriptStyleFunction) styleOverride).getStringValue());
            }
        }
        return null;
    }

    private ComponentProperties<?, Object> findComponentProperty(GuiComponent component, String optn) {
        ParseableComponent componentType = ParseableComponent.find(component);
        for (ComponentProperties<?, ?> property : componentType.getProperties()) {
            if (property.getName().equals(optn)) {
                return (ComponentProperties<?, Object>) property;
            }
        }
        return null;
    }

    private EnumCssStyleProperty findCssProperty(GuiComponent component, String optn) {
        for (EnumCssStyleProperty properties : EnumCssStyleProperty.values()) {
            if (properties.key.equals(optn)) {
                return properties;
            }
        }
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) throws ScriptException.ScriptUndefinedReferenceException {
        ScriptType<GuiComponent> param = getMatchedIndex() == 1 ?
                (ScriptType<GuiComponent>) context.getVariable("this_component") : parameters[1];
        String optn = parameters[0].getObject().toString();
        //System.out.println("Try set " + optn + " : WIP ON " + param.getObject() + " to " + to.getObject());

        ComponentProperties<?, Object> property = findComponentProperty(param.getObject(), optn);
        if (property != null) {
            property.setValueOnComponent(param.getObject(), to.getObject());
            //System.out.println("Good found " + property);
            return true;
        }

        EnumCssStyleProperty properties = findCssProperty(param.getObject(), optn);
        if (properties != null) {
            param.getObject().getStyleCustomizer().putStyleOverride(properties, new SqriptStyleFunction(properties, to.getObject().toString()));
            //System.out.println("Injection success on " + properties.key + " on " + param.getObject());
            return true;
        }/* else {
            System.out.println("Fail property not found");
        }*/

        return false;
    }

    public static class SqriptStyleFunction implements AutoStyleHandler.SimpleStyleFunction {
        private final String stringValue;
        private final CssStyleProperty<Object> styleProperty;

        protected SqriptStyleFunction(EnumCssStyleProperty property, String value) {
            value = value.trim();
            this.stringValue = value;
            CssValue cssValue;
            if (!value.contains(" ") && (value.equals("0") || value.endsWith("px"))) {
                cssValue = new CssIntValue(Integer.parseInt(value.replace("px", "")));
            } else if (!value.contains(" ") && value.endsWith("%")) {
                cssValue = new CssRelativeValue(Integer.parseInt(value.replace("%", "")), CssValue.Unit.RELATIVE_TO_PARENT);
            } else if (!value.contains(" ") && value.endsWith("vw")) {
                cssValue = new CssRelativeValue(Integer.parseInt(value.replace("vw", "")), CssValue.Unit.RELATIVE_TO_WINDOW_WIDTH);
            } else if (!value.contains(" ") && value.endsWith("vh")) {
                cssValue = new CssRelativeValue(Integer.parseInt(value.replace("vh", "")), CssValue.Unit.RELATIVE_TO_WINDOW_HEIGHT);
            } else {
                if (value.startsWith("\""))
                    value = value.substring(1);
                if (value.endsWith("\""))
                    value = value.substring(0, value.length() - 1);
                //System.out.println("SET VALUE OF "+data[0]+" to "+value);
                cssValue = new CssStringValue(value);
            }
            this.styleProperty = new CssStyleProperty<>(property, cssValue);
        }

        public String getStringValue() {
            return stringValue;
        }

        @Override
        public void apply(InternalComponentStyle target) {
            ((CssStyleApplier) styleProperty.getProperty().applyFunction).apply(styleProperty, target);
        }
    }
}
