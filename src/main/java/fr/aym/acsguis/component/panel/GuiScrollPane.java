package fr.aym.acsguis.component.panel;

import fr.aym.acsguis.component.EnumComponentType;
import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.button.GuiSlider;
import fr.aym.acsguis.component.textarea.IChildSizeUpdateListener;
import fr.aym.acsguis.event.listeners.IResizeListener;
import fr.aym.acsguis.event.listeners.mouse.IMouseWheelListener;
import fr.aym.acsguis.utils.ComponentRenderContext;

public class GuiScrollPane extends GuiPanel implements IMouseWheelListener, IResizeListener, IChildSizeUpdateListener
{
	protected int lastScrollAmountX;
	protected int lastScrollAmountY;
	protected final GuiSlider xSlider;
	protected final GuiSlider ySlider;

    private boolean autoScroll;

	public GuiScrollPane() {
		this(0, 0, 0, 0);
	}
    public GuiScrollPane(int x, int y, int width, int height) {
    	super(x, y, width, height);
    	
		xSlider = new GuiSlider(true);
		ySlider = new GuiSlider(false);
	    
		add(xSlider);
		add(ySlider);
		
		addWheelListener(this);
		addResizeListener(this);
	}

	@Override
	public EnumComponentType getType() {
		return EnumComponentType.SCROLL_PANE;
	}

    public GuiScrollPane setAutoScroll(boolean autoScroll)
    {
		this.autoScroll = autoScroll;
		return this;
	}
    public boolean hasAutoScroll()
    {
		return autoScroll;
	}
    
	@Override
	public void onMouseWheel(int dWheel) {
		if(isHovered()) {
			GuiSlider slider = xSlider.isHovered() ? xSlider : ySlider;
			slider.setValue(slider.getValue() + (dWheel / -120.0D * slider.getWheelStep()) * (slider.getMax() - slider.getMin()));
		}
	}
	
	@Override
	public void onResize(int width, int height) {
		updateSlidersVisibility();
	}
	
    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTicks, ComponentRenderContext enableScissor)
    {
        if(-xSlider.getValue() != lastScrollAmountX || -ySlider.getValue() != lastScrollAmountY) {

            for(GuiComponent component : getChildComponents()) {
                if(component == xSlider || component == ySlider) continue;
                component.getStyle().setOffsetX((int) -xSlider.getValue());
                component.getStyle().setOffsetY((int) -ySlider.getValue());
            }

            lastScrollAmountX = (int) -xSlider.getValue();
            lastScrollAmountY = (int) -ySlider.getValue();
        }

        super.drawForeground(mouseX, mouseY, partialTicks, enableScissor);
    }

    /**
     * Hide the sliders if the maximum effective size is inferior to the rendered size
     * {@link #getMaxWidth()}, {@link #getMaxHeight()}
     */
	public void updateSlidersVisibility() {
        xSlider.setMax(getMaxWidth() - getWidth());
        ySlider.setMax(getMaxHeight() - getHeight());
        xSlider.setVisible(getMaxWidth() - getWidth() > 0);
        ySlider.setVisible(getMaxHeight() - getHeight() > 0);
	}

    /**
     * @return Return the maximum effective width by by summing the child components' width
     */
    protected float getMaxWidth()
    {
        float maxWidth = getWidth();
        for(GuiComponent<?> component : getChildComponents())
        {
        	if(!(component instanceof GuiSlider)) {
				float width = component.getX() + component.getWidth();
				if (width > maxWidth) {
					maxWidth = width;
				}
			}
        }
        return maxWidth;
    }

    /**
     * @return Return the maximum effective height by by summing the child components' height
     */
    protected float getMaxHeight()
    {
        float maxHeight = getHeight();
        for(GuiComponent<?> component : getChildComponents())
        {
			if(!(component instanceof GuiSlider)) {
				float height = component.getY() + component.getHeight();
				//System.out.println("Cp "+component+" h" +height);
				if (height > maxHeight) {
					maxHeight = height;
				}
			}
        }

        return maxHeight;
    }

	@Override
	public void flushComponentsQueue()
	{
		int oldSize = getChildComponents().size();
		super.flushComponentsQueue();
		if(autoScroll && oldSize != getChildComponents().size())
		{
			xSlider.setValue(xSlider.getMax());
			ySlider.setValue(ySlider.getMax());
		}
	}
	public void scrollXBy(double d)
	{
		xSlider.setValue(xSlider.getValue()+d);
	}
	public void scrollYBy(double amount)
	{
		ySlider.setValue(ySlider.getValue()+amount);
	}
	public GuiSlider getxSlider() {
		return xSlider;
	}
	public GuiSlider getySlider() {
		return ySlider;
	}

	@Override
	public void onComponentChildSizeUpdate() {
		updateSlidersVisibility();
	}
}
