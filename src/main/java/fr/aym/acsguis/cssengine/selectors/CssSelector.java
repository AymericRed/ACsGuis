package fr.aym.acsguis.cssengine.selectors;

import fr.aym.acsguis.component.EnumComponentType;
import fr.aym.acsguis.component.GuiComponent;

import java.util.Objects;

/**
 * A css selector
 *
 * @param <T> The type of the target
 */
public class CssSelector<T>
{
    /**
     * The selector type
     */
    private final EnumSelectorType type;
    /**
     * The context to which this selector applies
     */
    private EnumSelectorContext context = EnumSelectorContext.NORMAL;
    /**
     * The target, a {@link EnumComponentType} if its a COMPONENT_TYPE selector, or a String
     */
    private final T target;

    public CssSelector(EnumSelectorType type, T target) {
        this.type = type;
        this.target = target;
    }

    /**
     * Set the context : when the selector matches
     */
    public void setContext(EnumSelectorContext context) {
        this.context = context;
    }

    /**
     * @return the context : when the selector matches
     */
    public EnumSelectorContext getContext() {
        return context;
    }

    @Override
    public String toString() {
        return "{" +
                "type=" + type +
                ", context=" + context +
                ", target=" + target +
                '}';
    }

    /**
     * Tests the given component against this selector
     *
     * @param to The component to test
     * @param targetContext If null, the context is not checked, else checks if it's equals to the context of this selector, or inherits from it (NORMAL is the base context)
     *
     * @return True if this selector applies to the given css gui component
     */
    public boolean applies(GuiComponent to, EnumSelectorContext targetContext) {
        if(targetContext != null && to.getStyle().getContext() != context && !(context.isParent(to.getStyle().getContext())))
        {
            //System.out.println("[FAIL] "+targetContext+" "+to+" "+to.getStyle().getContext()+" "+this);
            return false;
        }
        switch (type)
        {
            case COMPONENT_TYPE:
                if(target != to.getType())
                    return false;
                break;
            case ID:
                if(to.getCssId() == null || !to.getCssId().equals(target))
                    return false;
                break;
            case CLASS:
                //System.out.println("[CLASS] "+to.getCssClass()+" "+to+" "+to.getStyle().getContext()+" "+this);
                if(to.getCssClass() == null || !to.getCssClass().equals(target))
                    return false;
                break;
            case A_COMPONENT:
                return to == target;
        }
        return true;
    }

    /**
     * Selector types
     */
    public enum EnumSelectorType
    {
        COMPONENT_TYPE,
        ID,
        CLASS,
        A_COMPONENT
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CssSelector<?> that = (CssSelector<?>) o;
        return type == that.type &&
                context == that.context &&
                target.equals(that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, context, target);
    }
}
