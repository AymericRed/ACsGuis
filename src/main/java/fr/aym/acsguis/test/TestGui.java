package fr.aym.acsguis.test;

import fr.aym.acsguis.component.button.GuiButton;
import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.component.panel.GuiPanel;
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
        super(new GuiScaler.Identity());
        style.setBackgroundColor(Color.TRANSLUCENT);
        setCssClass("home");
        GuiPanel PD = new GuiPanel(0, 0, 0, 0);
        PD.add(new GuiButton("Bouton css"));
        add(PD);
    }

    @Override
    public List<ResourceLocation> getCssStyles() {
        return Arrays.asList(GuiDnxDebug.RESOURCE_LOCATION);
    }
}
