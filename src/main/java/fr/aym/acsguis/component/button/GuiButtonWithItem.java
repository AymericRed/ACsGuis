package fr.aym.acsguis.component.button;

import fr.aym.acsguis.component.EnumComponentType;
import fr.aym.acsguis.utils.ComponentRenderContext;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class GuiButtonWithItem extends GuiButton
{
    private ItemStack icon;

    private float itemSize = 32;

    public GuiButtonWithItem(ItemStack icon)
    {
        super("");
        this.icon = icon;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public GuiButtonWithItem setIcon(ItemStack icon) {
        this.icon = icon;
        return this;
    }

    public float getItemSize() {
        return itemSize;
    }

    public GuiButtonWithItem setItemSize(float itemSize) {
        this.itemSize = itemSize;
        return this;
    }

    @Override
    public EnumComponentType getType() {
        return EnumComponentType.BUTTON_WITH_ICON;
    }

    @Override
    public void drawBackground(int mouseX, int mouseY, float partialTicks, ComponentRenderContext enableScissor)
    {
        super.drawBackground(mouseX, mouseY, partialTicks, enableScissor);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glTranslated(getWidth()/2f - itemSize/2, getHeight()/2f - itemSize/2, 0);
        FontRenderer fontrenderer = mc.fontRenderer;
        float factorX = itemSize /16;
        float factorY = itemSize /16;
        if(factorX != 1 || factorY != 1)
    		GL11.glScalef(factorX, factorY, 0);
        int x = (int) (getScreenX()/factorX);
        int y = (int) (getScreenY()/factorY);
        mc.getRenderItem().renderItemAndEffectIntoGUI(icon, x, y);
        mc.getRenderItem().renderItemOverlayIntoGUI(fontrenderer, icon, x, y, null);
        if(factorX != 1 || factorY != 1)
    		GL11.glScalef(1/factorX, 1/factorY, 0);
        GL11.glTranslated(-getWidth()/2f + itemSize/2, -getHeight()/2f + itemSize/2, 0);
    	RenderHelper.disableStandardItemLighting();
    }
}
