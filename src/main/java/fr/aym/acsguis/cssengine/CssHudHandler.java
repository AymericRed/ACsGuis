package fr.aym.acsguis.cssengine;

import fr.aym.acsguis.component.panel.GuiFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles hud guis display
 */
public class CssHudHandler {
    private final List<GuiFrame.APIGuiScreen> displayedHuds = new ArrayList<>();
    private int displayWidth, displayHeight, guiScale;

    public List<GuiFrame.APIGuiScreen> getDisplayedHuds() {
        return displayedHuds;
    }

    public void closeAllHudGuis() {
        for (GuiFrame.APIGuiScreen hud : displayedHuds) {
            hud.onGuiClosed();
        }
        displayedHuds.clear();
    }

    public boolean closeHudGui(Class<? extends GuiFrame> hudFrameClass) {
        return displayedHuds.removeIf(hud -> {
            if (hudFrameClass.isInstance(hud.getFrame())) {
                hud.onGuiClosed();
                return true;
            }
            return false;
        });
    }

    public void closeHudGui(GuiFrame hud) {
        displayedHuds.remove(hud.getGuiScreen());
        hud.getGuiScreen().onGuiClosed();
    }

    public void showHudGui(GuiFrame hud) {
        showHudGui(displayedHuds.size(), hud);
    }

    public void showHudGui(int index, GuiFrame hud) {
        GuiFrame.APIGuiScreen currentHUD = hud.getGuiScreen();
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
        int i = scaledresolution.getScaledWidth();
        int j = scaledresolution.getScaledHeight();
        currentHUD.setWorldAndResolution(Minecraft.getMinecraft(), i, j);
        currentHUD.setFocused(false);
        displayedHuds.add(index, currentHUD);
    }

    @SubscribeEvent
    public void drawHud(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            if (displayedHuds.isEmpty())
                return;
            displayedHuds.forEach(hud -> hud.drawScreen(Integer.MIN_VALUE, Integer.MIN_VALUE, event.getPartialTicks()));
        }
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (displayedHuds.isEmpty())
                return;
            displayedHuds.forEach(GuiFrame.APIGuiScreen::updateScreen);
            if (Minecraft.getMinecraft().displayWidth != displayWidth || Minecraft.getMinecraft().displayHeight != displayHeight || Minecraft.getMinecraft().gameSettings.guiScale != guiScale) {
                displayWidth = Minecraft.getMinecraft().displayWidth;
                displayHeight = Minecraft.getMinecraft().displayHeight;
                guiScale = Minecraft.getMinecraft().gameSettings.guiScale;
                ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
                int i = scaledresolution.getScaledWidth();
                int j = scaledresolution.getScaledHeight();
                displayedHuds.forEach(hud -> hud.onResize(Minecraft.getMinecraft(), i, j));
            }
        }
    }

    @SubscribeEvent
    public void worldUnload(WorldEvent.Unload event) {
        closeAllHudGuis();
    }
}
