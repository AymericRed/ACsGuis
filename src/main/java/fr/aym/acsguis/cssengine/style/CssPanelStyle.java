package fr.aym.acsguis.cssengine.style;

import fr.aym.acsguis.component.layout.PanelLayout;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.style.PanelStyle;

public class CssPanelStyle extends CssComponentStyle implements PanelStyle
{
    private final GuiPanel panel;

    public CssPanelStyle(GuiPanel component) {
        super(component);
        panel = component;
    }

    @Override
    public PanelLayout<?> getLayout() {
        return panel.getLayout();
    }

    @Override
    public PanelStyle setLayout(PanelLayout<?> panelLayout) {
        panel.setLayout(panelLayout);
        return this;
    }
}
