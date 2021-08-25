package fr.aym.acsguis.cssengine.parsing.core.objects;

public class CssRelativeValue implements CssValue {
    private final int value;

    public CssRelativeValue(int value) {
        this.value = value;
    }

    @Override
    public String stringValue() {
        return value+"%";
    }

    @Override
    public int intValue() {
        return value;
    }

    @Override
    public Unit getUnit() {
        return Unit.RELATIVE_INT;
    }

    @Override
    public String toString() {
        return "CssRelativeValue{" +
                "value=" + value +
                '}';
    }
}
