package fr.aym.acsguis.sqript.actions;

import fr.nico.sqript.actions.ScriptAction;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.Side;
import net.minecraft.client.Minecraft;

@Action(name = "Add component to panel",
        description ="",
        examples = "",
        patterns = "close css gui",
        side = Side.CLIENT
)
public class ActionCloseGui extends ScriptAction
{
    @Override
    public void execute(ScriptContext context) throws ScriptException {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            Minecraft.getMinecraft().displayGuiScreen(null);
        });
    }
}