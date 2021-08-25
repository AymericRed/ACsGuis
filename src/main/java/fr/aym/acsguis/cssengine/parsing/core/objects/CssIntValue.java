package fr.aym.acsguis.cssengine.parsing.core.objects;

public class CssIntValue implements CssValue {
    private final int value;

    public CssIntValue(int value) {
        this.value = value;
    }

    @Override
    public String stringValue() {
        return value+"px";
    }

    @Override
    public int intValue() {
        return value;
    }

    @Override
    public Unit getUnit() {
        return Unit.ABSOLUTE_INT;
    }

    @Override
    public String toString() {
        return "CssIntValue{" +
                "value=" + value +
                '}';
    }
}
