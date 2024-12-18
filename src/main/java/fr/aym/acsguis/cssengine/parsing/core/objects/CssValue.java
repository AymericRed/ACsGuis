package fr.aym.acsguis.cssengine.parsing.core.objects;

/**
 * A css value
 *
 * @see CssStringValue
 * @see CssIntValue
 * @see CssRelativeValue
 */
public interface CssValue
{
    /**
     * @return The string value of this css value
     */
    String stringValue();

    /**
     * @return The int value of this css value
     * @throws IllegalArgumentException If this is not a numerical value
     */
    int intValue();

    /**
     * @return The unit of the value
     */
    Unit getUnit();

    enum Unit
    {
        STRING,
        ABSOLUTE_INT,
        RELATIVE_TO_PARENT,
        RELATIVE_TO_WINDOW_WIDTH,
        RELATIVE_TO_WINDOW_HEIGHT
    }
}
