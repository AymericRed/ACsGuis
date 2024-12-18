package fr.aym.acsguis.sqript.block;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.textarea.TextComponent;
import fr.aym.acsguis.sqript.component.ComponentUtils;
import fr.aym.acsguis.sqript.component.ParseableComponent;
import fr.aym.acsguis.sqript.expressions.TypeComponent;
import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.meta.Loop;
import fr.nico.sqript.structures.IScript;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptLoop;
import fr.nico.sqript.structures.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*@Block(name = "gui_component",
        description = "gui component block",
        examples = "add css component panel:",
        regex = "^add css component .*",
        side = Side.CLIENT,
        fields = {"css_class", "css_id", "css_code", "text", "onclick", "max_text_length", "checked", "entity_to_render", "choices", "min_value", "max_value", "hint_text", "regex"}
)*/
@Loop(name = "Add gui component block",
        pattern = "^add css component .*:",
        side = Side.CLIENT
)
@SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
public class ScriptBlockGuiComponent extends ScriptLoop {
    public static final String[] supportedFields = new String[]{"text", "max_text_length", "checked", "entity_to_render", "choices", "min_value", "max_value", "hint_text", "regex"};

    private String name;

    public String getName() {
        //TODOOLD USE TO SHOW getScriptInstance().getBlocksOfClass(ScriptBlockGuiFrame.class).stream().filter(g -> ((ScriptBlockGuiFrame)g).getName().equalsIgnoreCase(name)).findFirst().get();
        return name;
    }

    private GuiComponent component;

    protected void parseProperties() throws Exception {
        //System.out.println("PARSING "+name);
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
        GuiComponent component = componentType.create();

        String id = matcher.group(4);
        //System.out.println("The id is "+id);
        if (id != null && !id.isEmpty()) {
            component.setCssId(id);
        }

        String clazz = matcher.group(7);
        //System.out.println("The class is "+clazz);
        if (clazz != null && !clazz.isEmpty()) {
            component.setCssClass(clazz);
        }

        String text = matcher.group(10);
        //System.out.println("The text is "+text);
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
        //System.out.println("HUAWEI To: "+((TypeComponent) context.getVariable("this_component")).getObject()+" adding: "+component);
        ((GuiPanel) ((TypeComponent) context.getAccessor("this_component").element).getObject()).add(component);
        if (getWrapped() == null) {
            System.out.println("WTF no wrapped for " + getLine());
        } else {
            ComponentUtils.pushComponentVariables(component, context);
        }
    }

    public static int lastRuntTab;
    public static int hiddenTabs;
    public static boolean hidden;

    @Override
    public IScript run(ScriptContext context) throws ScriptException {
        //System.out.println("=== Compute real tab level of "+getLine().getText()+" ===");
        IScript parent = getParent();
        realTabLevel = tabLevel;
        while (parent != null) {
            if (parent instanceof ScriptBlockGuiFrame) {
                //System.out.println("Encountered root parent");
                break;
            } else if (parent instanceof ScriptBlockGuiComponent) {
                //System.out.println("Dec real tab by " + (((ScriptBlockGuiComponent) parent).getTabLevel() - ((ScriptBlockGuiComponent) parent).getRealTabLevel()));
                realTabLevel -= (((ScriptBlockGuiComponent) parent).getTabLevel() - ((ScriptBlockGuiComponent) parent).getRealTabLevel());
                break;
            } else {
                //System.out.println("Dec real tab because of loop " + parent);
                realTabLevel--;
            }
            parent = parent.getParent();
        }

        //System.out.println(">>>> GOT THE REAL TAB LEVEL " + realTabLevel + " / " + tabLevel);
        //System.out.println("OWW I DOING DONE " + getWrapped() + " " + getParent() + " " + this);
        IScript toDo = getWrapped() == null ? getNext(context) : getWrapped();
        //System.out.println("-------------> Return " + toDot + " " + this.next);
        //System.out.println("Last runt is " + lastRuntTab + " and tabs " + tabLevel + " and this " + getLine());
        while (lastRuntTab >= realTabLevel) {
            lastRuntTab--;
            ComponentUtils.popComponentVariables(context);
        }

        execute(context);
        lastRuntTab = realTabLevel;
        return toDo;
    }

    @Override
    public IScript getNext(ScriptContext context) throws ScriptException {
        if (next != null)
            return next;
        else if (getParent() != null && getParent() instanceof ScriptLoop) {
            return getParent().getNext(context);
        } else if (getParent() != null && getParent() instanceof ScriptBlockGuiComponent) {
            //if(((ScriptBlockGuiComponent) getParent()).component instanceof GuiPanel)
            //+  context.put(new ScriptAccessor(new TypeComponent(((ScriptBlockGuiComponent) getParent()).component), "this_component"));
            System.out.println("Mais wtf le next de mon papa c'est " + getParent().getNext(context));
            return getParent().getNext(context);
        } else
            return null;
    }

    @Override
    public IScript getParent() {
        return parent;
    }

    private int tabLevel;
    private int realTabLevel;

    @Override
    public void wrap(IScript wrapped) {
        String text = getLine().getText().trim().replaceFirst("(^|\\s+)add css component\\s+", ""); //Extracting the event parameters
        text = text.substring(0, text.length() - 1); //Removing the last ":"
        this.name = text;
        this.tabLevel = ScriptDecoder.getTabLevel(getLine().getText());
        //System.out.println("FILL "+text+" WITH TAB "+tabLevel);
        //System.out.println("Long name is " + name);
        super.wrap(wrapped);
    }

    public int getTabLevel() {
        return tabLevel;
    }

    public int getRealTabLevel() {
        return realTabLevel;
    }
}
