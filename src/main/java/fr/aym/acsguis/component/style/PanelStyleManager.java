package fr.aym.acsguis.component.style;

import fr.aym.acsguis.component.layout.PanelLayout;

public interface PanelStyleManager extends ComponentStyleManager
{
    PanelLayout<?> getLayout();
    PanelStyleManager setLayout(PanelLayout<?> panelLayout);
}
