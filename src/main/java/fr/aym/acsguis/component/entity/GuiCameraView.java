package fr.aym.acsguis.component.entity;

import fr.aym.acsguis.component.EnumComponentType;
import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.utils.ComponentRenderContext;

public class GuiCameraView extends GuiComponent {
	
	public GuiCameraView(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	@Override
	public EnumComponentType getType() {
		return EnumComponentType.CAMERA_VIEW;
	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float partialTicks, ComponentRenderContext enableScissor)
	{
		super.drawBackground(mouseX, mouseY, partialTicks, enableScissor);
		//GL11.glBindTexture(GL11.GL_TEXTURE_2D, GuiFrame.worldRenderBuffer.framebufferTexture);
		//Gui.drawModalRectWithCustomSizedTexture(getScreenX(), getScreenY(), 0, 0, getWidth(), getHeight(), getWidth(), getHeight());
		mc.fontRenderer.drawString("NOT FIXED", 2, 2, 0xFF0000);
	}
}
