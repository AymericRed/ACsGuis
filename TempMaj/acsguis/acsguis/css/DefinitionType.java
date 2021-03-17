package fr.aym.acsguis.cssengine;

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

    /**
     * Build-in definition types for this mod, if you want to use another types, you don't need to add them into this enum
     */
    public enum DynamXDefinitionTypes
    {
        INT(new DefinitionType<>(Integer::parseInt)),
        FLOAT(new DefinitionType<>(Float::parseFloat)),
        BOOL(new DefinitionType<>(Boolean::parseBoolean)),
        STRING(new DefinitionType<>((s) -> s)),
        STRING_ARRAY(new DefinitionType<>((s) -> {
            String[] t = s.split(" ");
            String[] string_array = new String[t.length];
            for(int i = 0; i< string_array.length; i++){
                string_array[i] = t[i];
            }
            return string_array;
        })),
        INT_ARRAY(new DefinitionType<>((s) -> {
            String[] t = s.split(" ");
            int[] int_array = new int[t.length];
            for(int i = 0; i< int_array.length; i++){
                int_array[i] = Integer.parseInt(t[i]);
            }
            return int_array;
        })),
        FLOAT_ARRAY(new DefinitionType<>((s) -> {
            String[] t = s.split(" ");
            float[] int_array = new float[t.length];
            for(int i = 0; i< int_array.length; i++){
                int_array[i] = Float.parseFloat(t[i]);
            }
            return int_array;
        }));

        public final DefinitionType<?> type;

        DynamXDefinitionTypes(DefinitionType<?> type) {
            this.type = type;
        }
    }
}
