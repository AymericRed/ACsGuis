package fr.aym.acsguis.sqript.expressions;

import fr.aym.acsguis.component.layout.PanelLayout;
import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.types.ScriptType;

@Type(
        name = "panel_layout",
        parsableAs = {}
)
public class TypePanelLayout extends ScriptType<PanelLayout<?>>
{
    public ScriptElement<?> parse(String typeName) {
        return null;
    }

    public String toString() {
        return this.getObject().toString();
    }

    public TypePanelLayout(PanelLayout file) {
        super(file);
    }
}
