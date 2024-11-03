package fr.aym.acsguis.component.panel.container;

import com.google.common.collect.Sets;
import fr.aym.acsguis.api.GuiAPIClientHelper;
import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.event.listeners.IGuiCloseListener;
import fr.aym.acsguis.event.listeners.IKeyboardListener;
import fr.aym.acsguis.event.listeners.ITickListener;
import fr.aym.acsguis.event.listeners.mouse.IMouseClickListener;
import fr.aym.acsguis.event.listeners.mouse.IMouseExtraClickListener;
import fr.aym.acsguis.event.listeners.mouse.IMouseMoveListener;
import fr.aym.acsguis.component.layout.GuiScaler;
import fr.aym.acsguis.utils.ComponentRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static net.minecraft.client.gui.GuiScreen.isShiftKeyDown;

public abstract class GuiContainer extends GuiFrame implements IMouseClickListener, IMouseExtraClickListener, IMouseMoveListener, IKeyboardListener, IGuiCloseListener, ITickListener
{
    /*
     * Copy and rehabilitated class from vanilla Minecraft.
     */

    /** A list of the players inventory slots */
    public Container inventorySlots;
    /** holds the slot currently hovered */
    protected Slot theSlot;
    /** Used when touchscreen is enabled. */
    protected Slot clickedSlot;
    /** Used when touchscreen is enabled. */
    protected boolean isRightMouseClick;
    /** Used when touchscreen is enabled */
    protected ItemStack draggedStack;
    protected float touchUpX;
    protected float touchUpY;
    protected Slot returningStackDestSlot;
    protected long returningStackTime;
    /** Used when touchscreen is enabled */
    protected ItemStack returningStack;
    protected Slot currentDragTargetSlot;
    protected long dragItemDropDelay;
    protected final Set<Slot> dragSplittingSlots = Sets.<Slot>newHashSet();
    protected boolean dragSplitting;
    protected int dragSplittingLimit;
    protected int dragSplittingButton;
    protected boolean ignoreMouseUp = true;
    protected int dragSplittingRemnant;
    protected long lastClickTime;
    protected Slot lastClickSlot;
    protected int lastClickButton;
    protected boolean doubleClick;
    protected ItemStack shiftClickedSlot;
    
    protected final List<GuiSlot> slots = new ArrayList<GuiSlot>();
	
	public GuiContainer(int x, int y, int width, int height, GuiScaler scale) {
		super(x, y, width, height, scale);
		addClickListener(this);
		addExtraClickListener(this);
		addMoveListener(this);
		addKeyboardListener(this);
		addCloseListener(this);
		addTickListener(this);
	}

