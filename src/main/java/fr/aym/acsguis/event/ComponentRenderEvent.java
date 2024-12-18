package fr.aym.acsguis.event;

import fr.aym.acsguis.component.GuiComponent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public abstract class ComponentRenderEvent extends Event {

    public final GuiComponent component;

    public ComponentRenderEvent(GuiComponent component) {
        this.component = component;
    }

    public static class ComponentRenderAllEvent extends ComponentRenderEvent {

        public ComponentRenderAllEvent(GuiComponent component) {
            super(component);
        }
    }

    public static class ComponentRenderBackgroundEvent extends ComponentRenderEvent {

        public ComponentRenderBackgroundEvent(GuiComponent component) {
            super(component);
        }
    }

    public static class ComponentRenderForegroundEvent extends ComponentRenderEvent {

        public ComponentRenderForegroundEvent(GuiComponent component) {
            super(component);
        }
    }

}
