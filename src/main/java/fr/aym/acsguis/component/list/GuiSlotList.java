package fr.aym.acsguis.component.list;

import fr.aym.acsguis.component.list.slot.GuiSlot;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperty;
import fr.aym.acsguis.event.listeners.IFocusListener;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static fr.aym.acsguis.cssengine.parsing.core.objects.CssValue.Unit.RELATIVE_TO_PARENT;

public class GuiSlotList extends GuiPanel {

    protected final GuiList list;
    protected final List<GuiSlot> slots = new ArrayList<GuiSlot>();

    public GuiSlotList(GuiList list) {
        this.list = list;
        getStyleCustomizer().withAutoStyle(EnumCssStyleProperty.WIDTH, t -> t.getYPos().setRelative(1, RELATIVE_TO_PARENT))
                .withAutoStyle(EnumCssStyleProperty.BACKGROUND_COLOR, t -> t.setBackgroundColor(new Color(0, 0, 0, 0).getRGB()));
    }

    public GuiSlot getSlotInstance(int n, String entryName) {
        return list.getSlotInstance(n, entryName);
    }

    @Override
    public float getHeight() {
        return list.getListPaddingBottom() + getListSlotsHeight();
    }

    protected float getListSlotsHeight() {
        float maxHeight = 0;
        for (GuiSlot slot : slots) {
            float h = slot.getY() + slot.getHeight();
            if (h > maxHeight) {
                maxHeight = h;
            }
        }
        return maxHeight;
    }

    public void updateSlotList() {
        for (GuiSlot slot : slots) {
            remove(slot);
        }
        slots.clear();
        for (int i = 0; i < list.getEntries().size(); i++) {
            GuiSlot listSlot = getSlotInstance(i, list.getEntries().get(i));
            slots.add(listSlot);
            add(listSlot);
        }
        list.updateSlidersVisibility();
    }

    public void updateFocus(int n) {
        for (GuiSlot slot : slots) {
            boolean wasFocused = slot.isFocused();
            slot.setFocused(slot.getEntryId() == n);

            if (slot.isFocused() && !wasFocused) {
                for (IFocusListener focusListener : slot.getFocusListeners()) {
                    focusListener.onFocus();
                }
            } else if (!slot.isFocused() && wasFocused) {
                for (IFocusListener focusListener : slot.getFocusListeners()) {
                    focusListener.onFocusLoose();
                }
            }
        }
    }

    public List<GuiSlot> getSlots() {
        return slots;
    }

}