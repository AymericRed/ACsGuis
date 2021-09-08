package fr.aym.acsguis.sqript.actions;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.textarea.TextComponent;
import fr.aym.acsguis.sqript.block.ScriptBlockGuiComponent;
import fr.aym.acsguis.sqript.component.ComponentUtils;
import fr.aym.acsguis.sqript.component.ParseableComponent;
import fr.aym.acsguis.sqript.expressions.TypeComponent;
import fr.nico.sqript.actions.ScriptAction;
import fr.nico.sqript.compiling.ScriptCompileGroup;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptToken;
import fr.nico.sqript.meta.Action;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.IScript;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptLoop;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Action(name = "Add gui component action",
        features = @Feature(
                name = "Add a new gui component",
                description = "Adds a new gui component to the current panel. <br>" +
                        "You can specify the id, name and text of the component, see the examples below. <br>" +
                        " Use the block if you want to change specific properties of your component.",
                examples = {"add css component label with id \"reload_models\" and class \"reload_button\" and text \"Recharger les packs\"",
                        "add css component panel with id \"root\"",
                        "add css component scroll_pane",
                        "add css component button with class \"reload_button\" and text \"Recharger les packs\""},
                pattern = "^add css component .*"))
public class ActionGuiComponent extends ScriptAction {
    private String name;

    private GuiComponent<?> component;

    public String getName() {
        return name;
    }

    protected void parseProperties() throws Exception {
        //System.out.println("PARSING " + name);
        Pattern pattern = Pattern.compile("^([a-z_]*)?( with)?( id \"([a-z0-9_-]*)?\")?( and)?( class \"([a-z0-9_-]*)?\")?( and)?( text \"([a-zA-Z0-9 :/_-]*)?\")?$");
        String name = this.name.trim();
        Matcher matcher = pattern.matcher(name);
        if (!matcher.matches()) {
            throw new ScriptException(getLine(), "Line doesn't match required component definition : matching : " + name);
        }
        String type = matcher.group(1);
        if (type.isEmpty()) {
            throw new ScriptException(getLine(), "No component found for type: " + type);
        }
        //System.out.println("My type is " + type);
        ParseableComponent componentType = ParseableComponent.find(type);
        GuiComponent<?> component = componentType.create();

        String id = matcher.group(4);
        //System.out.println("The id is " + id);
        if (id != null && !id.isEmpty()) {
            component.setCssId(id);
        }

        String clazz = matcher.group(7);
        //System.out.println("The class is " + clazz);
        if (clazz != null && !clazz.isEmpty()) {
            component.setCssClass(clazz);
        }

        String text = matcher.group(10);
        //System.out.println("The text is " + text);
        if (text != null) {
            if (component instanceof TextComponent) {
                ((TextComponent) component).setText(text);
            } else {
                throw new ScriptException(getLine(), "Component " + component + " cannot contain text !");
            }
        }

        this.component = component;
    }

    @Override
    public void execute(ScriptContext context) throws ScriptException {
        try {
            parseProperties(); //Re-create component
        } catch (Exception e) {
            throw new ScriptException.ScriptWrappedException(getLine(), e);
        }

        //System.out.println("3" + context.printVariables());
        //System.out.println("Context "+context);
        //System.out.println("Accessor : "+context.getAccessor("this_component"));
        //System.out.println("========== GROS PD DE FDP " + ((TypeComponent) context.getAccessor("this_component").element).getObject() + " ADDING " + component + " " + getNext(context) + " " + getWrapped());
        //System.out.println("HUAWEI To: " + ((TypeComponent) context.getVariable("this_component")).getObject() + " adding: " + component);
        ((GuiPanel) ((TypeComponent) context.getAccessor("this_component").element).getObject()).add(component);
        ComponentUtils.pushComponentVariables(component, context);
    }

    @Override
    public IScript run(ScriptContext context) throws ScriptException {
        //System.out.println("OWW I DOING DONE " + getWrapped() + " " + getParent() + " " + this);
        IScript toDo = getNext(context);
        //System.out.println("-------------> Return " + totDo + " " + this.next);
        //System.out.println("Last runt is " + ScriptBlockGuiComponent.lastRuntTab + " and tabs " + tabLevel + " and this " + getLine());
        while (ScriptBlockGuiComponent.lastRuntTab >= this.tabLevel) {
            ScriptBlockGuiComponent.lastRuntTab--;
            ComponentUtils.popComponentVariables(context);
        }

        execute(context);
        ScriptBlockGuiComponent.lastRuntTab = this.tabLevel;
        return toDo;
    }

    @Override
    public IScript getNext(ScriptContext context) throws ScriptException {
        //TODO SEE IF UTIL
        if (next != null)
            return next;
        else if (getParent() != null && getParent() instanceof ScriptLoop) {
            return getParent().getNext(context);
        } else if (getParent() != null && getParent() instanceof ActionGuiComponent) {
            //if(((ScriptBlockGuiComponent) getParent()).component instanceof GuiPanel)
            //+  context.put(new ScriptAccessor(new TypeComponent(((ScriptBlockGuiComponent) getParent()).component), "this_component"));
            //System.out.println("Mais wtf le next de mon papa c'est " + getParent().getNext(context));
            return getParent().getNext(context);
        } else
            return null;
    }

    private int tabLevel;

    @Override
    public void build(ScriptToken line, ScriptCompileGroup compileGroup, List<String> parameters, int matchedIndex, int marks, int tabLevel) throws Exception {
        super.build(line, compileGroup, parameters, matchedIndex, marks, tabLevel);
        this.name = line.getText().trim().replaceFirst("(^|\\s+)add css component\\s+", "");
        //System.out.println("TABS LEVELS " + ScriptDecoder.getTabLevel(line.getText())+" AND "+ScriptDecoder.getTabLevel(getLine().getText()));
        this.tabLevel = tabLevel;
        //System.out.println("FILL " + text + " WITH TAB " + this.tabLevel);
        //System.out.println("Long name is " + name);
    }
}
