package fr.aym.acsguis.cssengine.style;

import fr.aym.acsguis.cssengine.selectors.EnumSelectorContext;

public interface CssStyleApplier<T>
{
    void apply(EnumSelectorContext context, CssStyleProperty<T> style, ComponentStyleManager target);
}
