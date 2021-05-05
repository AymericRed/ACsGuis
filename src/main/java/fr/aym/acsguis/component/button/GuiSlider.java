package fr.aym.acsguis.component.button;

import fr.aym.acsguis.component.EnumComponentType;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.style.PanelStyleManager;
import fr.aym.acsguis.cssengine.selectors.EnumSelectorContext;
import fr.aym.acsguis.component.style.AutoStyleHandler;
import fr.aym.acsguis.component.style.ComponentStyleManager;
import fr.aym.acsguis.cssengine.style.CssComponentStyleManager;
import fr.aym.acsguis.cssengine.style.CssPanelStyleManager;
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
		add(sliderButton = new GuiSliderButton());
		sliderButton.getStyle().getWidth().setRelative(1F / (horizontal ? 5 : 1));
		sliderButton.getStyle().getHeight().setRelative(1F / (horizontal ? 1 : 5));
		/*sliderButton.getStyle().addAutoStyleHandler(new AutoStyleHandler<ComponentStyleManager>() {
            @Override
            public boolean handleProperty(EnumCssStyleProperties property, EnumSelectorContext context, ComponentStyleManager target) {
                if(property == EnumCssStyleProperties.WIDTH)
                {
                    target.setWidth((int) ((1F / (horizontal ? 1 : 5))*getWidth()));
                    return true;
                }
                if(property == EnumCssStyleProperties.HEIGHT)
                {
                    target.setHeight((int) ((1F / (horizontal ? 5 : 1))*getHeight()));
                    return true;
                }
                return false;
            }

            @Override
            public List<EnumCssStyleProperties> getModifiedProperties(ComponentStyleManager target) {
                return Arrays.asList(EnumCssStyleProperties.WIDTH, EnumCssStyleProperties.HEIGHT);
            }
        });*/
		style.setBackgroundColor(new Color(0, 0, 0, 0.5F).getRGB());
		
		addClickListener(this);
		addExtraClickListener(this);
	}

    @Override
    protected PanelStyleManager createStyleManager() {
        return new CssPanelStyleManager(GuiSlider.this) {
            @Override
            public ComponentStyleManager addAutoStyleHandler(AutoStyleHandler handler) {
                if(handler.getPriority(this) == Priority.COMPONENT) //Ignore layouts or parent's auto style
                    return super.addAutoStyleHandler(handler);
                return this;
            }
        };
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
            sliderButton.getStyle().getXPos().setAbsolute((int) (getRelativeValue() * (getWidth() - sliderButton.getWidth())));
            sliderButton.getStyle().getYPos().setAbsolute(0);
        } else {
            sliderButton.getStyle().getXPos().setAbsolute(0);
            sliderButton.getStyle().getYPos().setAbsolute((int) (getRelativeValue() * (getHeight() - sliderButton.getHeight())));
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
        protected GuiSliderButton() {
            super();
            addMoveListener(this);
            getStyle().addAutoStyleHandler(new AutoStyleHandler<ComponentStyleManager>() {
                @Override
                public boolean handleProperty(EnumCssStyleProperties property, EnumSelectorContext context, ComponentStyleManager target) {
                    if(property == EnumCssStyleProperties.LEFT && horizontal)
                    {
                        target.getXPos().setAbsolute((int) (getRelativeValue() * (GuiSlider.this.getWidth() - sliderButton.getWidth())));
                        return true;
                    }
                    if(property == EnumCssStyleProperties.TOP && !horizontal)
                    {
                        target.getYPos().setAbsolute((int) (getRelativeValue() * (GuiSlider.this.getHeight() - sliderButton.getHeight())));
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
	public interface ISliderListener
	{
		void onSliderChanged(double value);
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
