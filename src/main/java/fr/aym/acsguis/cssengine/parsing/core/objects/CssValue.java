package fr.aym.acsguis.cssengine.parsing.core.objects;

public interface CssValue {
    String stringValue();

    int intValue();

    Unit getUnit();

    enum Unit
    {
        STRING,
        ABSOLUTE_INT,
        RELATIVE_INT
    }
}
