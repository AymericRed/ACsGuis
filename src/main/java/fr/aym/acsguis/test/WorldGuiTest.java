package fr.aym.acsguis.test;

import fr.aym.acsguis.api.ACsGuiApi;
import fr.aym.acsguis.api.worldguis.WorldGui;
import fr.aym.acsguis.api.worldguis.WorldGuiTransform;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import javax.vecmath.Vector3f;

@Mod.EventBusSubscriber(modid = ExampleGuiMod.MOD_ID)
public class WorldGuiTest {
    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && Minecraft.getMinecraft().player != null) {
            if (Keyboard.isKeyDown(Keyboard.KEY_M) && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
                ACsGuiApi.getInWorldGuisManager().getWorldGuis().clear();
                WorldGuiTransform transform = new WorldGuiTransform(new Vector3f(513.98f, 65, -360), 90, 0);
                WorldGui wgui = new WorldGui(new GuiDnxDebug(), transform, 2, 1, 500, 250, true);
                wgui.setRenderDebug(true);
                ACsGuiApi.getInWorldGuisManager().addWorldGui(wgui);
            }
        }
    }
}
