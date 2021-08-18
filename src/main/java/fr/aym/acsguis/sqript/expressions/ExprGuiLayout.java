package fr.aym.acsguis.sqript.expressions;

import fr.aym.acsguis.component.layout.GridLayout;
import fr.aym.acsguis.component.layout.PanelLayout;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptOperator;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.primitive.TypeNumber;

import java.util.ArrayList;

@Expression(name = "GuiLayout",
        description = "g",
        examples = "h",
        patterns = {
        "new grid layout with size {array} spacing {number} direction {string} elements per line {number}:panel_layout"
        },
        side = Side.CLIENT
)
public class ExprGuiLayout extends ScriptExpression
{
    @Override
    public ScriptType<PanelLayout<?>> get(ScriptContext context, ScriptType[] parameters) {
        if(parameters.length == 4)
        {
            GridLayout.GridDirection disposition = parameters[2].getObject().toString().matches("vertical") ? GridLayout.GridDirection.VERTICAL : GridLayout.GridDirection.HORIZONTAL;
            System.out.println("Have set "+disposition);

            return new TypePanelLayout(new GridLayout((int) (double) ((ArrayList<TypeNumber>) parameters[0].getObject()).get(0).getObject(), (int) (double) ((ArrayList<TypeNumber>) parameters[0].getObject()).get(1).getObject(), (int) (double) parameters[1].getObject(), disposition, (int) (double) parameters[3].getObject()));
        }
        System.out.println("Tg");
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) throws ScriptException.ScriptUndefinedReferenceException {
        return false;
    }
}
