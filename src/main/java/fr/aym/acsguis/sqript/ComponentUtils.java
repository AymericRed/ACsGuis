package fr.aym.acsguis.sqript;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.textarea.TextComponent;
import fr.aym.acsguis.sqript.block.ParseableComponent;
import fr.aym.acsguis.sqript.expressions.TypeComponent;
import fr.aym.acsguis.sqript.expressions.TypePanelLayout;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.types.primitive.TypeString;

import java.util.ArrayDeque;

public class ComponentUtils {
    public static GuiComponent<?> lastComponent; //TODO CLEAN THIS :O
    private static final ArrayDeque<GuiPanel> componentQueue = new ArrayDeque<>();

    public static void popComponentVariables(GuiComponent<?> lastComponent, ScriptContext context) {
        lastComponent = ComponentUtils.lastComponent;
        ComponentUtils.lastComponent = lastComponent.getParent();
        fillProperties(lastComponent, context, true);

        //GuiPanel p = componentQueue.removeLast();
        //System.out.println("POP " + p);
        //GuiPanel component = p.getParent();

        GuiPanel component = lastComponent.getParent(); //Can't use last component's parent : not already set
        System.out.println("POP " + lastComponent + " get " + component);
        //GuiPanel component = p.getParent();

        if(component != null)
            setComponentContext(component, context);
    }

    private static void fillProperties(GuiComponent<?> lastComponent, ScriptContext context, boolean popping) {
        System.out.println("FILLING " + lastComponent);

        ParseableComponent type = ParseableComponent.find(lastComponent);
        type.fillComponent(context, lastComponent);

        //TODO DETECT VARIABLES EN TROP ?
    }

    public static void pushComponentVariables(GuiComponent<?> component, ScriptContext context) {
        if (component.getParent() != null)
            fillProperties(component.getParent(), context, false);
        ComponentUtils.lastComponent = component;

        System.out.println("PUSH " + component);
        setComponentContext(component, context);

        //System.out.println("HAVE SET CONTEXT "+context.printVariables());
    }

    public static void setComponentContext(GuiComponent<?> component, ScriptContext context) {
        context.put(new ScriptTypeAccessor(new TypeComponent(component), "this_component"));

        ParseableComponent type = ParseableComponent.find(component);
        type.setupContext(context, component);

        //TODO SOUS-CONTEXT ETANCHE ENTRE CHAQUE COMPOSANT :thinking:
    }
}
