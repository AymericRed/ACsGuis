package fr.aym.acsguis.utils;

public class GuiConstants
{
    /**
     * Absolute or relative position
     */
    public enum ENUM_POSITION { ABSOLUTE, RELATIVE, RELATIVE_VW, RELATIVE_VH }

    /**
     * Relative position origins
     */
    public enum ENUM_RELATIVE_POS { START, CENTER, END }

    /**
     * Absolute or relative size
     */
    public enum ENUM_SIZE { ABSOLUTE, RELATIVE, RELATIVE_VW, RELATIVE_VH }
    
	public enum ENUM_ICON_POSITION { CENTER, LEFT, RIGHT, TOP, BOTTOM }
	
    public enum HORIZONTAL_TEXT_ALIGNMENT { CENTER, LEFT, RIGHT, JUSTIFY }
    public enum VERTICAL_TEXT_ALIGNMENT { CENTER, TOP, BOTTOM }

    public enum COMPONENT_DISPLAY { BLOCK, INLINE, NONE }
}
