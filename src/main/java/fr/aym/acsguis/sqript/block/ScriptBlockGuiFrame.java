package fr.aym.acsguis.sqript.block;

import fr.aym.acsguis.api.ACsGuiApi;
import fr.aym.acsguis.component.layout.GuiScaler;
import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.sqript.expressions.TypeComponent;
import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.blocks.ScriptBlock;
import fr.nico.sqript.compiling.ScriptCompileGroup;
import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptLine;
import fr.nico.sqript.meta.Block;
import fr.nico.sqript.meta.BlockDefinition;
import fr.nico.sqript.structures.*;
import fr.nico.sqript.types.TypeArray;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


@Block(name = "gui_frame",
        description = "gui frame block",
        examples = "define gui frame my_frame:",
        regex = "^define gui frame .*",
        side = Side.CLIENT,
        fields = {"css_class","css_id","css_code","css_sheets"},
        reloadable = false
)
public class ScriptBlockGuiFrame extends ScriptBlock
{
    private final String name;

    public static ScriptBlock loadBlock(ScriptInstance instance, List<ScriptLine> block, ScriptLine line, ScriptLine head) throws Exception {
        BlockDefinition blockDefinition = ScriptDecoder.findBlockDefinition(head);
        if(blockDefinition==null)
            throw new ScriptException.ScriptUnknownTokenException(head);
        if(blockDefinition.getSide().isStrictlyValid() && (!ScriptManager.RELOADING || blockDefinition.isReloadable())){
            Class scriptBlockClass = blockDefinition.getBlockClass();
            //System.out.println("Loading : "+scriptBlockClass.getSimpleName());
            try{
                ScriptBlock scriptBlock = (ScriptBlock) scriptBlockClass.getConstructor(ScriptLine.class).newInstance(head);
                scriptBlock.setLine(line);
                scriptBlock.setScriptInstance(instance);
                return scriptBlock;
            } catch (InvocationTargetException exception){
                ScriptManager.handleError(line,exception.getTargetException());
            }
        }
        return null;
    }

    public ScriptBlockGuiFrame(ScriptLine head) throws ScriptException {
        super(head);
        String text = head.text.trim().replaceFirst("(^|\\s+)define gui frame\\s+", ""); //Extracting the event parameters
        text = text.substring(0, text.length()-1); //Removing the last ":"
        this.name = text.trim();
        System.out.println("My name is "+name);
    }

    public String getName() {
        System.out.println("IT My name is "+name);
        //TODO USE TO SHOW getScriptInstance().getBlocksOfClass(ScriptBlockGuiFrame.class).stream().filter(g -> ((ScriptBlockGuiFrame)g).getName().equalsIgnoreCase(name)).findFirst().get();
        return name;
    }

    private GuiFrame frame;

    @Override
    protected void load() throws Exception {
        if(!fieldDefined("css_sheets"))
            throw new ScriptException.ScriptMissingFieldException(this.getLine(),"define gui frame","css_sheets");

        List<ResourceLocation> lt = new ArrayList<>();
        ((TypeArray) getSubBlock("css_sheets").evaluate()).getObject().forEach(t -> {
            lt.add(new ResourceLocation(t.getObject().toString()));
        });

        System.out.println(lt);
        GuiFrame frame = new GuiFrame(new GuiScaler.Identity()) {
            @Override
            public List<ResourceLocation> getCssStyles() {
                return lt;
            }
        };
        if(fieldDefined("css_class"))
            frame.setCssClass(getSubBlock("css_class").evaluate().getObject().toString());
        if(fieldDefined("css_id"))
            frame.setCssClass(getSubBlock("css_id").evaluate().getObject().toString());
        if(fieldDefined("css_code"))
            frame.setCssCode(getSubBlock("css_code").evaluate().getObject().toString());

        System.out.println("Loading sub blocks");
        ScriptCompileGroup group = new ScriptCompileGroup();
        IScript script = getMainField().compile(group);
        ScriptContext ctx = ScriptContext.fromGlobal();
        ctx.put(new ScriptAccessor(new TypeComponent(frame), "this component"));
        //Running the associated script
        ScriptClock k = new ScriptClock(ctx);
        try {
            System.out.println("Running the command on "+script);
            k.start(script);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        this.frame = frame;

        getScriptInstance().registerBlock(this);
        System.out.println("RG "+getScriptInstance());
        System.out.println("RG0 "+getScriptInstance().getBlocksOfClass(ScriptBlockGuiFrame.class));
    }

    @Override
    public void execute(ScriptContext context) {
        super.execute(context);
        //TODO REèINSTANCIATE
        System.out.println("SHOW "+frame);
        ACsGuiApi.asyncLoadThenShowGui(name, () -> frame);
    }
}
