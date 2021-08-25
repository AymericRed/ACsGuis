package fr.aym.acsguis.cssengine.parsing.core.objects;

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

    public String getKey() {
        return key;
    }

    public CssValue getValue() {
        return value;
    }

    public String getSourceLocation() {
        return sourceLocation;
    }

    public void setSourceLocation(String sourceLocation) {
        this.sourceLocation = sourceLocation;
    }
}
