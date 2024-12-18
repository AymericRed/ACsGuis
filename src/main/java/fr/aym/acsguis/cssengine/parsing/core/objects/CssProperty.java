package fr.aym.acsguis.cssengine.parsing.core.objects;

import fr.aym.acsguis.cssengine.style.EnumCssStyleProperty;

/**
 * A css property
 */
public class CssProperty
{
    private final String key;
    private final CssValue value;

    private String sourceLocation;

    public CssProperty(String sourceLocation, String key, CssValue value) {
        this.sourceLocation = sourceLocation;
        this.key = key;
        this.value = value;
    }

    /**
     * @return The name of the property, should match one in {@link EnumCssStyleProperty}
     */
    public String getKey() {
        return key;
    }

    /**
     * @return The value of the property
     */
    public CssValue getValue() {
        return value;
    }

    /**
     * @return The line where this property is declared
     */
    public String getSourceLocation() {
        return sourceLocation;
    }

    /**
     * Sets the line where this property is declared
     */
    public void setSourceLocation(String sourceLocation) {
        this.sourceLocation = sourceLocation;
    }
}
