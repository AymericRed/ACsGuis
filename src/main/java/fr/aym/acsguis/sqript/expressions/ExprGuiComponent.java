package fr.aym.acsguis.sqript.expressions;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.button.GuiButton;
import fr.aym.acsguis.component.button.GuiCheckBox;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.panel.GuiScrollPane;
import fr.aym.acsguis.component.panel.GuiTabbedPane;
import fr.aym.acsguis.component.textarea.GuiLabel;
import fr.aym.acsguis.component.textarea.GuiTextField;
import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.expressions.ScriptExpression;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptOperator;
import fr.nico.sqript.structures.Side;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

/*@Expression(name = "GuiComponent",
        description = "Utilities about guis",
        examples = "gui label \"test\"",
        patterns = {"[a] [new] gui panel {string}:gui_component",
                "[a] [new] gui label {string} {string}:gui_component",
                "[a] [new] gui text_field {string}:gui_component",
                "[a] [new] gui button {function} {string} {string}:gui_component",
                "[a] [new] gui scroll_pane {string}:gui_component",
                "[a] [new] gui tabbed_pane {string}:gui_component",
                "[a] [new] gui checkbox {string} {string}:gui_component",
                "[a] [new] gui panel:gui_component",
                "[a] [new] gui label {string}:gui_component",
                "[a] [new] gui text_field:gui_component",
                "[a] [new] gui button {function} {string}:gui_component",
                "[a] [new] gui scroll_pane:gui_component",
                "[a] [new] gui tabbed_pane:gui_component",
                "[a] [new] gui checkbox {string}:gui_component"},
        side = Side.CLIENT
)*/
@Deprecated
public class ExprGuiComponent extends ScriptExpression
{
    public static final List<GuiPattern> patterns = new ArrayList<>();

    static
    {
        addPattern("[a] [new] gui panel", (c,p) -> new GuiPanel());
        addPattern("[a] [new] gui label {string}", (c,p) -> new GuiLabel(p[0].toString()));
        addPattern("[a] [new] gui text_field", (c,p) -> new GuiTextField());
        addPattern("[a] [new] gui button {function} {string}", (context, parameters) ->
                new GuiButton(parameters[1].toString()).addClickListener((x, y, z) -> {
                    System.out.println("Exec !");
                    try {
                        ((TypeFunction)parameters[0]).getObject().get(context, new ScriptType[0]);
                    } catch (ScriptException e) {
                        ScriptManager.log.fatal("Cannot process button function "+parameters[0], e);
                    }
                }));
        addPattern("[a] [new] gui scroll_pane", (c,p) -> new GuiScrollPane());
        addPattern("[a] [new] gui tabbed_pane", (c,p) -> new GuiTabbedPane());
        addPattern("[a] [new] gui checkbox {string}", (c,p) -> new GuiCheckBox(p[0].toString()));
    }

    /**
     * To complete the parameter :)
     */
    public static void main(String[] args) {
        String[] t = buildPatternsString();
        for(String s : t)
        {
            System.out.println("\""+s+"\",");
        }
    }

    public static void addPattern(String base, BiFunction<ScriptContext, ScriptType<?>[], GuiComponent<?>> creator) {
        patterns.add(new GuiPattern(base, creator));
    }

    public static String[] buildPatternsString() {
        String[] pattern = new String[patterns.size()*2];
        for (int i = 0; i < patterns.size(); i++) {
            pattern[patterns.size()+i] = patterns.get(i).patternBase;
            pattern[i] = patterns.get(i).patternFull;
        }
        return pattern;
    }

    private static class GuiPattern
    {
        private final String patternBase;
        private final String patternFull;
        private final BiFunction<ScriptContext, ScriptType<?>[], GuiComponent<?>> creator;

        public GuiPattern(String patternBase, BiFunction<ScriptContext, ScriptType<?>[], GuiComponent<?>> creator) {
            this.patternBase = patternBase+":gui_component";
            this.patternFull = patternBase + " {string}:gui_component";
            this.creator = creator;
        }

        public GuiComponent<?> get(ScriptContext context, ScriptType<?>[] parameters) {
            return creator.apply(context, parameters);
        }
    }

    @Override
    public ScriptType<GuiComponent<?>> get(ScriptContext context, ScriptType[] parameters) {
        String id = null, clas = null;
        System.out.println("==================");
       // System.out.println(context.printVariables());
        System.out.println(getMatchedIndex()+" "+ Arrays.toString(parameters));
        GuiPattern p;
        if(getMatchedIndex() >= patterns.size())
        {
            p = patterns.get(getMatchedIndex()-patterns.size());
            return new TypeComponent(p.get(context, parameters));
        }
        else {
            p = patterns.get(getMatchedIndex());
            id = parseId(parameters[parameters.length-1].toString());
            clas = parseClass(parameters[parameters.length-1].toString());
            System.out.println(p+" "+p.get(context, parameters));
            return new TypeComponent(p.get(context, parameters).setCssId(id).setCssClass(clas));
        }
    }

    public static String parseId(String from) {
        String s = from.contains("#") ? from.substring(from.indexOf("#")+1) : from;
        System.out.println("Parsing id "+from+" "+s);
        return from.contains("#") ? (s.contains(" ") ? s.substring(0, s.indexOf(" ")) : s) : null;
    }
    public static String parseClass(String from) {
        String s = from.contains(".") ? from.substring(from.indexOf(".")+1) : from;
        System.out.println("Parsing class "+from+" "+s);
        return from.contains(".") ? (s.contains(" ") ? s.substring(0, s.indexOf(" ")) : s) : null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) throws ScriptException.ScriptUndefinedReferenceException {
        System.out.println("Setting "+to+" "+ Arrays.toString(parameters));
        return false;
    }

    static
    {
        ScriptManager.registerBinaryOperation(ScriptOperator.ADD, TypeComponent.class, TypeComponent.class,
                (a,b) -> {
                    if(a.getObject() instanceof GuiPanel)
                    {
                        if(a.getObject() instanceof GuiTabbedPane)
                        {
                            if(b.getObject() instanceof GuiPanel)
                                ((GuiTabbedPane)a.getObject()).addTab(((GuiPanel)b.getObject()).getCssId(), (GuiPanel) b.getObject());
                            else
                                throw new RuntimeException("A panel is required in tabbed panes");
                        }
                        else
                            ((GuiPanel)a.getObject()).add((GuiComponent<?>) b.getObject());
                        System.out.println("Added !");
                    }
                    else
                        throw new RuntimeException("A panel is required");
                    return a;
                });
    }
}
