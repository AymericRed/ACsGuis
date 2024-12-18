package fr.aym.acsguis.component.panel;

import fr.aym.acsguis.api.ACsGuiApi;
import fr.aym.acsguis.component.EnumComponentType;
import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.button.GuiButton;
import fr.aym.acsguis.component.style.InternalComponentStyle;
import fr.aym.acsguis.cssengine.selectors.EnumSelectorContext;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperty;
import fr.aym.acsguis.event.listeners.IGuiCloseListener;
import fr.aym.acsguis.event.listeners.IGuiOpenListener;
import fr.aym.acsguis.event.listeners.IKeyboardListener;
import fr.aym.acsguis.event.listeners.mouse.IMouseClickListener;
import fr.aym.acsguis.event.listeners.mouse.IMouseExtraClickListener;
import fr.aym.acsguis.event.listeners.mouse.IMouseMoveListener;
import fr.aym.acsguis.event.listeners.mouse.IMouseWheelListener;
import fr.aym.acsguis.sqript.SqriptCompatiblity;
import fr.aym.acsguis.utils.ComponentRenderContext;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class GuiTabbedPane extends GuiPanel implements IGuiOpenListener, IGuiCloseListener, IKeyboardListener, IMouseWheelListener, IMouseClickListener, IMouseExtraClickListener, IMouseMoveListener {
    protected List<GuiTabbedPaneButton> tabsButtons = new ArrayList<>();
    protected List<GuiPanel> tabsContainers = new ArrayList<>();

    public GuiTabbedPane() {
        addOpenListener(this);
        addCloseListener(this);
        addKeyboardListener(this);
        addWheelListener(this);
        addClickListener(this);
        addExtraClickListener(this);
        addMoveListener(this);
    }

    @Override
    public EnumComponentType getType() {
        return EnumComponentType.TABBED_PANE;
    }

    @Override
    public void drawBackground(int mouseX, int mouseY, float partialTicks, ComponentRenderContext enableScissor) {
        super.drawBackground(mouseX, mouseY, partialTicks, enableScissor);
		
		/*for(String str : tabsContainers.keySet()) {
			GuiPanel tabContainer = tabsContainers.get(str);
			tabContainer.getStyle().setOffsetY(10);
			tabContainer.render(mouseX, mouseY, partialTicks);
		}*/
    }

    @Override
    public GuiPanel add(GuiComponent component) {
        if (ACsGuiApi.getSqriptSupport().isSqriptLoaded() && component instanceof GuiPanel) {
            addTab(SqriptCompatiblity.nextPannedTabName, (GuiPanel) component);
        } else {
            super.add(component);
        }
        return this;
    }

    public void addTab(String tabName, GuiPanel tabContainer) {
        GuiTabbedPaneButton tabButton = new GuiTabbedPaneButton(tabsContainers.size());
        tabButton.setText(tabName).getStyleCustomizer().withAutoStyles(this, EnumCssStyleProperty.WIDTH, EnumCssStyleProperty.HEIGHT, EnumCssStyleProperty.LEFT, EnumCssStyleProperty.COLOR);
        add(tabButton);
        tabsButtons.add(tabButton);

        tabContainer.setParent(this);
        tabsContainers.add(tabContainer);
        tabContainer.getStyle().setOffsetY(20);
        super.add(tabContainer);

        selectTab(tabsContainers.size() - 1);
    }

    public GuiPanel getTabContainer(int tabIndex) {
        if (tabsContainers.size() < tabIndex) {
            return tabsContainers.get(tabIndex);
        } else {
            return null;
        }
    }

    public GuiTabbedPaneButton getTabButton(int tabIndex) {
        if (tabIndex < tabsButtons.size()) {
            return tabsButtons.get(tabIndex);
        } else {
            return null;
        }
    }

    private int selectedTab;

    public void selectTab(int tabIndex) {
        tabIndex = MathHelper.clamp(tabIndex, 0, tabsContainers.size());
        selectedTab = tabIndex;
        for (int i = 0; i < tabsContainers.size(); i++) {
            if (i == tabIndex) {
                ((InternalComponentStyle) tabsContainers.get(i)).setVisible(true);
            } else {
               ((InternalComponentStyle) tabsContainers.get(i)).setVisible(false);
            }
        }
        /*for (GuiComponent child : getChildComponents()) {
            child.getStyle().refreshStyle(getGui());
        }*/
    }

    public int getSelectedTabIndex() {
        return selectedTab;
    }

    public GuiPanel getSelectedTab() {
        return tabsContainers.get(selectedTab);
    }

    public class GuiTabbedPaneButton extends GuiButton {
        protected final int index;

        protected GuiTabbedPaneButton(int index) {
            this.index = index;
        }

        @Override
        public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
            super.onMouseClicked(mouseX, mouseY, mouseButton);
            //GuiTabbedPane.this.getStyle().refreshStyle(false);
            selectTab(index);
        }

        @Override
        public EnumSelectorContext getState() {
            if (GuiTabbedPane.this.selectedTab == index)
                return EnumSelectorContext.ACTIVE;
            else
                return super.getState();
        }
    }

    @Override
    public boolean handleProperty(EnumCssStyleProperty property, EnumSelectorContext context, InternalComponentStyle target) {
        if (target.getOwner() instanceof GuiTabbedPaneButton) {
            switch (property) {
                case WIDTH: {
                    int c = tabsButtons.size();
                    float w = (getWidth() - 10) / c;
                    target.getWidth().setAbsolute(w);
                    return true;
                }
                case LEFT: {
                    int c = tabsButtons.size();
                    float w = (getWidth() - 10) / c;
                    target.getXPos().setAbsolute(5 + ((GuiTabbedPaneButton) target.getOwner()).index * w);
                    return true;
                }
                case COLOR:
                    if (((GuiTabbedPaneButton) target.getOwner()).index == selectedTab) {
                        target.setForegroundColor(tabsContainers.get(((GuiTabbedPaneButton) target.getOwner()).index).getStyle().getForegroundColor());
                    } else
                        target.setForegroundColor(14737632);
                    return true;
                default:
                    return false;
            }
        } else {
            return super.handleProperty(property, context, target);
        }
    }

	/*@Override
	public Priority getPriority(InternalComponentStyle forT) {
		return forT.getOwner() instanceof GuiTabbedPaneButton ? Priority.PARENT : super.getPriority(forT);
	}*/

    @Override
    public void onGuiOpen() {
		/*for(String str : tabsContainers.keySet()) {
			GuiPanel container = tabsContainers.get(str);
			container.guiOpen();
		}
		
		for(String str : tabsButtons.keySet()) {
			GuiTabbedPaneButton button = tabsButtons.get(str);
			button.guiOpen();
		}*/
    }

    @Override
    public void onGuiClose() {
		/*for(String str : tabsContainers.keySet()) {
			GuiPanel container = tabsContainers.get(str);
			container.guiClose();
		}
		
		for(String str : tabsButtons.keySet()) {
			GuiTabbedPaneButton button = tabsButtons.get(str);
			button.guiClose();
		}*/
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
		/*for(String str : tabsContainers.keySet()) {
			GuiPanel container = tabsContainers.get(str);
			container.keyTyped(typedChar, keyCode);
		}*/
    }

    @Override
    public void onMouseWheel(int dWheel) {
		/*for(String str : tabsContainers.keySet()) {
			GuiPanel container = tabsContainers.get(str);
			container.mouseWheel(dWheel);
		}*/
    }

    @Override
    public void onMouseMoved(int mouseX, int mouseY) {
		/*for(String str : tabsContainers.keySet()) {
			GuiPanel container = tabsContainers.get(str);
			container.mouseMoved(mouseX, mouseY, true);
		}
		
		for(String str : tabsButtons.keySet()) {
			GuiTabbedPaneButton button = tabsButtons.get(str);
			button.mouseMoved(mouseX, mouseY, true);
		}*/
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {
		/*for(String str : tabsContainers.keySet()) {
			GuiPanel container = tabsContainers.get(str);
			container.mouseReleased(mouseX, mouseY, mouseButton);
		}
		
		for(String str : tabsButtons.keySet()) {
			GuiTabbedPaneButton button = tabsButtons.get(str);
			button.mouseReleased(mouseX, mouseY, mouseButton);
		}*/
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
		/*for (String str : tabsContainers.keySet()) {
			GuiPanel container = tabsContainers.get(str);
			container.mouseClicked(mouseX, mouseY, mouseButton, true);
		}
		
		for (String str : tabsButtons.keySet()) {
			GuiTabbedPaneButton button = tabsButtons.get(str);
			button.mouseClicked(mouseX, mouseY, mouseButton, true);
		}*/
    }

    @Override
    public void onMouseHover(int mouseX, int mouseY) {
    }

    @Override
    public void onMouseUnhover(int mouseX, int mouseY) {
    }

    @Override
    public void onMouseDoubleClicked(int mouseX, int mouseY, int mouseButton) {
    }

    @Override
    public void onMousePressed(int mouseX, int mouseY, int mouseButton) {
    }

}
