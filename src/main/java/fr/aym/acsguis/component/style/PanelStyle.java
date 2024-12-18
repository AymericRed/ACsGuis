package fr.aym.acsguis.component.style;

import fr.aym.acsguis.component.layout.PanelLayout;

public interface PanelStyle extends ComponentStyle
{
    PanelLayout<?> getLayout();
    PanelStyle setLayout(PanelLayout<?> panelLayout);
}
