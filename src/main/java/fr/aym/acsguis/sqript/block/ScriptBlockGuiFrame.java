package fr.aym.acsguis.sqript.block;

import fr.aym.acsguis.api.ACsGuiApi;
import fr.aym.acsguis.component.layout.GuiScaler;
import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.sqript.ComponentUtils;
import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.blocks.ScriptBlock;
import fr.nico.sqript.compiling.ScriptCompileGroup;
import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptToken;
import fr.nico.sqript.meta.Block;
import fr.nico.sqript.meta.BlockDefinition;
import fr.nico.sqript.structures.*;
import fr.nico.sqript.types.TypeArray;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@Block(name = "gui_frame",
        description = "gui frame block",
        examples = "define gui frame my_frame:",
        regex = "^define gui frame .*:",
        side = Side.CLIENT,
        fields = {"css_class","css_id","css_code","css_sheets"}
)
public class ScriptBlockGuiFrame extends ScriptBlock
{
    private final String name;

    public static ScriptBlock loadBlock(ScriptInstance instance, List<ScriptToken> block, ScriptToken line, ScriptToken head) throws Exception {
        BlockDefinition blockDefinition = ScriptDecoder.findBlockDefinition(head);
        if(blockDefinition==null)
            throw new ScriptException.ScriptUnknownTokenException(head);
        if(blockDefinition.getSide().isStrictlyValid() && (!ScriptManager.RELOADING || blockDefinition.isReloadable())){
            Class scriptBlockClass = blockDefinition.getBlockClass();
            //System.out.println("Loading : "+scriptBlockClass.getSimpleName());
            try{
                ScriptBlock scriptBlock = (ScriptBlock) scriptBlockClass.getConstructor(ScriptToken.class).newInstance(head);
                scriptBlock.setLine(line);
                scriptBlock.setScriptInstance(instance);
                return scriptBlock;
            } catch (InvocationTargetException exception){
                ScriptManager.handleError(line,exception.getTargetException());
            }
        }
        return null;
    }

    public ScriptBlockGuiFrame(ScriptToken head) throws ScriptException {
        super(head);
        String text = head.getText().trim().replaceFirst("(^|\\s+)define gui frame\\s+", ""); //Extracting the event parameters
        text = text.substring(0, text.length()-1); //Removing the last ":"
        this.name = text.trim();
        System.out.println("My name is "+name);
    }

    public String getName() {
        return name;
    }

    private GuiFrame frame;

    @Override
    protected void load() throws Exception {
        if(!fieldDefined("css_sheets"))
            throw new ScriptException.ScriptMissingFieldException(this.getLine(),"define gui frame","css_sheets");

        System.out.println("Loading sub blocks");
        ScriptCompileGroup group = new ScriptCompileGroup();
        group.add("layout");
        group.add("text");
        group.add("action");
        group.add("this_component");
        //TODO AND ALL ComponentProperties
        IScript script = getMainField().compile(group);
        setRoot(script);

        getScriptInstance().registerBlock(this);
        System.out.println("RG "+getScriptInstance());
        System.out.println("RG0 "+getScriptInstance().getBlocksOfClass(ScriptBlockGuiFrame.class));
    }

    @Override
    public void execute(ScriptContext context) {
        if(FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            System.out.println("AFFICHAGE ====================");
            ACsGuiApi.asyncLoadThenShowGui(name, () -> {
                try {
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
                    if (fieldDefined("css_class"))
                        frame.setCssClass(getSubBlock("css_class").evaluate().getObject().toString());
                    if (fieldDefined("css_id"))
                        frame.setCssClass(getSubBlock("css_id").evaluate().getObject().toString());
                    if (fieldDefined("css_code"))
                        frame.setCssCode(getSubBlock("css_code").evaluate().getObject().toString());
                    this.frame = frame;
                    System.out.println("GUI CREATED "+frame+" "+frame.hashCode());

                    ScriptContext ctx = ScriptContext.fromGlobal();
                    System.out.println("1" + ctx.printVariables());
                    ComponentUtils.pushComponentVariables(frame, ctx);
                    System.out.println("2" + ctx.printVariables());
                    //Running the associated script
                    ScriptClock k = new ScriptClock(ctx);
                    ScriptBlockGuiComponent.lastRuntTab = -1;
                    try {
                        System.out.println("Running the command on " + getRoot());
                        k.start(getRoot());
                    } catch (ScriptException e) {
                        e.printStackTrace();
                    }
                    while(ComponentUtils.lastComponent != null) {
                        ComponentUtils.popComponentVariables(null, ctx);
                    }

                    System.out.println("SHOW " + frame);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return frame;
            });
        }
    }

    @Override
    public IScript run(ScriptContext context) {
        execute(context);
        System.out.println("OH FRAME IS DOING DONE "+getRoot()+" "+getParent());
        return getNext(context);
    }
}