    /**
     * Set the GUI container and setup all the GuiSlot linked to it.
     * @param inventorySlotsIn The container to be linked to the GuiContainer.
     */
    public GuiContainer setContainer(Container inventorySlotsIn) {
        this.inventorySlots = inventorySlotsIn;
		
		for(GuiSlot slot : slots) {
			remove(slot);
		}
        
        slots.clear();

        for(Slot slot : ((List<Slot>)inventorySlots.inventorySlots)) {
            GuiSlot guiSlot = new GuiSlot(slot.xPos, slot.yPos, slot);
            slots.add(guiSlot);
            add(guiSlot);
        }
        mc.player.openContainer = inventorySlotsIn;
        return this;
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTicks, ComponentRenderContext enableScissor) {

        super.drawForeground(mouseX, mouseY, partialTicks, enableScissor);

        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.pushMatrix();
        GlStateManager.translate(getScreenX(), getScreenY(), 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        this.theSlot = null;
        int k = 240;
        int l = 240;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)k / 1.0F, (float)l / 1.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        InventoryPlayer inventoryplayer = mc.player.inventory;
        ItemStack itemstack = this.draggedStack == null ? inventoryplayer.getItemStack() : this.draggedStack;

        if (itemstack != null)
        {
            int j2 = 8;
            int k2 = this.draggedStack == null ? 8 : 16;

            if (this.draggedStack != null && this.isRightMouseClick)
            {
                itemstack = itemstack.copy();
                itemstack.setCount(MathHelper.ceil((float)itemstack.getCount() / 2.0F));
            }
            else if (this.dragSplitting && this.dragSplittingSlots.size() > 1)
            {
                itemstack = itemstack.copy();
                itemstack.setCount(this.dragSplittingRemnant);
            }

            mc.getRenderItem().zLevel = 200;
            GuiAPIClientHelper.drawItemStack(itemstack, (int) (mouseX - getScreenX() - j2), (int) (mouseY - getScreenY() - k2));
            mc.getRenderItem().zLevel = 0;
        }

        if (this.returningStack != null)
        {
            float f = (float)(Minecraft.getSystemTime() - this.returningStackTime) / 100.0F;

            if (f >= 1.0F)
            {
                f = 1.0F;
                this.returningStack = null;
            }

            float l2 = this.returningStackDestSlot.xPos - this.touchUpX;
            float i3 = this.returningStackDestSlot.yPos - this.touchUpY;
            float l1 = this.touchUpX + (int)((float)l2 * f);
            float i2 = this.touchUpY + (int)((float)i3 * f);

            if(itemstack != null)
                GuiAPIClientHelper.drawItemStack(this.returningStack, (int) l1, (int) i2, 1, false);
        }

        GlStateManager.popMatrix();

        if (inventoryplayer.getItemStack() == null && this.theSlot != null && this.theSlot.getHasStack())
        {
            ItemStack itemstack1 = this.theSlot.getStack();
            this.renderToolTip(itemstack1, mouseX, mouseY);
        }

        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
    }

    protected void renderToolTip(ItemStack stack, int x, int y)
    {
        List<String> list = stack.getTooltip(mc.player, mc.gameSettings.advancedItemTooltips?TooltipFlags.ADVANCED:TooltipFlags.NORMAL);

        for (int i = 0; i < list.size(); ++i)
        {
            if (i == 0)
            {
                list.set(i, stack.getRarity().color + list.get(i));
            }
            else
            {
                list.set(i, TextFormatting.GRAY + list.get(i));
            }
        }

        GuiAPIClientHelper.drawHoveringText(list, x, y);
    }

