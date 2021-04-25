package fr.aym.acsguis.sqript.block;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.sqript.expressions.TypeComponent;
import fr.nico.sqript.blocks.ScriptBlock;
import fr.nico.sqript.compiling.ScriptCompileGroup;
import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptLine;
import fr.nico.sqript.meta.Block;
import fr.nico.sqript.meta.Type;
import fr.nico.sqript.structures.*;
import fr.nico.sqript.types.ScriptType;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TODO A DAY : LOOPS
@Block(name = "gui_component",
        description = "gui component block",
        examples = "add css component panel:",
        regex = "^add css component .*",
        side = Side.CLIENT,
        fields = {"css_class","css_id","css_code","text","onclick","max_text_length","checked","entity_to_render","choices","min_value","max_value","hint_text","regex"}
)
public class ScriptBlockGuiComponent extends ScriptBlock implements IScriptLoop
{
    public static final String[] supportedFields = new String[] {"text", "max_text_length", "checked", "entity_to_render", "choices", "min_value", "max_value", "hint_text", "regex"};

    private final String name;

    public ScriptBlockGuiComponent(ScriptLine head) throws ScriptException {
        super(head);
        String text = head.text.trim().replaceFirst("(^|\\s+)add css component\\s+", ""); //Extracting the event parameters
        text = text.substring(0, text.length()-1); //Removing the last ":"
        this.name = text;
        System.out.println("Long name is "+name);
    }

    public String getName() {
        //TODO USE TO SHOW getScriptInstance().getBlocksOfClass(ScriptBlockGuiFrame.class).stream().filter(g -> ((ScriptBlockGuiFrame)g).getName().equalsIgnoreCase(name)).findFirst().get();
        return name;
    }

    private GuiComponent component;

    public static List<String[]> test(String patt, String text) throws Exception {
        TransformedPattern tp = ScriptDecoder.transformPattern(patt);
        Pattern p = tp.getPattern();
        Matcher m = p.matcher(text);
        //System.out.println(m.pattern());
        List<String> group = new ArrayList<>();
        if (m.find()) {
            List<String[]> parameters = new ArrayList<>();
            //System.out.println("Found expression definition : "+def.getExpressionClass()+" "+def.getName());
            int i = 0;
            for (String matchGroup : tp.getAllArguments(text)) {
                //System.out.println("iter: "+i+" "+ matchGroup +" "+tp.getTypes().length+" "+tp.getTypes()[i].isN_args());
                if (i < tp.getTypes().length && tp.getTypes()[i].isN_args()) {
                    if (matchGroup == null || matchGroup.isEmpty()) {
                        parameters.add(new String[0]);
                        continue;
                    }
                    String[] parts = ScriptDecoder.splitAtComa(matchGroup);
                    group.addAll(Arrays.asList(parts));
                } else
                    group.add(m.group(i + 1));
                parameters.add(group.toArray(new String[0]));
                //System.out.println("Added " + Arrays.toString(parameters.get(parameters.size()-1)));
                group.clear();
                i++;
            }
            return parameters;
        }
        else
            return null;
    }

