package fr.aym.acsguis.sqript.guiactions;

import fr.aym.acsguis.component.GuiComponent;
import fr.nico.sqript.meta.Loop;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.primitive.TypeNumber;

@Loop(name = "gui_component_on_key",
        pattern = "on component key input",
        side = Side.CLIENT
)
public class ScriptComponentKeyAction extends GuiActionScriptLoop {
    @Override
    public void appendListener(ScriptContext context, GuiComponent<?> component) {
        component.addKeyboardListener((typedChar, keyCode) -> {
            System.out.println("Running button action !!");
            ScriptContext context1 = new ScriptContext(context); //todo voir si ça empêche de "faire sortir" des variables
            context1.put(new ScriptTypeAccessor(new TypeNumber(typedChar), "typed_char"));
            context1.put(new ScriptTypeAccessor(new TypeNumber(keyCode), "key_code"));
            executeAction(context1);
        });
    }
}
