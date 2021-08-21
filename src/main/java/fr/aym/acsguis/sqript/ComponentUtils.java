package fr.aym.acsguis.sqript;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.sqript.block.ComponentProperties;
import fr.aym.acsguis.sqript.block.ParseableComponent;
import fr.aym.acsguis.sqript.expressions.TypeComponent;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptTypeAccessor;

public class ComponentUtils {
    public static GuiComponent<?> lastAddedComponent;

    public static void pushComponentVariables(GuiComponent<?> component, ScriptContext context) {
        if (component.getParent() != null) {
            fillProperties(component.getParent(), context, false);
        }
        ComponentUtils.lastAddedComponent = component;

        //System.out.println("PUSH " + component);
        setComponentContext(component, context);
    }

    public static void setComponentContext(GuiComponent<?> component, ScriptContext context) {
        context.put(new ScriptTypeAccessor(new TypeComponent(component), "this_component"));

        ParseableComponent type = ParseableComponent.find(component);
        type.setupContext(context, component);
    }

    public static void popComponentVariables(ScriptContext context) {
        GuiComponent<?> lastComponent = ComponentUtils.lastAddedComponent;

        ParseableComponent component = ParseableComponent.find(lastComponent);
        ComponentUtils.lastAddedComponent = lastComponent.getParent();
        fillProperties(lastComponent, context, true);
        for(ComponentProperties<?, ?> property : component.getProperties()) {
            context.remove(property.getName());
        }

        //System.out.println("POP " + lastComponent + " get " + lastAddedComponent);

        if(lastAddedComponent != null)
            setComponentContext(lastAddedComponent, context);
    }

    public static void fillProperties(GuiComponent<?> lastComponent, ScriptContext context, boolean popping) {
        //System.out.println("FILLING " + lastComponent);
        ParseableComponent type = ParseableComponent.find(lastComponent);
        type.fillComponent(context, lastComponent);
    }
}
