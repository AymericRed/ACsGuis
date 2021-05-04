package fr.aym.acsguis.component.button;

import fr.aym.acsguis.component.EnumComponentType;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GuiButtonWithItem extends GuiButton
{
    protected static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");
    /** The string displayed on this control. */
    public String hoverMessage;
    public Item icon;
    /** True if this control is enabled, false to disable. */
    public boolean enabled;
    /** Hides the button completely if false. */
    public boolean visible;
    protected boolean hovered;
    public int packedFGColour;
    
    private boolean hasNoBackground;

    public GuiButtonWithItem(Item icon, String hoverMessage)
    {
    	this(0, 0, 20, 20, icon, hoverMessage);
    }
    public GuiButtonWithItem(int x, int y, int width, int height, Item icon, String hoverMessage)
    {
    	super(x, y, width, height);
    	
        this.enabled = true;
        this.visible = true;
        this.hoverMessage = hoverMessage;
        
        this.icon = icon;
    }

    public GuiButtonWithItem setHasNoBackgroundTexture()
    {
    	hasNoBackground = true;
    	return this;
    }

    @Override
    public EnumComponentType getType() {
        return EnumComponentType.BUTTON_WITH_ICON;
    }

    @Override
    public void drawBackground(int mouseX, int mouseY, float partialTicks) 
    {
        super.drawBackground(mouseX, mouseY, partialTicks);
    	FontRenderer fontrenderer = mc.fontRenderer;
        
		float factorX = 1.0F;
        float factorY = 1.0F;
		if(getWidth() != 20)
		{
			factorX = (float)getWidth()/20;
		}
        if(getHeight() != 20)
        {
            factorY = (float)getHeight()/20;
        }

		if(!hasNoBackground)
		{
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glColor4f(1, 1, 1, 1);
	        drawTexturedBackground(mouseX, mouseY, partialTicks);
		}
        
      /*  this.zLevel = 100;
        itemRender.zLevel = 100.0F;*/

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        ItemStack itemstack = new ItemStack(icon);
        
        if(factorX != 1 || factorY != 1)
        {
    		GL11.glScalef(factorX, factorY, 0);
        }
        int x = (int) (getScreenX()/factorX);
        int y = (int) (getScreenY()/factorY);
        mc.getRenderItem().renderItemAndEffectIntoGUI(itemstack, x+2, y+2);
        //itemRender.renderItemOverlayIntoGUI(fontrenderer, mc.getTextureManager(), itemstack, yPosition, j, null);
        if(factorX != 1 || factorY != 1)
        {
    		GL11.glScalef(1/factorX, 1/factorY, 0);
        }

        /*itemRender.zLevel = 0.0F;
        this.zLevel = 0;*/
    	//super.drawBackground(mouseX, mouseY, partialTicks);
        
        if(this.isHovered() && !StringUtils.isNullOrEmpty(hoverMessage))
        {
        	setHoveringText(fontrenderer.listFormattedStringToWidth(hoverMessage, 50));
        }
    	RenderHelper.disableStandardItemLighting();
    }
}
