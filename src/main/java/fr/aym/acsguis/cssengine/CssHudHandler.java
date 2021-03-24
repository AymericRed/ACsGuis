package fr.aym.acsguis.cssengine;

import fr.aym.acsguis.component.panel.GuiFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Handles hud guis display
 */
public class CssHudHandler
{
    private GuiFrame.APIGuiScreen currentHUD;
    private int displayWidth, displayHeight;

    public GuiFrame.APIGuiScreen getCurrentHUD() {
        return currentHUD;
    }

    public void setCurrentHUD(GuiFrame hud) {
        if(currentHUD != null) {
            currentHUD.onGuiClosed();
        }
        if(hud != null) {
            currentHUD = hud.getGuiScreen();
            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
            currentHUD.setWorldAndResolution(Minecraft.getMinecraft(), sr.getScaledWidth(), sr.getScaledHeight());
            currentHUD.setFocused(false);
        }
        else
            currentHUD = null;
    }

    @SubscribeEvent
    public void drawHud(RenderGameOverlayEvent.Post event) {
        if(event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            if(currentHUD != null) {
                currentHUD.drawScreen(Integer.MIN_VALUE, Integer.MIN_VALUE, event.getPartialTicks());
            }
        }
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.END) {
            if(currentHUD != null) {
                currentHUD.updateScreen();
            }
            if(Minecraft.getMinecraft().displayWidth != displayWidth || Minecraft.getMinecraft().displayHeight != displayHeight) {
                displayWidth = Minecraft.getMinecraft().displayWidth;
                displayHeight = Minecraft.getMinecraft().displayHeight;
                if(currentHUD != null)
                    currentHUD.onResize(Minecraft.getMinecraft(), displayWidth, displayHeight);
            }
        }
    }

    @SubscribeEvent
    public void worldUnload(WorldEvent.Unload event) {
        setCurrentHUD(null);
    }
}
