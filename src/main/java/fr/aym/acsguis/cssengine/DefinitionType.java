package fr.aym.acsguis.cssengine;

import fr.aym.acsguis.component.layout.BorderedGridLayout;
import fr.aym.acsguis.component.layout.GridLayout;

import java.util.function.Function;

/**
 * A DefinitionType provides a method to parse a configurable string value to the corresponding java object
 * @param <T> The corresponding object's type
 */
public class DefinitionType<T>
{
    private final Function<String, T> parser;

    /**
     * @param parser Takes the configurable string in argument and should return the corresponding object
     */
    public DefinitionType(Function<String, T> parser) {
        this.parser = parser;
    }

    /**
     * @return This parser's function
     */
    public Function<String, T> getFunction() {
        return parser;
    }

    /**
     * Parses the given configurable string value into the corresponding object
     */
    public T getValue(String value) {
        return getFunction().apply(value);
    }
}