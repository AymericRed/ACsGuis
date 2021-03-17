package fr.aym.acsguis.cssengine.selectors;

/**
 * {@link CssSelector} contexts
 */
public enum EnumSelectorContext
{
    /** Base context */
    NORMAL,
    /** Active when the element is hovered */
    HOVER,
    /** Active when the element is disabled */
    DISABLED,
    /** Active when the element is pressed */
    ACTIVE;

    /**
     * Parsing method
     */
    public static EnumSelectorContext fromString(String value) {
        for(EnumSelectorContext t : values())
        {
            if(t.name().toLowerCase().equals(value))
                return t;
        }
        throw new IllegalArgumentException("Unknown selector context (for example :hover) : "+value);
    }
}
