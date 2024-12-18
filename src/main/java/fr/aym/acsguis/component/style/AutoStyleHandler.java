package fr.aym.acsguis.component.style;

import fr.aym.acsguis.cssengine.selectors.EnumSelectorContext;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperty;

/**
 * Callback for elements that support "auto" css properties, to let them compute it
 * @param <T> The component receiving the auto style
 */
public interface AutoStyleHandler<T extends InternalComponentStyle>
{
    /**
     * Computes "auto" style of a property
     *
     * @param property The property to compute
     * @param context The context when the style is applied
     * @param target The component receiving the auto style
     * @return True if the property was computed, false to let default behavior (inheriting) happen
     */
    boolean handleProperty(EnumCssStyleProperty property, EnumSelectorContext context, T target);

    /**
     * Higher priorities will be handled before low priorities <br>
     *     If you return true if some handleProperty, lowest priorities won't be applied
     * @param forT The component where the style will be applied
     * @return The priority, COMPONENT by default
     */
    default Priority getPriority(T forT) {return Priority.COMPONENT;}

    /**
     * Auto style priorities <br>
     *     IGNORE_LAYOUT > LAYOUT > COMPONENT
     */
    enum Priority
    {
        IGNORE_LAYOUT, LAYOUT, COMPONENT
    }

    interface SimpleStyleFunction {
        void apply(InternalComponentStyle target);
    }
}
