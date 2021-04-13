package fr.aym.acsguis.component.panel;

import fr.aym.acsguis.component.EnumComponentType;
import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.style.PanelStyleManager;
import fr.aym.acsguis.cssengine.selectors.EnumSelectorContext;
import fr.aym.acsguis.component.style.AutoStyleHandler;
import fr.aym.acsguis.component.style.ComponentStyleManager;
import fr.aym.acsguis.cssengine.style.CssPanelStyleManager;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperties;
import fr.aym.acsguis.component.layout.PanelLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class GuiPanel extends GuiComponent<PanelStyleManager> implements AutoStyleHandler<ComponentStyleManager> {
	
	protected List<GuiComponent> childComponents = new ArrayList<GuiComponent>();
	
	protected List<GuiComponent> queuedComponents = new ArrayList<GuiComponent>();
	protected List<GuiComponent> toRemoveComponents = new ArrayList<GuiComponent>();

	protected PanelLayout<?> layout;

	public GuiPanel() {
		super();
	}
	public GuiPanel(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	@Override
	public EnumComponentType getType() {
		return EnumComponentType.PANEL;
	}

	@Override
	protected PanelStyleManager createStyleManager() {
		PanelStyleManager s = new CssPanelStyleManager(this);
		s.addAutoStyleHandler(this);
		return s;
	}

	/**
	 * Changes the layout of this panel <br>
	 *     If the panel has elements, it automatically recomputes their position and size
	 *
	 * @see PanelLayout
	 */
	public void setLayout(PanelLayout<?> layout) {
		boolean dif = this.layout != layout;
		if(this.layout != null) {
			this.layout.clear();
		}
		for(GuiComponent<?> c : queuedComponents)
		{
			if(dif) {
				if (layout != null)
					c.getStyle().addAutoStyleHandler(layout);
				if (this.layout != null)
					c.getStyle().removeAutoStyleHandler(this.layout);
			}
			c.getStyle().refreshCss(false, "layout_change");
		}
		for(GuiComponent<?> c : childComponents)
		{
			if(!toRemoveComponents.contains(c)) {
				if(dif) {
					if (layout != null)
						c.getStyle().addAutoStyleHandler(layout);
					if (this.layout != null)
						c.getStyle().removeAutoStyleHandler(this.layout);
				}
				c.getStyle().refreshCss(false, "layout_change");
			}
		}
		this.layout = layout;
	}

	public PanelLayout<?> getLayout() {
		return layout;
	}

	@Override
	public boolean handleProperty(EnumCssStyleProperties property, EnumSelectorContext context, ComponentStyleManager target) {
		if(property == EnumCssStyleProperties.HEIGHT)
		{
			int height = 0;
			for(GuiComponent<?> c : queuedComponents)
			{
				height = Math.max(height, c.getY() + c.getStyle().getOffsetY() + c.getHeight());
			}
			for(GuiComponent<?> c : childComponents)
			{
				if(!toRemoveComponents.contains(c))
					height = Math.max(height, c.getY() + c.getStyle().getOffsetY() + c.getHeight());
			}
			target.getHeight().setAbsolute(height);
		}
		return false;
	}

	private static final List<EnumCssStyleProperties> affectedProperties = Collections.singletonList(EnumCssStyleProperties.HEIGHT);
	@Override
	public List<EnumCssStyleProperties> getModifiedProperties(ComponentStyleManager target) {
		return affectedProperties;
	}

	/**
	 * Add a child component to this GuiPanel.
	 * The child component will be updated, rendered, etc,
	 * with its parent.
	 * @param component The child component
	 */
	public GuiPanel add(GuiComponent component) {
		component.setParent(this);
		if(layout != null)
			component.getStyle().addAutoStyleHandler(layout);
		queuedComponents.add(component);
		return this;
	}
	
	public GuiPanel remove(GuiComponent component) {
		toRemoveComponents.add(component);
		return this;
	}

    public void removeAllChilds()
    {
    	if(layout != null)
    		layout.clear();
    	queuedComponents.clear();
    	toRemoveComponents.addAll(childComponents);
    }
	
    public List<GuiComponent> getQueuedComponents()
    {
		return queuedComponents;
	}

	public List<GuiComponent> getToRemoveComponents() {
		return toRemoveComponents;
	}

	public void flushComponentsQueue()
	{
		Iterator<GuiComponent> queuedComponentsIterator = queuedComponents.iterator();
		
		while(queuedComponentsIterator.hasNext())
		{
			GuiComponent component = queuedComponentsIterator.next();
			if(getStyle().getCssStack() != null)
				component.getStyle().reloadCssStack();
			component.resize(GuiFrame.resolution.getScaledWidth(), GuiFrame.resolution.getScaledHeight());
			getChildComponents().add(component);
			//the resize already refresh the style component.getStyle().refreshCss(false);
			
			if(this instanceof GuiScrollPane)
				((GuiScrollPane) this).updateSlidersVisibility();
			
			queuedComponentsIterator.remove();

			if(component instanceof GuiPanel) {
				((GuiPanel) component).flushComponentsQueue();
			}
		}
		
		Collections.sort(getChildComponents());
	}

	@Override
	public void resize(int screenWidth, int screenHeight) {
		super.resize(screenWidth, screenHeight);

		for(GuiComponent component : this.getReversedChildComponents()) {
			component.resize(screenWidth, screenHeight);
		}
	}

	public void flushRemovedComponents()
	{
		Iterator<GuiComponent> toRemoveComponentsIterator = toRemoveComponents.iterator();
		
		while (toRemoveComponentsIterator.hasNext())
		{
			GuiComponent component = toRemoveComponentsIterator.next();
			getChildComponents().remove(component);
			
			toRemoveComponentsIterator.remove();
			
			if(component instanceof GuiPanel) {
				((GuiPanel) component).flushRemovedComponents();
			}
			
			if(this instanceof GuiScrollPane) {
				((GuiScrollPane) this).updateSlidersVisibility();
			}
		}
	}
	
	@Override
	public void drawForeground(int mouseX, int mouseY, float partialTicks)
	{
		for (GuiComponent component : getChildComponents()) {
			component.render(mouseX, mouseY, partialTicks);
		}
		
		super.drawForeground(mouseX, mouseY, partialTicks);
	}
	
	public List<GuiComponent> getChildComponents() {
		return childComponents;
	}
	
	public List<GuiComponent> getReversedChildComponents() {
		List<GuiComponent> components = new ArrayList<>();
		if(getChildComponents() != null) {
			components.addAll(getChildComponents());
			Collections.reverse(components);
		}
		return components;
	}
}
