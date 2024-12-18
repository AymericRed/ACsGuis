package fr.aym.acsguis.component.panel;

import fr.aym.acsguis.component.EnumComponentType;
import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.layout.PanelLayout;
import fr.aym.acsguis.component.style.AutoStyleHandler;
import fr.aym.acsguis.component.style.InternalComponentStyle;
import fr.aym.acsguis.cssengine.selectors.EnumSelectorContext;
import fr.aym.acsguis.cssengine.style.CssPanelStyle;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperty;
import fr.aym.acsguis.utils.ComponentRenderContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class GuiPanel extends GuiComponent implements AutoStyleHandler<InternalComponentStyle> {
    protected List<GuiComponent> childComponents = new ArrayList<>();

    protected List<GuiComponent> queuedComponents = new ArrayList<>();
    protected List<GuiComponent> toRemoveComponents = new ArrayList<>();

    protected PanelLayout<?> layout;//TODO = new FlowLayout();

    @Override
    public EnumComponentType getType() {
        return EnumComponentType.PANEL;
    }

    @Override
    protected InternalComponentStyle createStyleManager() {
        CssPanelStyle s = new CssPanelStyle(this);
        s.getCustomizer().withAutoStyles(this, EnumCssStyleProperty.HEIGHT);
        return s;
    }

    /**
     * Changes the layout of this panel <br>
     * If the panel has elements, it automatically recomputes their position and size
     *
     * @see PanelLayout
     */
    public void setLayout(PanelLayout<?> layout) {
        boolean dif = this.layout != layout;
        if (this.layout != null) {
            this.layout.clear();
        }
        if (!dif) {
            return;
        }
        for (GuiComponent c : queuedComponents) {
            if (layout != null)
                c.getStyleCustomizer().withAutoStyles(layout, layout.getModifiedProperties());
            if (this.layout != null)
                c.getStyleCustomizer().removeAutoStyles(this.layout, this.layout.getModifiedProperties());
        }
        for (GuiComponent c : childComponents) {
            if (!toRemoveComponents.contains(c)) {
                if (layout != null)
                    c.getStyleCustomizer().withAutoStyles(layout, layout.getModifiedProperties());
                if (this.layout != null)
                    c.getStyleCustomizer().removeAutoStyles(this.layout, this.layout.getModifiedProperties());
            }
        }
        this.layout = layout;
        if (layout != null) {
            layout.setContainer(this);
        }
    }

    public PanelLayout<?> getLayout() {
        return layout;
    }

    @Override
    public boolean handleProperty(EnumCssStyleProperty property, EnumSelectorContext context, InternalComponentStyle target) {
        if (property == EnumCssStyleProperty.HEIGHT) {
            float height = 0;
            for (GuiComponent c : queuedComponents) {
                height = Math.max(height, c.getY() + c.getStyle().getOffsetY() + c.getHeight());
            }
            for (GuiComponent c : childComponents) {
                if (!toRemoveComponents.contains(c))
                    height = Math.max(height, c.getY() + c.getStyle().getOffsetY() + c.getHeight());
            }
            target.getHeight().setAbsolute(height);
            return true;
        }
        return false;
    }

    /**
     * Add a child component to this GuiPanel.
     * The child component will be updated, rendered, etc,
     * with its parent.
     *
     * @param component The child component
     */
    public GuiPanel add(GuiComponent component) {
        component.setParent(this);
        if (layout != null)
            component.getStyleCustomizer().withAutoStyles(layout, layout.getModifiedProperties());
        queuedComponents.add(component);
        return this;
    }

    public GuiPanel remove(GuiComponent component) {
        if (layout != null) {
            component.getStyleCustomizer().removeAutoStyles(layout, layout.getModifiedProperties());
        }
        toRemoveComponents.add(component);
        return this;
    }

    public void removeAllChildren() {
        if (layout != null)
            layout.clear();
        queuedComponents.clear();
        toRemoveComponents.addAll(childComponents);
    }

    public List<GuiComponent> getQueuedComponents() {
        return queuedComponents;
    }

    public List<GuiComponent> getToRemoveComponents() {
        return toRemoveComponents;
    }

    public void flushComponentsQueue() {
        if (queuedComponents.isEmpty()) {
            return;
        }
        Iterator<GuiComponent> queuedComponentsIterator = queuedComponents.iterator();
        while (queuedComponentsIterator.hasNext()) {
            GuiComponent component = queuedComponentsIterator.next();
            if (getStyle().getCssStack() != null) {
                component.getStyle().reloadCssStack();
            }
            GuiFrame frame = getGui().getFrame();
            component.resize(getGui(), frame.getResolution().getScaledWidth(), frame.getResolution().getScaledHeight());
            getChildComponents().add(component);
            //the resize already refresh the style component.getStyle().refreshCss(false);
            queuedComponentsIterator.remove();
            if (component instanceof GuiPanel) {
                ((GuiPanel) component).flushComponentsQueue();
            }
        }
        Collections.sort((List) getChildComponents());
    }

    @Override
    public void resize(GuiFrame.APIGuiScreen gui, int screenWidth, int screenHeight) {
        super.resize(gui, screenWidth, screenHeight);
        this.getReversedChildComponents().forEach(component -> component.resize(gui, screenWidth, screenHeight));
        /*
        TODO USELESS SI PANEL LISTEN CHANGEMENTS DE TAILLE DES ENFANTS
        if(getLayout() != null) {
            getLayout().clear();
            getStyle().refreshStyle(gui, getLayout().getModifiedProperties());
        }*/
    }

    public void flushRemovedComponents() {
        if (toRemoveComponents.isEmpty()) {
            return;
        }
        Iterator<GuiComponent> toRemoveComponentsIterator = toRemoveComponents.iterator();
        while (toRemoveComponentsIterator.hasNext()) {
            GuiComponent component = toRemoveComponentsIterator.next();
            getChildComponents().remove(component);
            toRemoveComponentsIterator.remove();
            if (component instanceof GuiPanel) {
                ((GuiPanel) component).flushRemovedComponents();
            }
        }
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTicks, ComponentRenderContext enableScissor) {
        for (GuiComponent component : getChildComponents()) {
            component.render(mouseX, mouseY, partialTicks, enableScissor);
        }

        super.drawForeground(mouseX, mouseY, partialTicks, enableScissor);
    }

    public List<GuiComponent> getChildComponents() {
        return childComponents;
    }

    public List<GuiComponent> getReversedChildComponents() {
        List<GuiComponent> components = new ArrayList<>();
        if (getChildComponents() != null) {
            components.addAll(getChildComponents());
            Collections.reverse(components);
        }
        return components;
    }
}
