package fr.aym.acsguis.sqript.expressions;

import fr.aym.acsguis.component.layout.GridLayout;
import fr.aym.acsguis.component.layout.PanelLayout;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.primitive.TypeNumber;

import java.util.ArrayList;

@Expression(name = "Gui layout expression",
        features = @Feature(
                name = "Define a grid gui layout",
                description = "Defines a new grid layout to add to a gui panel",
                examples = "new grid layout with size [-1,25] spacing 5 direction \"horizontal\" elements per line 1",
                pattern = "new grid layout with size {array} spacing {number} direction {string} elements per line {number}:panel_layout",
                side = Side.CLIENT)
)
public class ExprGuiLayout extends ScriptExpression {
    @Override
    public ScriptType<PanelLayout<?>> get(ScriptContext context, ScriptType[] parameters) {
        if (parameters.length == 4) {
            GridLayout.GridDirection disposition = parameters[2].getObject().toString().matches("vertical") ? GridLayout.GridDirection.VERTICAL : GridLayout.GridDirection.HORIZONTAL;
            return new TypePanelLayout(new GridLayout((int) (double) ((ArrayList<TypeNumber>) parameters[0].getObject()).get(0).getObject(), (int) (double) ((ArrayList<TypeNumber>) parameters[0].getObject()).get(1).getObject(), (int) (double) parameters[1].getObject(), disposition, (int) (double) parameters[3].getObject()));
        }
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) throws ScriptException.ScriptUndefinedReferenceException {
        return false;
    }
}
