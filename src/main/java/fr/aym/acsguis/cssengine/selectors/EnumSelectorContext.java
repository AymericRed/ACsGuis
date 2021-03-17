package fr.aym.acsguis.cssengine.selectors;

/**
 * {@link CssSelector} contexts
 */
public enum EnumSelectorContext
{
    /** Base context */
    NORMAL(null),
    /** Active when the element is hovered */
    HOVER(NORMAL),
    /** Active when the element is disabled */
    DISABLED(NORMAL),
    /** Active when the element is pressed */
    ACTIVE(HOVER);

    private final EnumSelectorContext parent;

    EnumSelectorContext(EnumSelectorContext parent) {
        this.parent = parent;
    }

    public boolean isParent(EnumSelectorContext context) {
        return this == context.parent || (context.parent != null && context.parent.parent == this);
    }

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
