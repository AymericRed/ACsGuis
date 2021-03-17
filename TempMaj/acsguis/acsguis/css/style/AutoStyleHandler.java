package fr.aym.acsguis.cssengine.style;

import fr.aym.acsguis.cssengine.selectors.EnumSelectorContext;

import java.util.List;

/**
 * Callback for elements that support "auto" css properties, to let them compute it
 * @param <T> The component receiving the auto style
 */
public interface AutoStyleHandler<T extends ComponentStyleManager>
{
    /**
     * Computes "auto" style of a property
     *
     * @param property The property to compute
     * @param context The context when the style is applied
     * @param target The component receiving the auto style
     * @return True if the property was computed, false to let default behavior (inheriting) happen
     */
    boolean handleProperty(EnumCssStyleProperties property, EnumSelectorContext context, T target);

    /**
     * Higher priorities will be handled before low priorities <br>
     *     If you return true if some handleProperty, lowest priorities won't be applied
     * @param forT The component where the style will be applied
     * @return The priority, COMPONENT by default
     */
    default Priority getPriority(T forT) {return Priority.COMPONENT;}

    /**
     * Should return the modified properties, used to filter handleProperty(...) calls <br>
     *     You can store the return field in a static list for optimization
     *
     * @param target The component receiving the auto style
     * @return The modified properties, including modified properties in super(...) implementations
     */
    List<EnumCssStyleProperties> getModifiedProperties(T target);

    /**
     * Auto style priorities <br>
     *     LAYOUT > PARENT > COMPONENT
     */
    enum Priority
    {
        LAYOUT, PARENT, COMPONENT
    }
}
