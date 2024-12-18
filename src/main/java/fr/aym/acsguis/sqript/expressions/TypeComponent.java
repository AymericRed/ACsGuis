package fr.aym.acsguis.sqript.expressions;

import fr.aym.acsguis.component.GuiComponent;
import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.types.ScriptType;
import net.minecraftforge.fml.relauncher.SideOnly;

@Type(
        name = "gui_component",
        parsableAs = {}
)
@SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
public class TypeComponent extends ScriptType<GuiComponent>
{
    public ScriptElement<?> parse(String typeName) {
        return null;
    }

    public String toString() {
        return this.getObject().toString();
    }

    public TypeComponent(GuiComponent file) {
        super(file);
    }
}
