package fr.aym.acsguis.component;

public enum EnumComponentType {
    PANEL, TEXT_AREA, PROGRESS_BAR, SLOT, ENTITY_RENDER, CAMERA_VIEW, BUTTON, LABEL, CHECKBOX, SLIDER_HORIZONTAL, SLIDER_VERTICAL, TEXT_FIELD, TABBED_PANE, SCROLL_PANE, COMBO_BOX;

    public static EnumComponentType fromString(String value) {
        for(EnumComponentType t : values())
        {
            if(t.name().toLowerCase().equals(value))
                return t;
        }
        return null;
    }
}
