package fr.aym.acsguis.cssengine.parsing.core.objects;

public class CssStringValue implements CssValue {
    private final String value;

    public CssStringValue(String value) {
        this.value = value;
    }

    @Override
    public String stringValue() {
        return value;
    }

    @Override
    public int intValue() {
        throw new IllegalArgumentException("Not a number");
    }

    @Override
    public Unit getUnit() {
        return Unit.STRING;
    }

    @Override
    public String toString() {
        return "CssStringValue{" +
                "value='" + value + '\'' +
                '}';
    }
}
