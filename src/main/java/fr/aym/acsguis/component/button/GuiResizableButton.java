package fr.aym.acsguis.component.button;

import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.event.listeners.IResizableButtonListener;
import fr.aym.acsguis.event.listeners.mouse.IMouseMoveListener;
import fr.aym.acsguis.utils.ComponentRenderContext;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GuiResizableButton extends GuiButton implements IMouseMoveListener {
	
	private final List<IResizableButtonListener> resizableButtonListeners = new ArrayList<IResizableButtonListener>();
	
	public enum ENUM_RESIZE_SIDE { LEFT, RIGHT, TOP, BOTTOM }
	
	protected boolean leftInnerResizable = true;
	protected boolean leftOutsideResizable = true;
	protected boolean rightInnerResizable = true;
	protected boolean rightOutsideResizable = true;
	protected boolean topInnerResizable = true;
	protected boolean topOutsideResizable = true;
	protected boolean bottomInnerResizable = true;
	protected boolean bottomOutsideResizable = true;
	
	protected int resizeBorderSize = 1;
	
	protected boolean leftHovered;
	protected boolean rightHovered;
	protected boolean topHovered;
	protected boolean bottomHovered;
	
	protected int resizeBorderColor = Color.LIGHT_GRAY.getRGB();
	
	protected float lastWidth, lastHeight;
	protected float lastX, lastY;
	protected int minWidth = 4, minHeight = 4, maxWidth = Integer.MAX_VALUE, maxHeight = Integer.MAX_VALUE;

	public GuiResizableButton(String text) {
		super(text);
		addMoveListener(this);
	}
	
	public GuiResizableButton addResizableButtonListener(IResizableButtonListener resizableButtonListener) {
		resizableButtonListeners.add(resizableButtonListener);
		return this;
	}
	
	@Override
	public void drawForeground(int mouseX, int mouseY, float partialTicks, ComponentRenderContext enableScissor) {
		super.drawForeground(mouseX, mouseY, partialTicks, enableScissor);
		
		if(isLeftHovered()) {
			drawRect(getScreenX(), getScreenY(), getScreenX() + getResizeBorderSize(), getScreenY() + getHeight(), getResizeBorderColor());
		} else if(isRightHovered()) {
			drawRect(getScreenX() + getWidth() - getResizeBorderSize(), getScreenY(), getScreenX() + getWidth(), getScreenY() + getHeight(), getResizeBorderColor());
		} else if(isTopHovered()) {
			drawRect(getScreenX(), getScreenY(), getScreenX() + getWidth(), getScreenY() + getResizeBorderSize(), getResizeBorderColor());
		} else if(isBottomHovered()) {
			drawRect(getScreenX(), getScreenY() + getHeight() - getResizeBorderSize(), getScreenX() + getWidth(), getScreenY() + getHeight(), getResizeBorderColor());
		}
	}
	
	@Override
	public void onMouseClicked(int mouseX, int mouseY, int mouseButton)
	{
		super.onMouseClicked(mouseX, mouseY, mouseButton);
		lastWidth = getWidth();
		lastHeight = getHeight();
		lastX = getX();
		lastY = getY();
	}
	
	@Override
	public void onMouseMoved(int mouseX, int mouseY)
	{
		boolean leftHovered = mouseX >= getScreenX() && mouseX <= getScreenX() + getResizeBorderSize();
		boolean rightHovered = mouseX >= getScreenX() + getWidth() - getResizeBorderSize() && mouseX <= getScreenX() + getWidth();
		boolean topHovered = mouseY >= getScreenY() && mouseY <= getScreenY() + getResizeBorderSize();
		boolean bottomHovered = mouseY >= getScreenY() + getHeight() - getResizeBorderSize() && mouseY <= getScreenY() + getHeight();
		
		if(!isPressed()) {
			setLeftHovered(isHovered() && (isLeftOutsideResizable() || isLeftInnerResizable()) && leftHovered);
			setRightHovered(isHovered() && (isRightOutsideResizable() || isRightInnerResizable()) && rightHovered);
			setTopHovered(isHovered() && (isTopOutsideResizable() || isTopInnerResizable()) && topHovered);
			setBottomHovered(isHovered() && (isBottomOutsideResizable() || isBottomInnerResizable()) && bottomHovered);
		} else {
			
			float newWidth = MathHelper.clamp(getLastWidth() + (mouseX - GuiFrame.lastPressedX) * (isRightHovered() ? 1 : -1), getMinWidth(), getMaxWidth());
			float newHeight = MathHelper.clamp(getLastHeight() + (mouseY - GuiFrame.lastPressedY) * (isBottomHovered() ? 1 : -1), getMinHeight(), getMaxHeight());
			
			float wDelta = getWidth() - newWidth;
			float hDelta = getHeight() - newHeight;
			
			boolean leftFlag = isLeftHovered() && ((isLeftInnerResizable() && wDelta > 0) || (isLeftOutsideResizable() && wDelta < 0));
			boolean rightFlag = isRightHovered() && ((isRightInnerResizable() && wDelta > 0) || (isRightOutsideResizable() && wDelta < 0));
			boolean topFlag = isTopHovered() && ((isTopInnerResizable() && hDelta > 0) || (isTopOutsideResizable() && hDelta < 0));
			boolean bottomFlag = isBottomHovered() && ((isBottomInnerResizable() && hDelta > 0) || (isBottomOutsideResizable() && hDelta < 0));
			
			if(leftFlag || rightFlag) {
				style.getWidth().setAbsolute(newWidth);
				
				if(leftFlag)
					style.getXPos().setAbsolute(getLastX() + (getLastWidth() - getWidth()));
			}
			
			if(topFlag || bottomFlag) {
				style.getHeight().setAbsolute(newHeight);
				
				if(topFlag)
					style.getYPos().setAbsolute(getLastY() + (getLastHeight() - getHeight()));
			}
			
			for(IResizableButtonListener resizableButtonListener : resizableButtonListeners) {
				if(isLeftHovered())
					resizableButtonListener.onButtonUpdated(ENUM_RESIZE_SIDE.LEFT);
				else if(isRightHovered())
					resizableButtonListener.onButtonUpdated(ENUM_RESIZE_SIDE.RIGHT);
				else if(isTopHovered())
					resizableButtonListener.onButtonUpdated(ENUM_RESIZE_SIDE.TOP);
				else if(isBottomHovered())
					resizableButtonListener.onButtonUpdated(ENUM_RESIZE_SIDE.BOTTOM);
			}
		}
	}
	
	public float getLastWidth() {
		return lastWidth;
	}
	
	public float getLastHeight() {
		return lastHeight;
	}
	
	public float getLastX() {
		return lastX;
	}
	
	public float getLastY() {
		return lastY;
	}
	
	@Override public void onMouseHover(int mouseX, int mouseY) {}
	
	@Override public void onMouseUnhover(int mouseX, int mouseY) {}
	
	public boolean isLeftInnerResizable() {
		return leftInnerResizable;
	}
	
	public GuiResizableButton setLeftInnerResizable(boolean leftInnerResizable) {
		this.leftInnerResizable = leftInnerResizable;
		return this;
	}
	
	public boolean isLeftOutsideResizable() {
		return leftOutsideResizable;
	}
	
	public GuiResizableButton setLeftOutsideResizable(boolean leftOutsideResizable) {
		this.leftOutsideResizable = leftOutsideResizable;
		return this;
	}
	
	public boolean isRightInnerResizable() {
		return rightInnerResizable;
	}
	
	public GuiResizableButton setRightInnerResizable(boolean rightInnerResizable) {
		this.rightInnerResizable = rightInnerResizable;
		return this;
	}
	
	public boolean isRightOutsideResizable() {
		return rightOutsideResizable;
	}
	
	public GuiResizableButton setRightOutsideResizable(boolean rightOutsideResizable) {
		this.rightOutsideResizable = rightOutsideResizable;
		return this;
	}
	
	public boolean isTopInnerResizable() {
		return topInnerResizable;
	}
	
	public GuiResizableButton setTopInnerResizable(boolean topInnerResizable) {
		this.topInnerResizable = topInnerResizable;
		return this;
	}
	
	public boolean isTopOutsideResizable() {
		return topOutsideResizable;
	}
	
	public GuiResizableButton setTopOutsideResizable(boolean topOutsideResizable) {
		this.topOutsideResizable = topOutsideResizable;
		return this;
	}
	
	public boolean isBottomInnerResizable() {
		return bottomInnerResizable;
	}
	
	public GuiResizableButton setBottomInnerResizable(boolean bottomInnerResizable) {
		this.bottomInnerResizable = bottomInnerResizable;
		return this;
	}
	
	public boolean isBottomOutsideResizable() {
		return bottomOutsideResizable;
	}
	
	public GuiResizableButton setBottomOutsideResizable(boolean bottomOutsideResizable) {
		this.bottomOutsideResizable = bottomOutsideResizable;
		return this;
	}
	
	public int getResizeBorderSize() {
		return resizeBorderSize;
	}
	
	public GuiResizableButton setResizeBorderSize(int resizeBorderSize) {
		this.resizeBorderSize = resizeBorderSize;
		return this;
	}
	
	public boolean isLeftHovered() {
		return leftHovered;
	}
	
	public GuiResizableButton setLeftHovered(boolean leftHovered) {
		this.leftHovered = leftHovered;
		return this;
	}
	
	public boolean isRightHovered() {
		return rightHovered;
	}
	
	public GuiResizableButton setRightHovered(boolean rightHovered) {
		this.rightHovered = rightHovered;
		return this;
	}
	
	public boolean isTopHovered() {
		return topHovered;
	}
	
	public GuiResizableButton setTopHovered(boolean topHovered) {
		this.topHovered = topHovered;
		return this;
	}
	
	public boolean isBottomHovered() {
		return bottomHovered;
	}
	
	public GuiResizableButton setBottomHovered(boolean bottomHovered) {
		this.bottomHovered = bottomHovered;
		return this;
	}
	
	public int getResizeBorderColor() {
		return resizeBorderColor;
	}
	
	public GuiResizableButton setResizeBorderColor(int resizeBorderColor) {
		this.resizeBorderColor = resizeBorderColor;
		return this;
	}
	
	public int getMinWidth() {
		return minWidth;
	}
	
	public GuiResizableButton setMinWidth(int minWidth) {
		this.minWidth = minWidth;
		return this;
	}
	
	public int getMinHeight() {
		return minHeight;
	}
	
	public GuiResizableButton setMinHeight(int minHeight) {
		this.minHeight = minHeight;
		return this;
	}
	
	public int getMaxWidth() {
		return maxWidth;
	}
	
	public GuiResizableButton setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
		return this;
	}
	
	public int getMaxHeight() {
		return maxHeight;
	}
	
	public GuiResizableButton setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
		return this;
	}

	//FIXME MOVE THIS
    public static void drawRect(float left, float top, float right, float bottom, int color)
    {
        if (left < right)
        {
            float i = left;
            left = right;
            right = i;
        }

        if (top < bottom)
        {
            float j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(f, f1, f2, f3);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos((double)left, (double)bottom, 0.0D).endVertex();
        bufferbuilder.pos((double)right, (double)bottom, 0.0D).endVertex();
        bufferbuilder.pos((double)right, (double)top, 0.0D).endVertex();
        bufferbuilder.pos((double)left, (double)top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
}
