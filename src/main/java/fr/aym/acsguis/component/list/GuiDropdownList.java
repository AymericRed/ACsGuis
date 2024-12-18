package fr.aym.acsguis.component.list;

import fr.aym.acsguis.component.EnumComponentType;
import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.button.GuiButton;
import fr.aym.acsguis.component.layout.GridLayout;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.panel.GuiScrollPane;
import fr.aym.acsguis.component.style.ComponentStyle;
import fr.aym.acsguis.component.style.InternalComponentStyle;
import fr.aym.acsguis.component.textarea.GuiLabel;
import fr.aym.acsguis.cssengine.positionning.Size;
import fr.aym.acsguis.event.listeners.mouse.IMouseClickListener;
import fr.aym.acsguis.utils.GuiConstants;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class GuiDropdownList extends GuiPanel implements IMouseClickListener {
    private final GuiButton button;
    private final GuiPanel panel;
    private String selectedElement;
    @Nullable
    private Consumer<String> changeCallback;

    public GuiDropdownList(String label, List<String> elements) {
        super();
        panel = new GuiScrollPane();
        add((button = new GuiButton(label)).addClickListener((mouseX, mouseY, mouseButton) -> ((InternalComponentStyle) panel.getStyleCustomizer()).setVisible(!panel.isVisible())));

        panel.setLayout(new GridLayout(new Size.SizeValue(1, GuiConstants.ENUM_SIZE.RELATIVE), new Size.SizeValue(20, GuiConstants.ENUM_SIZE.ABSOLUTE), new Size.SizeValue(1, GuiConstants.ENUM_SIZE.ABSOLUTE), GridLayout.GridDirection.HORIZONTAL, 1));
        setElements(elements);
        ((InternalComponentStyle) panel.getStyleCustomizer()).setVisible(false);
        add(panel);
    }

    public void setElements(List<String> elements) {
        panel.removeAllChildren();
        for (String s : elements) {
            panel.add(new GuiLabel(s).addClickListener((mouseX, mouseY, mouseButton) -> {
                selectedElement = s;
                ((InternalComponentStyle) panel.getStyleCustomizer()).setVisible(false);
                if (changeCallback != null)
                    changeCallback.accept(s);
            }));
        }
    }

    public GuiDropdownList setChangeCallback(@Nullable Consumer<String> changeCallback) {
        this.changeCallback = changeCallback;
        return this;
    }

    @Nullable
    public Consumer<String> getChangeCallback() {
        return changeCallback;
    }

    @Override
    public EnumComponentType getType() {
        return EnumComponentType.DROPDOWN_LIST;
    }

    public GuiButton getButton() {
        return button;
    }

    public GuiPanel getPanel() {
        return panel;
    }

    public void setSelectedElement(String selectedElement) {
        this.selectedElement = selectedElement;
    }

    public String getSelectedElement() {
        return selectedElement;
    }

    public void closeDropdown() {
        ((InternalComponentStyle) panel.getStyleCustomizer()).setVisible(false);
    }

    public void setLabel(String label) {
        getButton().setText(label);
    }

    @Override
    public float getRenderMaxY() {
        return super.getRenderMaxY() + (panel.isVisible() ? panel.getHeight() : 0);
    }

    @Override
    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= getScreenX() && mouseX < getScreenX() + getWidth() && mouseY >= getScreenY() && mouseY < getScreenY() + getHeight() + (panel.isVisible() ? panel.getHeight() : 0);
    }

    @Override
    public GuiComponent setParent(GuiPanel parent) {
        if (this.parent != null && parent != this.parent) {
            this.parent.getClickListeners().remove(this);
        }
        if (parent != null && parent != this.parent) {
            parent.addClickListener(this);
        }
        return super.setParent(parent);
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!isMouseOver(mouseX, mouseY)) {
            closeDropdown();
        }
    }
}
