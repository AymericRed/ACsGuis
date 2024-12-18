package fr.aym.acsguis.sqript.block;

import fr.aym.acsguis.api.ACsGuiApi;
import fr.aym.acsguis.api.ACsGuiApiService;
import fr.aym.acsguis.component.layout.GuiScaler;
import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.sqript.component.ComponentProperties;
import fr.aym.acsguis.sqript.component.ComponentUtils;
import fr.aym.acsguis.sqript.expressions.TypeComponent;
import fr.aym.acslib.ACsLib;
import fr.aym.acslib.utils.ACsLogger;
import fr.nico.sqript.blocks.ScriptBlock;
import fr.nico.sqript.compiling.ScriptCompilationContext;
import fr.nico.sqript.compiling.ScriptException;
import fr.nico.sqript.compiling.ScriptToken;
import fr.nico.sqript.meta.Block;
import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.structures.*;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeArray;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@Block(
        feature = @Feature(
        name = "Define gui frame block",
        description = "Defines a new gui frame",
        examples = "define gui frame my_frame:",
        regex = "^define gui frame .*:",
        side = Side.CLIENT),
        fields = { @Feature(name = "css_class", description = "Sets the css class of this frame"),
                @Feature(name = "css_id", description = "Sets the css id of this frame"),
                @Feature(name = "css_code", description = "Sets the css code of this frame"),
                @Feature(name = "gui_scaling", description = "Sets the scaling function of the gui (identity or adjust_full_screen)"), //TODO SUPPORT MAX WIDTH & MAX HEIGHT
                @Feature(name = "enable_debug", description = "Enables debug functions on the gui : reload all css files when the gui is opened and enable the 'K' debug key"),
                @Feature(name = "css_sheets", description = "List the css sheets used by the gui here. Array of resource locations. Required", type = "String array")}
)
@SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
public class ScriptBlockGuiFrame extends ScriptBlock
{
    private final String name;

    public ScriptBlockGuiFrame(ScriptToken head) throws ScriptException {
        super(head);
        String text = head.getText().trim().replaceFirst("(^|\\s+)define gui frame\\s+", ""); //Extracting the event parameters
        text = text.substring(0, text.length()-1); //Removing the last ":"
        this.name = text.trim();
        //System.out.println("My name is "+name);
    }

    public String getName() {
        return name;
    }

    private GuiFrame frame;

    @Override
    protected void load() throws Exception {
        if(!fieldDefined("css_sheets"))
            throw new ScriptException.ScriptMissingFieldException(this.getLine(),"define gui frame","css_sheets");

        //System.out.println("Loading sub blocks");
        ScriptCompilationContext group = new ScriptCompilationContext();
        group.add("this_component", TypeComponent.class);
        for(ComponentProperties<?, ?> property : ComponentProperties.getProperties()) {
            group.add(property.getName(), ScriptElement.class);
        }
        IScript script = getMainField().compile(group);
        setRoot(script);

        getScriptInstance().registerBlock(this);
        //System.out.println("RG "+getScriptInstance());
        //System.out.println("RG0 "+getScriptInstance().getBlocksOfClass(ScriptBlockGuiFrame.class));
    }

    @Override
    public void execute(ScriptContext context) {
        if(FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            //System.out.println("AFFICHAGE ====================");
            ACsGuiApi.asyncLoadThenShowGui(name, () -> {
                try {
                    List<ResourceLocation> lt = new ArrayList<>();
                    ((TypeArray) getSubBlock("css_sheets").evaluate()).getObject().forEach(t -> {
                        lt.add(new ResourceLocation(t.getObject().toString()));
                    });

                    //System.out.println(lt);
                    final boolean enableDebug;
                    if(fieldDefined("enable_debug")) {
                        enableDebug = (boolean) getSubBlock("enable_debug").evaluate().getObject();
                    } else {
                        enableDebug = false;
                    }

                    final GuiScaler scale;
                    if(fieldDefined("gui_scaling")) {
                        String scaling = getSubBlock("gui_scaling").getString();
                        if(scaling.equalsIgnoreCase("adjust_full_screen")) {
                            scale = new GuiScaler.AdjustFullScreen();
                        } else {
                            scale = new GuiScaler.Identity();
                        }
                    } else {
                        scale = new GuiScaler.Identity();
                    }
                    
                    GuiFrame frame = new GuiFrame(scale) {
                        @Override
                        public List<ResourceLocation> getCssStyles() {
                            return lt;
                        }

                        @Override
                        public boolean isEnableDebugPanel() {
                            return  enableDebug;
                        }

                        @Override
                        public boolean needsCssReload() {
                            return enableDebug;
                        }
                    };
                    if (fieldDefined("css_class"))
                        frame.setCssClass(getSubBlock("css_class").evaluate().getObject().toString());
                    if (fieldDefined("css_id"))
                        frame.setCssId(getSubBlock("css_id").evaluate().getObject().toString());
                    if (fieldDefined("css_code"))
                        frame.setCssCode(getSubBlock("css_code").evaluate().getObject().toString());
                    this.frame = frame;
                    //System.out.println("GUI CREATED "+frame+" "+frame.hashCode());

                    ScriptContext ctx = ScriptContext.fromGlobal();
                    //System.out.println("1" + ctx.printVariables());
                    ComponentUtils.pushComponentVariables(frame, ctx);
                    //System.out.println("2" + ctx.printVariables());
                    //Running the associated script
                    ScriptClock k = new ScriptClock(ctx);
                    ScriptBlockGuiComponent.lastRuntTab = getLine().getTabLevel()-1;
                    try {
                        //System.out.println("Running the command on " + getRoot());
                        k.start(getRoot());
                    } catch (ScriptException e) {
                        ACsLogger.serviceFatal(ACsLib.getPlatform().provideService(ACsGuiApiService.class), "An error occurred evaluating gui "+name, e);
                        throw e;
                    }
                    while(ComponentUtils.lastAddedComponent != null) {
                        ComponentUtils.popComponentVariables(ctx);
                    }
                    //System.out.println("SHOW " + frame);
                } catch (Exception e) {
                    throw new RuntimeException("Cannot show gui "+name+" : "+ e, e);
                }
                return frame;
            });
        }
    }

    @Override
    public IScript run(ScriptContext context) {
        execute(context);
        //System.out.println("OH FRAME IS DOING DONE "+getRoot()+" "+getParent());
        return getNext(context);
    }
}
