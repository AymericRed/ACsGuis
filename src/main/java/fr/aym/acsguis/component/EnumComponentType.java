package fr.aym.acsguis.component;

/**
 * All ACsGuis component types, usable in css code to refer to all elements of this type
 */
public enum EnumComponentType
{
    PANEL,
    SCROLL_PANE,
    TABBED_PANE,
    LABEL,
    TEXT_AREA,
    TEXT_FIELD,
    BUTTON,
    SLIDER_HORIZONTAL,
    SLIDER_VERTICAL,
    CHECKBOX,
    COMBO_BOX,
    SLOT,
    PROGRESS_BAR,
    ENTITY_RENDER,
    CAMERA_VIEW;

    public static EnumComponentType fromString(String value) {
        for(EnumComponentType t : values())
        {
            if(t.name().toLowerCase().equals(value))
                return t;
        }
        return null;
    }
}
