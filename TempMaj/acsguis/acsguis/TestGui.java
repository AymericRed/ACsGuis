package fr.aym.acsguis;

import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.component.textarea.GuiLabel;
import fr.aym.acsguis.component.layout.GuiScaler;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class TestGui extends GuiFrame
{
    private GuiLabel load;
    public TestGui()
    {
        super(2000, 900, new GuiScaler.Identity());
        this.setCssId("root");
        style.setBackgroundColor(Color.TRANSLUCENT);
        add(new GuiLabel(10, 10, 0, 0, "Stylisé avec css :)"));
        /*GuiPanel PD = new GuiPanel(0, 0, 0, 0);
        PD.add(new GuiButton(10, 30, 0, 0, "Bouton css"));
        add(PD);*/
    }

    @Override
    public List<ResourceLocation> getCssStyles() {
        return Arrays.asList(new ResourceLocation(ACsGuiApi.RES_LOC_ID, "skineditor.css"));
    }
}
