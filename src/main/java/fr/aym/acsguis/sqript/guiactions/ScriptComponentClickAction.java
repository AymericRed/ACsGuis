package fr.aym.acsguis.sqript.guiactions;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.sqript.ComponentUtils;
import fr.aym.acsguis.sqript.block.ParseableComponent;
import fr.aym.acsguis.sqript.expressions.TypeComponent;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Loop;
import fr.nico.sqript.structures.*;
import fr.nico.sqript.types.primitive.TypeNumber;

@Loop(name = "gui_component_on_click",
        pattern = "on component click:",
        side = Side.CLIENT
)
public class ScriptComponentClickAction extends GuiActionScriptLoop { //TODO AUTRES LISTENERS DE REDEN
    @Override
    public void appendListener(ScriptContext context, GuiComponent<?> component) {
        component.addClickListener((x, y, b) -> {
            System.out.println("Running button action !!");
            ScriptContext context1 = context;//new ScriptContext(context); //todo voir si ça empêche de "faire sortir" des variables : oui
            //ComponentUtils.setComponentContext(component, context); //TODO FAIRE UNE EXPRESSION EVENT DATA OU TRUC DU GENRE ?
            context1.put(new ScriptTypeAccessor(new TypeComponent(component), "this_component"));
            context1.put(new ScriptTypeAccessor(new TypeNumber(x), "click_x"));
            context1.put(new ScriptTypeAccessor(new TypeNumber(y), "click_y"));
            context1.put(new ScriptTypeAccessor(new TypeNumber(b), "click_b"));
            executeAction(context1);

            //System.out.println("FILLING " + component);

            //ParseableComponent type = ParseableComponent.find(component);
            //type.fillComponent(context, component);
        });
    }
}
