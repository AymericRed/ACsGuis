package fr.aym.acsguis.cssengine.style;

import fr.aym.acsguis.component.style.InternalComponentStyle;

public interface CssStyleApplier<T> {
    void apply(CssStyleProperty<T> style, InternalComponentStyle target);
}
