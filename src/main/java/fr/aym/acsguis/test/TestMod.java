package fr.aym.acsguis.test;

import fr.aym.acsguis.api.ACsGuiApi;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

@Mod(modid = "acsguis_test", name = "ACsGuis test mod", version = "1.0.0")
public class TestMod
{
    @Mod.EventHandler
    public void preInit(FMLInitializationEvent event) {
        ACsGuiApi.registerStyleSheetToPreload(GuiDnxDebug.RESOURCE_LOCATION);
        ACsGuiApi.init();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPress(InputEvent.KeyInputEvent event) {
        if(Keyboard.isKeyDown(Keyboard.KEY_J))
        {
            ACsGuiApi.asyncLoadThenShowGui("test_gui", GuiDnxDebug::new);
        }
    }
}
