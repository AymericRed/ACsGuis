package fr.aym.acsguis.component;

import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.cssengine.selectors.EnumSelectorContext;
import fr.aym.acsguis.cssengine.style.AutoStyleHandler;
import fr.aym.acsguis.cssengine.style.ComponentStyleManager;
import fr.aym.acsguis.cssengine.style.CssComponentStyleManager;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperties;
import fr.aym.acsguis.event.listeners.mouse.IMouseClickListener;
import fr.aym.acsguis.event.listeners.mouse.IMouseExtraClickListener;
import fr.aym.acsguis.event.listeners.mouse.IMouseMoveListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuiSlider extends GuiPanel implements IMouseClickListener, IMouseExtraClickListener 
{
    protected final List<ISliderListener> sliderListeners = new ArrayList<ISliderListener>();
	protected final GuiSliderButton sliderButton;
	protected final boolean horizontal;
	
	protected double min = 0, max = 1, value = 0;
	protected float step = 0.1F, wheelStep = 0.1F;
	
	public GuiSlider(boolean horizontal) {
		super();
		this.horizontal = horizontal;
		add(sliderButton = new GuiSliderButton(0, 0, 0, 0));
		sliderButton.getStyle().setRelativeWidth(1F / (horizontal ? 5 : 1));
		sliderButton.getStyle().setRelativeHeight(1F / (horizontal ? 1 : 5));
		style.setBackgroundColor(new Color(0, 0, 0, 0.5F).getRGB());
		
		addClickListener(this);
		addExtraClickListener(this);
	}

    @Override
    protected ComponentStyleManager createStyleManager() {
        ComponentStyleManager s = new CssComponentStyleManager(this) {
            @Override
            public ComponentStyleManager addAutoStyleHandler(AutoStyleHandler handler) {
                if(handler.getPriority(this) == Priority.COMPONENT) //Ignore layouts or parent's auto style
                    return super.addAutoStyleHandler(handler);
                return this;
            }
        };
        return s;
    }

    @Override
    public EnumComponentType getType() {
        return horizontal ? EnumComponentType.SLIDER_HORIZONTAL : EnumComponentType.SLIDER_VERTICAL;
    }

    public GuiSlider setValue(double value)
    {
        this.value = Math.round(value / step) * step;
        this.value = MathHelper.clamp(value, min, max);

        if(horizontal) {
            sliderButton.getStyle().setX((int) (getRelativeValue() * (getWidth() - sliderButton.getWidth())));
            sliderButton.getStyle().setY(0);
        } else {
            sliderButton.getStyle().setX(0);
            sliderButton.getStyle().setY((int) (getRelativeValue() * (getHeight() - sliderButton.getHeight())));
        }
        for(ISliderListener lis : sliderListeners)
        {
        	lis.onSliderChanged(value);
        }
        return this;
    }

    protected void changeValue(int mouseX, int mouseY)
    {
        double relValue;

        if (horizontal) {
            relValue = (mouseX - getScreenX() - sliderButton.getWidth() / 2) / (double) (getWidth() - sliderButton.getWidth());
        } else {
            relValue = (mouseY - getScreenY() - sliderButton.getHeight() / 2) / (double) (getHeight() - sliderButton.getHeight());
        }

        setValue(min + MathHelper.clamp(relValue, 0, 1) * (max - min));
    }
	
	@Override
    public void onMouseDoubleClicked(int mouseX, int mouseY, int mouseButton) {
        //System.out.println(isHovered()+" "+sliderButton.getWidth()+" "+sliderButton.getHeight());
		if(isHovered()) {
			changeValue(mouseX, mouseY);
			Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		}
		
    }
	
	@Override public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {}
    
	@Override public void onMousePressed(int mouseX, int mouseY, int mouseButton) {}
	
	@Override public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {}

    private static final List<EnumCssStyleProperties> affectedSliderProperties = Arrays.asList(EnumCssStyleProperties.LEFT, EnumCssStyleProperties.TOP);

	protected class GuiSliderButton extends GuiButton implements IMouseMoveListener
    {
        protected GuiSliderButton(int x, int y, int width, int height) {
            super(x, y, width, height);
            addMoveListener(this);
            getStyle().addAutoStyleHandler(new AutoStyleHandler<ComponentStyleManager>() {
                @Override
                public boolean handleProperty(EnumCssStyleProperties property, EnumSelectorContext context, ComponentStyleManager target) {
                    if(property == EnumCssStyleProperties.LEFT && horizontal)
                    {
                        target.setX((int) (getRelativeValue() * (GuiSlider.this.getWidth() - sliderButton.getWidth())));
                        return true;
                    }
                    if(property == EnumCssStyleProperties.TOP && !horizontal)
                    {
                        target.setY((int) (getRelativeValue() * (GuiSlider.this.getHeight() - sliderButton.getHeight())));
                        return true;
                    }
                    return false;
                }

                @Override
                public List<EnumCssStyleProperties> getModifiedProperties(ComponentStyleManager target) {
                    return affectedSliderProperties;
                }
            });
        }
		
        @Override
        public void onMouseMoved(int mouseX, int mouseY)
        {
	        if(isPressed()) {
		        changeValue(mouseX, mouseY);
	        }
        }
	
	    @Override public void onMouseHover(int mouseX, int mouseY) {}
	
	    @Override public void onMouseUnhover(int mouseX, int mouseY) {}
    }
	public static interface ISliderListener
	{
		public void onSliderChanged(double value);
	}
	public GuiSlider addSliderListener(ISliderListener lis)
	{
		sliderListeners.add(lis);
		return this;
	}

    public double getValue() {
        return value;
    }

    public double getRelativeValue() {
        return (this.value - min) / (max - min);
    }

    public GuiSlider setMin(double min) {
        this.min = min;
        setValue(value);
        return this;
    }

    public GuiSlider setMax(double max) {
        this.max = max;
        setValue(value);
        return this;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public GuiSlider setStep(float step) {
        this.step = step;
        return this;
    }

    public GuiSlider setWheelStep(float wheelStep) {
        this.wheelStep = wheelStep;
        return this;
    }
    
    public float getWheelStep() {
        return wheelStep;
    }

}