    @Override
    protected void load() throws Exception {
        String name = this.name;
        if(name.contains(" with"))
            name = this.name.substring(0, this.name.indexOf(" with"));
        System.out.println("My name is "+name);
        ParseableComponent componentType = ParseableComponent.find(name);
        GuiComponent<?> component = componentType.create();

        String patt = ""+name+" with id {string} and class {string}";
        List<String[]> params = test(patt, this.name);
        if(params != null) {
            System.out.println("Found params with 0");
            for(String[] s : params)
                System.out.println(Arrays.toString(s));
            component.setCssId(params.get(0)[0].replace("\"", ""));
            component.setCssClass(params.get(1)[0].replace("\"", ""));
        }
        else {
            patt = "" + name + " with class {string} and id {string}";
            params = test(patt, this.name);
            if (params != null) {
                System.out.println("Found params with 1");
                for (String[] s : params)
                    System.out.println(Arrays.toString(s));
                component.setCssClass(params.get(0)[0].replace("\"", ""));
                component.setCssId(params.get(1)[0].replace("\"", ""));
            }
            else
            {
                patt = "" + name + " with class {string}";
                params = test(patt, this.name);
                if (params != null) {
                    System.out.println("Found params with 2");
                    for (String[] s : params)
                        System.out.println(Arrays.toString(s));
                    component.setCssClass(params.get(0)[0].replace("\"", ""));
                }
                else
                {
                    patt = "" + name + " with id {string}";
                    params = test(patt, this.name);
                    if (params != null) {
                        System.out.println("Found params with 3");
                        for (String[] s : params)
                            System.out.println(Arrays.toString(s));
                        component.setCssId(params.get(0)[0].replace("\"", ""));
                    }
                    else
                    {
                        System.out.println("No setup found");
                    }
                }
            }
        }

        ScriptCompileGroup grs = new ScriptCompileGroup();
        grs.add("this component");

        //TODO FINISH

        if(fieldDefined("css_class"))
            component.setCssClass(getSubBlock("css_class").getRawContent());
        if(fieldDefined("css_id"))
            component.setCssClass(getSubBlock("css_id").getRawContent());
        if(fieldDefined("css_code"))
            component.setCssCode(getSubBlock("css_code").getRawContent());
        if(fieldDefined("onclick"))
        {
            ScriptLineBlock block = getSubBlock("onclick");
            IScript script = block.compile(this, grs);
            component.addClickListener((x, y, b) -> {
                ScriptContext ctx = ScriptContext.fromGlobal();
                ctx.put(new ScriptAccessor(new TypeComponent(component), "this component"));
                ScriptClock clock = new ScriptClock(ctx);
                try {
                    System.out.println("Running the onclick "+script+" on "+component);
                    clock.start(script);
                } catch (ScriptException e) {
                    e.printStackTrace();
                }
            });
        }
        for(String s : supportedFields) {
            if(fieldDefined(s)) {
                componentType.getFieldHandler().accept(component, s, getSubBlock(s));
            }
        }

        this.component = component;

        System.out.println("Parsed "+component);
        if(component instanceof GuiPanel) { //also allow others but check bottom
            System.out.println("Going deeper");
            IScript script = getMainField().compile(this, grs);
            if(script != null) {
                setRoot(script);
                /*ScriptContext ctx = ScriptContext.fromGlobal();
                ctx.put(new ScriptAccessor(new TypeComponent(component), "this component"));
                //Running the associated script
                ScriptClock k = new ScriptClock(ctx);
                try {
                    System.out.println("Running the command");
                    k.start(script);
                } catch (ScriptException e) {
                    e.printStackTrace();
                }
                System.out.println("Goed deeper");*/
            }
            else
                System.out.println("Script not found here :c");
        }
    }

    @Type(
            name = "component_queue",
            parsableAs = {}
    )
    public static class TypeGuiComponentQueue extends ScriptType<Deque<GuiPanel>>
    {
        public ScriptElement<?> parse(String typeName) {
            return null;
        }

        public String toString() {
            return this.getObject().toString();
        }

        public TypeGuiComponentQueue(Deque<GuiPanel> file) {
            super(file);
        }
    }


    @Override
    public void execute(ScriptContext context) {
        System.out.println("========== GROS PD DE FDP "+ ((TypeComponent)context.getAccessor("this component").element).getObject()+" ADDING "+component+" "+getNext(context)+" "+getRoot());
        ((GuiPanel)((TypeComponent)context.getAccessor("this component").element).getObject()).add(component);
        if(this.next == null && getRoot() == null) {
            GuiPanel p = ((TypeGuiComponentQueue)context.getAccessor("component_queue").element).getObject().removeLast();
            System.out.println("POP "+p);
            context.put(new ScriptAccessor(new TypeComponent(p.getParent()), "this component"));
        }
        else if(component instanceof GuiPanel) { //TODO PAS COMPTER LES BLOCS VIDES
            System.out.println("PUSH "+component);
            ((TypeGuiComponentQueue)context.getAccessor("component_queue").element).getObject().add((GuiPanel) component);
            context.put(new ScriptAccessor(new TypeComponent(component), "this component"));
        }
    }

    @Override
    public IScript run(ScriptContext context) {
        execute(context);

        System.out.println("OWW I DOING DONE "+getRoot()+" "+getParent()+" "+this);
        IScript toDo = getRoot() == null ? getNext(context) : getRoot();
        System.out.println("-------------> Return "+toDo+" "+this.next);
        return toDo;
    }

    @Override
    public void setNext(IScript next) {
        System.out.println("OH ON SET MON PAPA !");
        super.setNext(next);
        //if(getRoot() != null)
          //  getRoot().setNext(next);
    }

    @Override
    public IScript getNext(ScriptContext context) {
        if(next!=null)
            return next;
        else if(getParent()!=null && getParent() instanceof ScriptLoop) {
            return getParent().getNext(context);
        }else if(getParent()!=null && getParent() instanceof ScriptBlockGuiComponent) {
            //if(((ScriptBlockGuiComponent) getParent()).component instanceof GuiPanel)
              //+  context.put(new ScriptAccessor(new TypeComponent(((ScriptBlockGuiComponent) getParent()).component), "this component"));
            System.out.println("Mais wtf le next de mon papa c'est " + getParent().getNext(context));
            return getParent().getNext(context);
        }else
            return null;
    }

    @Override
    public IScript getParent() {
        return parent;
    }

    @Override
    public void initLoop(IScript script, int tabLevel, ScriptCompileGroup compileGroup, List<ScriptLine> forContainer) throws Exception {
        init(tabLevel, new ScriptBlock.ScriptLineBlock("main", forContainer));
    }
}