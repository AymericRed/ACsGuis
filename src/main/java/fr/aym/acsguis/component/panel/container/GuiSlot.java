package fr.aym.acsguis.component.panel.container;

import fr.aym.acsguis.api.GuiAPIClientHelper;
import fr.aym.acsguis.component.EnumComponentType;
import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.utils.ComponentRenderContext;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.awt.*;

public class GuiSlot extends GuiComponent {
    protected static final int DEFAULT_SLOT_WIDTH = 18;
    protected static final int DEFAULT_SLOT_HEIGHT = 18;

    protected final Slot slot;

    public GuiSlot(Slot slot) {
        this.slot = slot;
        getStyleCustomizer().setBackgroundColor(new Color(0, 0, 0, 0).getRGB())
                .setSize(DEFAULT_SLOT_WIDTH, DEFAULT_SLOT_HEIGHT);
    }

    @Override
    public EnumComponentType getType() {
        return EnumComponentType.SLOT;
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTicks, ComponentRenderContext enableScissor) {

        super.drawForeground(mouseX, mouseY, partialTicks, enableScissor);

        GuiContainer container = ((GuiContainer) getParent());

        ItemStack itemstack = slot.getStack();
        boolean flag = false;
        boolean flag1 = slot == container.clickedSlot && container.draggedStack != null && !container.isRightMouseClick;
        ItemStack itemstack1 = mc.player.inventory.getItemStack();

        if (slot == container.clickedSlot && container.draggedStack != null && container.isRightMouseClick && itemstack != null) {
            itemstack = itemstack.copy();
            itemstack.setCount(itemstack.getCount() / 2);
        } else if (container.dragSplitting && container.dragSplittingSlots.contains(slot) && itemstack1 != null) {
            if (container.dragSplittingSlots.size() == 1) {
                return;
            }

            if (net.minecraft.inventory.Container.canAddItemToSlot(slot, itemstack1, true) && container.inventorySlots.canDragIntoSlot(slot)) {
                itemstack = itemstack1.copy();
                flag = true;
                net.minecraft.inventory.Container.computeStackSize(container.dragSplittingSlots, container.dragSplittingLimit, itemstack, slot.getStack() == null ? 0 : slot.getStack().getCount());

                if (itemstack.getCount() > itemstack.getMaxStackSize()) {
                    itemstack.setCount(itemstack.getMaxStackSize());
                }

                if (itemstack.getCount() > slot.getSlotStackLimit()) {
                    itemstack.setCount(slot.getSlotStackLimit());
                }
            } else {
                container.dragSplittingSlots.remove(slot);
                container.updateDragSplitting();
            }
        }


        if (itemstack == null) {
            TextureAtlasSprite textureatlassprite = slot.getBackgroundSprite();

            if (textureatlassprite != null) { //MODIFIED FOR 1.12.2
                GlStateManager.disableLighting();
                this.mc.getTextureManager().bindTexture(slot.getBackgroundLocation());
                this.drawTexturedModalRect(getScreenX(), getScreenY(), textureatlassprite, 16, 16);
                GlStateManager.enableLighting();
                flag1 = true;
            }
        }

        if (!flag1) {
            if (flag) {
                GuiAPIClientHelper.drawRect(getScreenX(), getScreenY(), getScreenX() + 16, getScreenY() + 16, -2130706433);
            }

            if (itemstack != null)
                GuiAPIClientHelper.drawItemStack(itemstack, (int) getScreenX(), (int) getScreenY());
        }

        if (isHovered() && slot.isEnabled()) { //MODIFIED FOR 1.12.2
            GlStateManager.disableDepth();
            GuiAPIClientHelper.drawRect(getScreenX(), getScreenY(), getScreenX() + 16, getScreenY() + 16, -2130706433);
            GlStateManager.enableDepth();
        }
    }

    public Slot getSlot() {
        return slot;
    }

    // FIXME MOVE
    public void drawTexturedModalRect(float xCoord, float yCoord, TextureAtlasSprite textureSprite, int widthIn, int heightIn) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos((double) (xCoord + 0), (double) (yCoord + heightIn), (double) this.zLevel).tex((double) textureSprite.getMinU(), (double) textureSprite.getMaxV()).endVertex();
        bufferbuilder.pos((double) (xCoord + widthIn), (double) (yCoord + heightIn), (double) this.zLevel).tex((double) textureSprite.getMaxU(), (double) textureSprite.getMaxV()).endVertex();
        bufferbuilder.pos((double) (xCoord + widthIn), (double) (yCoord + 0), (double) this.zLevel).tex((double) textureSprite.getMaxU(), (double) textureSprite.getMinV()).endVertex();
        bufferbuilder.pos((double) (xCoord + 0), (double) (yCoord + 0), (double) this.zLevel).tex((double) textureSprite.getMinU(), (double) textureSprite.getMinV()).endVertex();
        tessellator.draw();
    }
}
