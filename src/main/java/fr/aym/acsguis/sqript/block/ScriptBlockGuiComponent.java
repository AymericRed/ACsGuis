package fr.aym.acsguis.sqript.block;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.button.GuiButton;
import fr.aym.acsguis.component.layout.GuiScaler;
import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.panel.GuiScrollPane;
import fr.aym.acsguis.component.panel.GuiTabbedPane;
import fr.aym.acsguis.component.textarea.GuiLabel;
import fr.aym.acsguis.sqript.expressions.TypeComponent;
import fr.nico.sqript.blocks.ScriptBlock;
import fr.nico.sqript.compiling.ScriptCompileGroup;
import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptLine;
import fr.nico.sqript.meta.Block;
import fr.nico.sqript.structures.*;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Block(name = "gui_component",
        description = "gui component block",
        examples = "add css component panel:",
        regex = "^add css component .*",
        side = Side.CLIENT,
        fields = {"css_class","css_id","css_code"},
        reloadable = false
)
public class ScriptBlockGuiComponent extends ScriptBlock
{
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
        GuiComponent<?> component = null;
        String name = this.name;
        if(name.contains(" with"))
            name = this.name.substring(0, this.name.indexOf(" with"));
        System.out.println("My name is "+name);
        switch (name)
        {
            case "panel":
                component = new GuiPanel();
                break;
            case "tabbed_pane":
                component = new GuiTabbedPane();
                break;
            case "scroll_pane":
                component = new GuiScrollPane();
                break;
            case "label":
                component = new GuiLabel("not set");
                break;
            case "button":
                component = new GuiButton("not set");
                break;
            default: //TODO CHANGE ERROR DESC
                throw new ScriptException.ScriptMissingFieldException(this.getLine(),"add css component","invalid type");
        }

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

    @Override
    public void execute(ScriptContext context) {
        System.out.println("========== GROS PD DE FDP "+ ((TypeComponent)context.getAccessor("this component").element).getObject()+" ADDING "+component);
        ((GuiPanel)((TypeComponent)context.getAccessor("this component").element).getObject()).add(component);
        if(component instanceof GuiPanel)
            context.put(new ScriptAccessor(new TypeComponent(component), "this component"));
    }

    @Override
    public IScript run(ScriptContext context) {
        execute(context);

        System.out.println("OWW I DOING DONE "+getRoot()+" "+getParent());
        IScript toDo = getRoot() == null ? getNext(context) : getRoot();
        return toDo;
    }

    public IScript getNext(ScriptContext context) {
        if(next!=null)
            return next;
        else if(getParent()!=null && getParent() instanceof ScriptLoop) {
            return getParent().getNext(context);
        }else if(getParent()!=null && getParent() instanceof ScriptBlockGuiComponent) {
            if(((ScriptBlockGuiComponent) getParent()).component instanceof GuiPanel)
                context.put(new ScriptAccessor(new TypeComponent(((ScriptBlockGuiComponent) getParent()).component), "this component"));
            return getParent().getNext(context);
        }else
            return null;
    }

    @Override
    public IScript getParent() {
        return parent;
    }
}
