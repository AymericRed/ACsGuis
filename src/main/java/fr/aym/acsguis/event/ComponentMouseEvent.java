package fr.aym.acsguis.event;

import fr.aym.acsguis.component.GuiComponent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public abstract class ComponentMouseEvent extends Event {

    public final GuiComponent component;

    public ComponentMouseEvent(GuiComponent component) {
        this.component = component;
    }

    @Override
    public boolean isCancelable()
    {
        return true;
    }

    public static class ComponentMouseClickEvent extends ComponentMouseEvent {

        public final int mouseX, mouseY;
        public final int mouseButton;

        public ComponentMouseClickEvent(GuiComponent component, int mouseX, int mouseY, int mouseButton) {
            super(component);
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.mouseButton = mouseButton;
        }
    }

    public static class ComponentMousePressEvent extends ComponentMouseEvent {

        public final int mouseX, mouseY;
        public final int mouseButton;

        public ComponentMousePressEvent(GuiComponent component, int mouseX, int mouseY, int mouseButton) {
            super(component);
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.mouseButton = mouseButton;
        }
    }

    public static class ComponentMouseDoubleClickEvent extends ComponentMouseEvent {

        public final long lastClickTime;
        public final int mouseX, mouseY;

        public ComponentMouseDoubleClickEvent(GuiComponent component, long lastClickTime, int mouseX, int mouseY) {
            super(component);
            this.lastClickTime = lastClickTime;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }
    }

    public static class ComponentMouseReleaseEvent extends ComponentMouseEvent {

        public final int mouseX, mouseY;
        public final int mouseButton;

        public ComponentMouseReleaseEvent(GuiComponent component, int mouseX, int mouseY, int mouseButton) {
            super(component);
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.mouseButton = mouseButton;
        }
    }

    public static class ComponentMouseHoverEvent extends ComponentMouseEvent {

        public final int mouseX, mouseY;

        public ComponentMouseHoverEvent(GuiComponent component, int mouseX, int mouseY) {
            super(component);
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }
    }

    public static class ComponentMouseMoveEvent extends ComponentMouseEvent {

        public final int lastMouseX, lastMouseY, mouseX, mouseY;

        public ComponentMouseMoveEvent(GuiComponent component, int lastMouseX, int lastMouseY, int mouseX, int mouseY) {
            super(component);
            this.lastMouseX = lastMouseX;
            this.lastMouseY = lastMouseY;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }
    }

    public static class ComponentMouseWheelEvent extends ComponentMouseEvent {

        public final int dWheel;

        public ComponentMouseWheelEvent(GuiComponent component, int dWheel) {
            super(component);
            this.dWheel = dWheel;
        }

    }

}
