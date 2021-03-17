package fr.aym.acsguis.component.panel;

import fr.aym.acsguis.component.EnumComponentType;
import fr.aym.acsguis.component.button.GuiButton;
import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.cssengine.selectors.EnumSelectorContext;
import fr.aym.acsguis.component.style.ComponentStyleManager;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperties;
import fr.aym.acsguis.event.listeners.IGuiCloseListener;
import fr.aym.acsguis.event.listeners.IGuiOpenListener;
import fr.aym.acsguis.event.listeners.IKeyboardListener;
import fr.aym.acsguis.event.listeners.mouse.IMouseClickListener;
import fr.aym.acsguis.event.listeners.mouse.IMouseExtraClickListener;
import fr.aym.acsguis.event.listeners.mouse.IMouseMoveListener;
import fr.aym.acsguis.event.listeners.mouse.IMouseWheelListener;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuiTabbedPane extends GuiPanel implements IGuiOpenListener, IGuiCloseListener, IKeyboardListener, IMouseWheelListener, IMouseClickListener, IMouseExtraClickListener, IMouseMoveListener
{
	protected List<GuiTabbedPaneButton> tabsButtons = new ArrayList<>();
	protected List<GuiPanel> tabsContainers = new ArrayList<>();
	
	public GuiTabbedPane(int x, int y, int width, int height) {
		super(x, y, width, height);
		
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
	public void drawBackground(int mouseX, int mouseY, float partialTicks)
	{
		super.drawBackground(mouseX, mouseY, partialTicks);
		
		/*for(String str : tabsContainers.keySet()) {
			GuiPanel tabContainer = tabsContainers.get(str);
			tabContainer.getStyle().setOffsetY(10);
			tabContainer.render(mouseX, mouseY, partialTicks);
		}*/
	}
	
	public void addTab(String tabName, GuiPanel tabContainer)
	{
		int width = mc.fontRenderer.getStringWidth(tabName) + 6;

		int x = 0;

		for(GuiButton str : tabsButtons) {
			x += str.getWidth();
		}

		GuiTabbedPaneButton tabButton = new GuiTabbedPaneButton(tabsContainers.size(), x, 0, width, 20);
		tabButton.setText(tabName).getStyle().addAutoStyleHandler(this);
		add(tabButton);
		tabsButtons.add(tabButton);

		tabContainer.setParent(this);
		tabsContainers.add(tabContainer);
		tabContainer.getStyle().setOffsetY(20);
		add(tabContainer);

		selectTab(tabsContainers.size()-1);
	}
	
	public GuiPanel getTabContainer(int tabIndex)
	{
		if(tabsContainers.size() < tabIndex) {
			return tabsContainers.get(tabIndex);
		}  else {
			return null;
		}
	}
	
	public GuiTabbedPaneButton getTabButton(int tabIndex)
	{
		if(tabIndex < tabsButtons.size()) {
			return tabsButtons.get(tabIndex);
		}  else {
			return null;
		}
	}

	private int selectedTab;

	public void selectTab(int tabIndex)
	{
		tabIndex = MathHelper.clamp(tabIndex, 0, tabsContainers.size());
		selectedTab = tabIndex;
		for(int i=0;i<tabsContainers.size();i++) {
			if(i == tabIndex) {
				tabsContainers.get(i).setVisible(true);
			} else {
				tabsContainers.get(i).setVisible(false);
			}
		}
		for(GuiComponent child : getChildComponents())
			child.getStyle().refreshCss(false, "tab_selection_change");
	}
	
	public class GuiTabbedPaneButton extends GuiButton {
		protected final int index;
		protected GuiTabbedPaneButton(int index, int x, int y, int width, int height) {
			super(x, y, width, height);
			this.index = index;
		}
		
		@Override
		public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
			super.onMouseClicked(mouseX, mouseY, mouseButton);
			selectTab(index);
		}

		@Override
		public EnumSelectorContext getState() {
			if(GuiTabbedPane.this.selectedTab == index)
				return EnumSelectorContext.ACTIVE;
			else
				return super.getState();
		}
	}

	@Override
	public boolean handleProperty(EnumCssStyleProperties property, EnumSelectorContext context, ComponentStyleManager target) {
		if(target.getOwner() instanceof GuiTabbedPaneButton)
		{
			switch (property) {
				case WIDTH: {
					int c = tabsButtons.size();
					int w = (getWidth() - 10) / c;
					target.getWidth().setAbsolute(w);
					return true;
				}
				case LEFT: {
					int c = tabsButtons.size();
					int w = (getWidth() - 10) / c;
					target.getXPos().setAbsolute(5 + ((GuiTabbedPaneButton) target.getOwner()).index * w);
					return true;
				}
				case COLOR:
					if(((GuiTabbedPaneButton) target.getOwner()).index == selectedTab)
					{
						target.setForegroundColor(tabsContainers.get(((GuiTabbedPaneButton) target.getOwner()).index).getStyle().getForegroundColor());
					}
					else
						target.setForegroundColor(14737632);
					return true;
				default:
					return false;
			}
		}
		else
			return super.handleProperty(property, context, target);
	}

	private static final List<EnumCssStyleProperties> affectedProperties = Arrays.asList(EnumCssStyleProperties.WIDTH, EnumCssStyleProperties.HEIGHT, EnumCssStyleProperties.LEFT, EnumCssStyleProperties.COLOR);
	@Override
	public List<EnumCssStyleProperties> getModifiedProperties(ComponentStyleManager target) {
		return target.getOwner() instanceof GuiTabbedPaneButton ? affectedProperties : super.getModifiedProperties(target);
	}

	@Override
	public Priority getPriority(ComponentStyleManager forT) {
		return forT.getOwner() instanceof GuiTabbedPaneButton ? Priority.PARENT : super.getPriority(forT);
	}

	@Override
	public void resize(int screenWidth, int screenHeight) {
		super.resize(screenWidth, screenHeight);
	}

	@Override
	public void onGuiOpen()
	{
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
	
	@Override public void onMouseHover(int mouseX, int mouseY) {}
	
	@Override public void onMouseUnhover(int mouseX, int mouseY) {}
	
	@Override public void onMouseDoubleClicked(int mouseX, int mouseY, int mouseButton) {}
	
	@Override public void onMousePressed(int mouseX, int mouseY, int mouseButton) {}
	
}
