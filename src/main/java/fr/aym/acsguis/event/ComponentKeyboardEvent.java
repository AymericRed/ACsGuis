package fr.aym.acsguis.event;

import fr.aym.acsguis.component.GuiComponent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public abstract class ComponentKeyboardEvent extends Event {

    public final GuiComponent component;

    public ComponentKeyboardEvent(GuiComponent component) {
        this.component = component;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    public static class ComponentKeyTypeEvent extends ComponentKeyboardEvent {

        public final char typedChar;
        public final int keyCode;

        public ComponentKeyTypeEvent(GuiComponent component, char typedChar, int keyCode) {
            super(component);
            this.typedChar = typedChar;
            this.keyCode = keyCode;
        }
    }
}
