package fr.aym.acsguis.cssengine.style;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.layout.PanelLayout;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.style.PanelStyleManager;

public class CssPanelStyleManager extends CssComponentStyleManager implements PanelStyleManager
{
    private final GuiPanel panel;

    public CssPanelStyleManager(GuiPanel component) {
        super(component);
        panel = component;
    }

    @Override
    public PanelLayout<?> getLayout() {
        return panel.getLayout();
    }

    @Override
    public PanelStyleManager setLayout(PanelLayout<?> panelLayout) {
        panel.setLayout(panelLayout);
        return this;
    }
}
