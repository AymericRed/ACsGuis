package fr.aym.acsguis.event;

import fr.aym.acsguis.component.GuiComponent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public abstract class ComponentStateEvent extends Event {
    public final GuiComponent component;

    public ComponentStateEvent(GuiComponent component) {
        this.component = component;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    public static class ComponentCloseEvent extends ComponentStateEvent {

        public ComponentCloseEvent(GuiComponent component) {
            super(component);
        }
    }

    public static class ComponentOpenEvent extends ComponentStateEvent {

        public ComponentOpenEvent(GuiComponent component) {
            super(component);
        }
    }

    public static class ComponentFocusEvent extends ComponentStateEvent {

        public final boolean gainFocus;

        public ComponentFocusEvent(GuiComponent component) {
            super(component);
            this.gainFocus = !component.isFocused();
        }
    }

    public static class ComponentTickEvent extends ComponentStateEvent {

        public ComponentTickEvent(GuiComponent component) {
            super(component);
        }
    }
}
