package fr.aym.acsguis.utils;

import fr.aym.acsguis.component.panel.GuiFrame;

public class ComponentRenderContext {
    private final GuiFrame parentGui;
    private final boolean enableScissors;
    private final GuiFrame.GuiType guiType;

    public ComponentRenderContext(GuiFrame parentGui, boolean enableScissors, GuiFrame.GuiType guiType) {
        this.parentGui = parentGui;
        this.enableScissors = enableScissors;
        this.guiType = guiType;
    }

    public GuiFrame getParentGui() {
        return parentGui;
    }

    public boolean enableScissors() {
        return enableScissors;
    }

    public GuiFrame.GuiType getGuiType() {
        return guiType;
    }
}