    protected void updateDragSplitting()
    {
        ItemStack itemstack = mc.player.inventory.getItemStack();

        if (itemstack != null && this.dragSplitting)
        {
            this.dragSplittingRemnant = itemstack.getCount();

            for (Slot slot : this.dragSplittingSlots)
            {
                ItemStack itemstack1 = itemstack.copy();
                int i = slot.getStack() == null ? 0 : slot.getStack().getCount();
                Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, itemstack1, i);

                if (itemstack1.getCount() > itemstack1.getMaxStackSize())
                {
                    itemstack1.setCount(itemstack1.getMaxStackSize());
                }

                if (itemstack1.getCount() > slot.getSlotStackLimit())
                {
                	itemstack1.setCount(slot.getSlotStackLimit());
                }

                this.dragSplittingRemnant -= itemstack1.getCount() - i;
            }
        }
    }

    /**
     * Returns the slot at the given coordinates or null if there is none.
     */
    protected Slot getSlotAtPosition(int x, int y)
    {
        for (int i = 0; i < this.inventorySlots.inventorySlots.size(); ++i)
        {
            Slot slot = (Slot) this.inventorySlots.inventorySlots.get(i);

            if (this.isMouseOverSlot(slot, x, y))
            {
                return slot;
            }
        }

        return null;
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if(inventorySlots != null) {

            boolean flag = mouseButton == mc.gameSettings.keyBindPickBlock.getKeyCode() + 100;
            Slot slot = this.getSlotAtPosition(mouseX, mouseY);
            long i = Minecraft.getSystemTime();
            this.doubleClick = this.lastClickSlot == slot && i - this.lastClickTime < 250L && this.lastClickButton == mouseButton;
            this.ignoreMouseUp = false;

            if (mouseButton == 0 || mouseButton == 1 || flag) {
                boolean flag1 = mouseX < getScreenX() || mouseY < getScreenY() || mouseX >= getScreenX() + getWidth() || mouseY >= getScreenY() + getHeight();
                if (slot != null)
                    flag1 = false; // Forge, prevent dropping of items through slots outside of GUI boundaries
                int l = -1;

                if (slot != null) {
                    l = slot.slotNumber;
                }

                if (flag1) {
                    l = -999;
                }

                if (mc.gameSettings.touchscreen && flag1 && mc.player.inventory.getItemStack() == null) {
                    mc.displayGuiScreen(null);
                    return;
                }

                if (l != -1) {
                    if (mc.gameSettings.touchscreen) {
                        if (slot != null && slot.getHasStack()) {
                            this.clickedSlot = slot;
                            this.draggedStack = null;
                            this.isRightMouseClick = mouseButton == 1;
                        } else {
                            this.clickedSlot = null;
                        }
                    } else if (!this.dragSplitting) {
                        if (mc.player.inventory.getItemStack() == null) {
                            if (mouseButton == mc.gameSettings.keyBindPickBlock.getKeyCode() + 100) {
                                this.handleMouseClick(slot, l, mouseButton, 3);
                            } else {
                                boolean flag2 = l != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
                                int i1 = 0;

                                if (flag2) {
                                    this.shiftClickedSlot = slot.getHasStack() ? slot.getStack() : null;
                                    i1 = 1;
                                } else if (l == -999) {
                                    i1 = 4;
                                }

                                this.handleMouseClick(slot, l, mouseButton, i1);
                            }

                            this.ignoreMouseUp = true;
                        } else {
                            this.dragSplitting = true;
                            this.dragSplittingButton = mouseButton;
                            this.dragSplittingSlots.clear();

                            if (mouseButton == 0) {
                                this.dragSplittingLimit = 0;
                            } else if (mouseButton == 1) {
                                this.dragSplittingLimit = 1;
                            } else if (mouseButton == mc.gameSettings.keyBindPickBlock.getKeyCode() + 100) {
                                this.dragSplittingLimit = 2;
                            }
                        }
                    }
                }
            }

            this.lastClickSlot = slot;
            this.lastClickTime = i;
            this.lastClickButton = mouseButton;

        }
    }

	/**
     * Called when a mouse button is pressed and the mouse is moved around. Parameters are : mouseX, mouseY
     */
    @Override
    public void onMouseMoved(int mouseX, int mouseY)
    {
        if(inventorySlots != null) {

            Slot slot = this.getSlotAtPosition(mouseX, mouseY);
            ItemStack itemstack = mc.player.inventory.getItemStack();

            if (this.clickedSlot != null && mc.gameSettings.touchscreen) {
                if (GuiFrame.mouseButton == 0 || GuiFrame.mouseButton == 1) {
                    if (this.draggedStack == null) {
                        if (slot != this.clickedSlot && this.clickedSlot.getStack() != null) {
                            this.draggedStack = this.clickedSlot.getStack().copy();
                        }
                    } else if (this.draggedStack.getCount() > 1 && slot != null && Container.canAddItemToSlot(slot, this.draggedStack, false)) {
                        long i = Minecraft.getSystemTime();

                        if (this.currentDragTargetSlot == slot) {
                            if (i - this.dragItemDropDelay > 500L) {
                                this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, 0);
                                this.handleMouseClick(slot, slot.slotNumber, 1, 0);
                                this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, 0);
                                this.dragItemDropDelay = i + 750L;
                                this.draggedStack.shrink(1);
                            }
                        } else {
                            this.currentDragTargetSlot = slot;
                            this.dragItemDropDelay = i;
                        }
                    }
                }
            } else if (this.dragSplitting && slot != null && itemstack != null && itemstack.getCount() > this.dragSplittingSlots.size() && Container.canAddItemToSlot(slot, itemstack, true) && slot.isItemValid(itemstack) && this.inventorySlots.canDragIntoSlot(slot)) {
                this.dragSplittingSlots.add(slot);
                this.updateDragSplitting();
            }

        }

    }
	
	@Override public void onMouseHover(int mouseX, int mouseY) {}
	
	@Override public void onMouseUnhover(int mouseX, int mouseY) {}
	
	@Override public void onMouseDoubleClicked(int mouseX, int mouseY, int mouseButton) {}
	
	@Override public void onMousePressed(int mouseX, int mouseY, int mouseButton) {}
	
	/**
     * Called when a mouse button is released.  Args : mouseX, mouseY, releaseButton
     */
    @Override
    public void onMouseReleased(int mouseX, int mouseY, int state)
    {
        if(inventorySlots != null) {

            Slot slot = this.getSlotAtPosition(mouseX, mouseY);
            boolean flag = mouseX < getScreenX() || mouseY < getScreenY() || mouseX >= getScreenX() + getWidth() || mouseY >= getScreenY() + getHeight();
            if (slot != null) flag = false; // Forge, prevent dropping of items through slots outside of GUI boundaries
            int k = -1;

            if (slot != null) {
                k = slot.slotNumber;
            }

            if (flag) {
                k = -999;
            }

            if (this.doubleClick && slot != null && state == 0 && this.inventorySlots.canMergeSlot(null, slot)) {
                if (isShiftKeyDown()) {
                    if (slot.inventory != null && this.shiftClickedSlot != null) {
                        for (Slot slot2 : ((List<Slot>)this.inventorySlots.inventorySlots)) {
                            if (slot2 != null && slot2.canTakeStack(mc.player) && slot2.getHasStack() && slot2.inventory == slot.inventory && Container.canAddItemToSlot(slot2, this.shiftClickedSlot, true)) {
                                this.handleMouseClick(slot2, slot2.slotNumber, state, 1);
                            }
                        }
                    }
                } else {
                    this.handleMouseClick(slot, k, state, 6);
                }

                this.doubleClick = false;
                this.lastClickTime = 0L;
            } else {
                if (this.dragSplitting && this.dragSplittingButton != state) {
                    this.dragSplitting = false;
                    this.dragSplittingSlots.clear();
                    this.ignoreMouseUp = true;
                    return;
                }

                if (this.ignoreMouseUp) {
                    this.ignoreMouseUp = false;
                    return;
                }

                if (this.clickedSlot != null && mc.gameSettings.touchscreen) {
                    if (state == 0 || state == 1) {
                        if (this.draggedStack == null && slot != this.clickedSlot) {
                            this.draggedStack = this.clickedSlot.getStack();
                        }

                        boolean flag2 = Container.canAddItemToSlot(slot, this.draggedStack, false);

                        if (k != -1 && this.draggedStack != null && flag2) {
                            this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, state, 0);
                            this.handleMouseClick(slot, k, 0, 0);

                            if (mc.player.inventory.getItemStack() != null) {
                                this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, state, 0);
                                this.touchUpX = mouseX - getScreenX();
                                this.touchUpY = mouseY - getScreenY();
                                this.returningStackDestSlot = this.clickedSlot;
                                this.returningStack = this.draggedStack;
                                this.returningStackTime = Minecraft.getSystemTime();
                            } else {
                                this.returningStack = null;
                            }
                        } else if (this.draggedStack != null) {
                            this.touchUpX = mouseX - getScreenX();
                            this.touchUpY = mouseY - getScreenY();
                            this.returningStackDestSlot = this.clickedSlot;
                            this.returningStack = this.draggedStack;
                            this.returningStackTime = Minecraft.getSystemTime();
                        }

                        this.draggedStack = null;
                        this.clickedSlot = null;
                    }
                } else if (this.dragSplitting && !this.dragSplittingSlots.isEmpty()) {
                    this.handleMouseClick(null, -999, Container.getQuickcraftMask(0, this.dragSplittingLimit), 5);

                    for (Slot slot1 : this.dragSplittingSlots) {
                        this.handleMouseClick(slot1, slot1.slotNumber, Container.getQuickcraftMask(1, this.dragSplittingLimit), 5);
                    }

                    this.handleMouseClick(null, -999, Container.getQuickcraftMask(2, this.dragSplittingLimit), 5);
                } else if (mc.player.inventory.getItemStack() != null) {
                    if (state == mc.gameSettings.keyBindPickBlock.getKeyCode() + 100) {
                        this.handleMouseClick(slot, k, state, 3);
                    } else {
                        boolean flag1 = k != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));

                        if (flag1) {
                            this.shiftClickedSlot = slot != null && slot.getHasStack() ? slot.getStack() : null;
                        }

                        this.handleMouseClick(slot, k, state, flag1 ? 1 : 0);
                    }
                }
            }

            if (mc.player.inventory.getItemStack() == null) {
                this.lastClickTime = 0L;
            }

            this.dragSplitting = false;

        }

    }

    /**
     * Returns if the passed mouse position is over the specified slot. Args : slot, mouseX, mouseY
     */
    protected boolean isMouseOverSlot(Slot slotIn, int mouseX, int mouseY)
    {
        return this.isPointInRegion(slotIn.xPos, slotIn.yPos, 16, 16, mouseX, mouseY);
    }

    /**
     * Test if the 2D point is in a rectangle (relative to the GUI). Args : rectX, rectY, rectWidth, rectHeight, pointX,
     * pointY
     */
    protected boolean isPointInRegion(int left, int top, int right, int bottom, int pointXI, int pointYI)
    {
        float pointX = pointXI - getScreenX();
        float pointY = pointYI - getScreenY();
        return pointX >= left - 1 && pointX < left + right + 1 && pointY >= top - 1 && pointY < top + bottom + 1;
    }

    /**
     * Called when the mouse is clicked over a slot or outside the gui.
     */
    protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType)
    {
        if (slotIn != null)
        {
            slotId = slotIn.slotNumber;
        }

        mc.playerController.windowClick(this.inventorySlots.windowId, slotId, clickedButton, ClickType.values()[clickType], mc.player);
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    @Override
    public void onKeyTyped(char typedChar, int keyCode)
    {
        if (keyCode == 1/* || keyCode == mc.gameSettings.keyBindInventory.getKeyCode()*/)
        {
            mc.player.closeScreen();
        } else {

            this.checkHotbarKeys(keyCode);

            if (this.theSlot != null && this.theSlot.getHasStack()) {
                if (keyCode == mc.gameSettings.keyBindPickBlock.getKeyCode()) {
                    this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, 0, 3);
                } else if (keyCode == mc.gameSettings.keyBindDrop.getKeyCode()) {
                    this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, GuiScreen.isCtrlKeyDown() ? 1 : 0, 4);
                }
            }
        }
    }

    /**
     * This function is what controls the hotbar shortcut check when you press a number key when hovering a stack. Args
     * : keyCode, Returns true if a Hotbar key is pressed, else false
     */
    protected boolean checkHotbarKeys(int keyCode)
    {
        if (mc.player.inventory.getItemStack() == null && this.theSlot != null)
        {
            for (int i = 0; i < 9; ++i)
            {
                if (keyCode == mc.gameSettings.keyBindsHotbar[i].getKeyCode())
                {
                    this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, i, 2);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    @Override
    public void onGuiClose()
    {
        if (inventorySlots != null && mc.player != null)
        {
            this.inventorySlots.onContainerClosed(mc.player);
        }
    }

    /**
     * Called from the main game loop to update the screen.
     */
    @Override
    public void onTick()
    {
        if (!mc.player.isEntityAlive() || mc.player.isDead) {
            mc.player.closeScreen();
        }
    }

    /* ======================================== FORGE START =====================================*/

    /**
     * Returns the slot that is currently displayed under the mouse.
     */
    public Slot getSlotUnderMouse()
    {
        return this.theSlot;
    }

    /* ======================================== FORGE END   =====================================*/
}