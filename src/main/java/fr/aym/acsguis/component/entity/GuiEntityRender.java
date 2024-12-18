package fr.aym.acsguis.component.entity;

import fr.aym.acsguis.component.EnumComponentType;
import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.style.ComponentStyle;
import fr.aym.acsguis.event.listeners.mouse.IMouseExtraClickListener;
import fr.aym.acsguis.utils.ComponentRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;

public class GuiEntityRender extends GuiComponent implements IMouseExtraClickListener
{
	protected EntityLivingBase entity;
	protected int paddingTop;
	protected int paddingBottom;
	protected float counter;
	private boolean kept;

    @Override
    public EnumComponentType getType() {
        return EnumComponentType.ENTITY_RENDER;
    }

    public GuiEntityRender(EntityLivingBase entity) {
		setEntity(entity);
		int padding = (int) (0.125 * getHeight());
		setPaddingTop(padding);
		setPaddingBottom(padding);
		addExtraClickListener(this);
	}
	
    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTicks, ComponentRenderContext enableScissor)
    {
        if(entity != null) {
            float scale = (getHeight() - (paddingBottom + paddingTop)) / entity.height;
            float x = getScreenX() + getWidth() / 2;
            float y = getScreenY() + getHeight() - paddingBottom;
            float mX = kept ? getScreenX() + getWidth() / 2 - mouseX : counter%mc.currentScreen.width;
            float mY = kept ? (y - mouseY - entity.getEyeHeight() * scale) : 0;
            drawEntityOnScreen(x, y, scale, mX, mY, entity);
        }

        super.drawForeground(mouseX, mouseY, partialTicks, enableScissor);
    }

    public static void drawEntityOnScreen(float posX, float posY, float scale, float mouseX, float mouseY, EntityLivingBase ent)
    {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)posX, (float)posY, 380+50.0F);
        GlStateManager.scale(-scale, scale, scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-((float)Math.atan(mouseY / 40.0F)) * 20.0F, 1.0F, 0.0F, 0.0F);
        ent.renderYawOffset = mouseX*360f/mc.currentScreen.width;//(float)Math.atan((double)(mouseX)/ 40f) * 20.0F;
        ent.rotationYaw = mouseX*360f/mc.currentScreen.width;
        ent.rotationPitch = -((float)Math.atan(mouseY / 40.0F)) * 20.0F;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        rendermanager.setRenderShadow(true);
        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    @Override
    public void tick() {
        super.tick();
        counter+=1;
    }

    public GuiEntityRender setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
        return this;
    }

    public GuiEntityRender setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
        return this;
    }

    public int getPaddingTop() {
        return paddingTop;
    }

    public int getPaddingBottom() {
        return paddingBottom;
    }

    public GuiEntityRender setEntity(EntityLivingBase entity) {
        this.entity = entity;
        return this;
    }

    public EntityLivingBase getEntity() {
        return entity;
    }

    @Override
    public void onMouseDoubleClicked(int mouseX, int mouseY, int mouseButton) {

    }

    @Override
    public void onMousePressed(int mouseX, int mouseY, int mouseButton) {
        kept = true;
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {
        if(kept)
            counter = getScreenX() + getWidth() / 2 - mouseX;
        kept = false;
    }
}
