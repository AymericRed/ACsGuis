package fr.aym.acsguis.component;

import fr.aym.acsguis.GuiAPIClientHelper;
import fr.aym.acsguis.GuiConstants;
import fr.aym.acsguis.cssengine.parsing.DnxCssParser;
import fr.aym.acsguis.cssengine.style.CssComponentStyleManager;
import fr.aym.acsguis.utils.GuiTextureSprite;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GuiProgressBar extends GuiComponent<GuiProgressBar.ProgressBarStyleManager>
{
	protected GuiTextureSprite emptyTexture, fullTexture;

    protected int progressTextureWidth, progressTextureHeight;
    protected float progressTextureRelWidth = 1, progressTextureRelHeight = 1;

    protected GuiConstants.ENUM_SIZE progressTextureHorizontalSize = GuiConstants.ENUM_SIZE.ABSOLUTE;
    protected GuiConstants.ENUM_SIZE progressTextureVerticalSize = GuiConstants.ENUM_SIZE.ABSOLUTE;
	
	protected float minProgress, maxProgress, progress;
	protected final boolean horizontal;

	protected int fullProgressBarColor = Color.GRAY.getRGB();
	
	/** Text horizontal alignment, relative to the GuiLabel {@link GuiConstants.HORIZONTAL_TEXT_ALIGNMENT} **/
	protected GuiConstants.HORIZONTAL_TEXT_ALIGNMENT horizontalTextAlignment = GuiConstants.HORIZONTAL_TEXT_ALIGNMENT.CENTER;
	/** Text horizontal alingment, relative to the GuiLabel {@link GuiConstants.VERTICAL_TEXT_ALIGNMENT} **/
	protected GuiConstants.VERTICAL_TEXT_ALIGNMENT verticalTextAlignment = GuiConstants.VERTICAL_TEXT_ALIGNMENT.CENTER;
	
	protected int progressTextColor = Color.WHITE.getRGB();
	protected String progressText = "";
	
	public GuiProgressBar(int x, int y, int width, int height) {
		this(x, y, width, height, true);
	}

    @Override
    public EnumComponentType getType() {
        return EnumComponentType.PROGRESS_BAR;
    }

    @Override
    public ProgressBarStyleManager getStyle() {
        return new ProgressBarStyleManager(this);
    }

    public GuiProgressBar(int x, int y, int width, int height, boolean horizontal) {
		super(x, y, width, height);
		this.horizontal = horizontal;
		setMinProgress(0);
		setMaxProgress(100);
		setProgress(0);
		//TODO COLOR AND TEXTURE CSS MANAGEMENT
		setFullProgressBarColor(Color.GRAY.getRGB());
	}

    @Override
    public void drawTexturedBackground(int mouseX, int mouseY, float partialTicks) {

        float relProgress = (progress - minProgress) / (maxProgress - minProgress);

        drawRect(getScreenX(), getScreenY(), (int) (getScreenX() + getWidth() * relProgress), getScreenY() + getHeight(), fullProgressBarColor);
        GL11.glColor4f(1,1,1,1);

        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(getBackgroundSrcBlend(), getBackgroundDstBlend());

        setBackgroundSrcBlend(GL11.GL_ONE);
        setBackgroundDstBlend(GL11.GL_ONE_MINUS_SRC_ALPHA);

        if(emptyTexture != null)
        {
            if(horizontal) {
                int xOffset = (int) Math.ceil(getProgressTextureWidth() * relProgress);
                int spriteWidth = (int) Math.ceil(getProgressTextureWidth() * (1 - relProgress));
                int uOffset = (int) Math.ceil(emptyTexture.getTextureWidth() * relProgress);
                int textureWidth = (int) Math.ceil(emptyTexture.getTextureWidth() * (1 - relProgress));
                emptyTexture.drawSprite(getScreenX() + xOffset, getScreenY(), spriteWidth, getProgressTextureHeight(), uOffset, 0, textureWidth, emptyTexture.getTextureHeight(), spriteWidth, getProgressTextureHeight());
            } else {
                int spriteHeight = (int) Math.ceil(getProgressTextureHeight() * (1 - relProgress));
                int textureHeight = (int) Math.ceil(emptyTexture.getTextureHeight() * (1 - relProgress));
                emptyTexture.drawSprite(getScreenX(), getScreenY(), getProgressTextureWidth(), spriteHeight, 0, 0, emptyTexture.getTextureWidth(), textureHeight, getProgressTextureWidth(), spriteHeight);
            }
        }

        if(fullTexture != null)
        {
            if(horizontal) {
                int spriteWidth = (int) Math.ceil(getProgressTextureWidth() * relProgress);
                int textureWidth = (int) Math.ceil(fullTexture.getTextureWidth() * relProgress);
                fullTexture.drawSprite(getScreenX(), getScreenY(), spriteWidth, getProgressTextureHeight(), 0, 0, textureWidth, fullTexture.getTextureHeight(), spriteWidth, getProgressTextureHeight());
            } else {
                int yOffset = (int) Math.ceil(getProgressTextureHeight() * (1 - relProgress));
                int spriteHeight = (int) Math.ceil(getProgressTextureHeight() * relProgress);
                int vOffset = (int) Math.ceil(fullTexture.getTextureHeight() * (1 - relProgress));
                int textureHeight = (int) Math.ceil(fullTexture.getTextureHeight() * relProgress);
                fullTexture.drawSprite(getScreenX(), getScreenY() + yOffset, getProgressTextureWidth(), spriteHeight, 0, vOffset, fullTexture.getTextureWidth(), textureHeight, getProgressTextureWidth(), spriteHeight);
            }
        }

        GlStateManager.disableBlend();
		
		drawString(mc.fontRenderer, progressText, (int) (getScreenX() + GuiAPIClientHelper.getRelativeTextX(progressText, getWidth(), horizontalTextAlignment, DnxCssParser.DEFAULT_FONT, 1)), (int) (getScreenY() + GuiAPIClientHelper.getRelativeTextY(0, 1, getHeight(), verticalTextAlignment, mc.fontRenderer.FONT_HEIGHT)), progressTextColor);
    }

    public class ProgressBarStyleManager extends CssComponentStyleManager
    {
        public ProgressBarStyleManager(GuiProgressBar component) {
            super(component);
        }

        @Override
        public void updateComponentSize(int screenWidth, int screenHeight) {
            super.updateComponentSize(screenWidth, screenHeight);

            if (getProgressTextureHorizontalSize() == GuiConstants.ENUM_SIZE.RELATIVE) {
                setProgressTextureWidth((int) (getWidth() * getProgressTextureRelativeWidth()));
            }

            if (getProgressTextureVerticalSize() == GuiConstants.ENUM_SIZE.RELATIVE) {
                setProgressTextureHeight((int) (getHeight() * getProgressTextureRelativeHeight()));
            }
        }
    }

    public GuiProgressBar setFullProgressBarColor(int fullProgressBarColor) {
        this.fullProgressBarColor = fullProgressBarColor;
        return this;
    }

    public GuiProgressBar setEmptyTexture(GuiTextureSprite emptyTexture) {
        this.emptyTexture = emptyTexture;
        setProgressTextureWidth(emptyTexture.getTextureWidth());
        setProgressTextureHeight(emptyTexture.getTextureHeight());
        return this;
    }

    public GuiProgressBar setFullTexture(GuiTextureSprite fullTexture) {
        this.fullTexture = fullTexture;
        setProgressTextureWidth(fullTexture.getTextureWidth());
        setProgressTextureHeight(fullTexture.getTextureHeight());
        return this;
    }

    public int getProgressTextureWidth() {
        return progressTextureWidth;
    }

    public GuiProgressBar setProgressTextureWidth(int progressTextureWidth) {
        this.progressTextureWidth = progressTextureWidth;
        return this;
    }

    public int getProgressTextureHeight() {
        return progressTextureHeight;
    }

    public GuiProgressBar setProgressTextureHeight(int progressTextureHeight) {
        this.progressTextureHeight = progressTextureHeight;
        return this;
    }

    public float getProgressTextureRelativeWidth() {
        return progressTextureRelWidth;
    }

    public GuiProgressBar setProgressTextureRelativeWidth(float progressTextureRelWidth) {
        setProgressTextureHorizontalSize(GuiConstants.ENUM_SIZE.RELATIVE);
        this.progressTextureRelWidth = MathHelper.clamp(progressTextureRelWidth, 0, Float.MAX_VALUE);

        if(getParent() != null) {
            setProgressTextureWidth((int) (getProgressTextureRelativeWidth() * getParent().getWidth()));
        }

        return this;
    }

    public float getProgressTextureRelativeHeight() {
        return progressTextureRelHeight;
    }

    public GuiProgressBar setProgressTextureRelativeHeight(float progressTextureRelHeight) {
        setProgressTextureVerticalSize(GuiConstants.ENUM_SIZE.RELATIVE);
        this.progressTextureRelHeight = MathHelper.clamp(progressTextureRelHeight, 0, Float.MAX_VALUE);

        if(getParent() != null) {
            setProgressTextureHeight((int) (getProgressTextureRelativeHeight() * getParent().getHeight()));
        }
	    
        return this;
    }

    public GuiConstants.ENUM_SIZE getProgressTextureHorizontalSize() {
        return progressTextureHorizontalSize;
    }

    public GuiProgressBar setProgressTextureHorizontalSize(GuiConstants.ENUM_SIZE progressTextureHorizontalSize) {
        this.progressTextureHorizontalSize = progressTextureHorizontalSize;
        return this;
    }

    public GuiConstants.ENUM_SIZE getProgressTextureVerticalSize() {
        return progressTextureVerticalSize;
    }

    public GuiProgressBar setProgressTextureVerticalSize(GuiConstants.ENUM_SIZE progressTextureVerticalSize) {
        this.progressTextureVerticalSize = progressTextureVerticalSize;
        return this;
    }

    public GuiProgressBar setProgress(float progress) {
        this.progress = MathHelper.clamp(progress, minProgress, maxProgress);
        return this;
    }

    public float getProgress() {
        return progress;
    }

    public GuiProgressBar setMinProgress(float minProgress) {
        this.minProgress = minProgress;
        return this;
    }

    public float getMinProgress() {
        return minProgress;
    }

    public GuiProgressBar setMaxProgress(float maxProgress) {
        this.maxProgress = maxProgress;
        return this;
    }

    public float getMaxProgress() {
        return maxProgress;
    }

    public GuiProgressBar setProgressTextColor(int progressTextColor) {
    	this.progressTextColor = progressTextColor;
        return this;
	}
	
	public int getProgressTextColor() {
		return progressTextColor;
	}
	
	public GuiProgressBar setProgressText(String progressText) {
		this.progressText = progressText;
        return this;
	}
	
	public String getProgressText() {
		return progressText;
	}
	
	public GuiConstants.HORIZONTAL_TEXT_ALIGNMENT getHorizontalTextAlignment() {
		return horizontalTextAlignment;
	}
	
	public GuiProgressBar setHorizontalTextAlignment(GuiConstants.HORIZONTAL_TEXT_ALIGNMENT horizontalTextAlignment) {
		this.horizontalTextAlignment = horizontalTextAlignment;
		return this;
	}
	
	public GuiConstants.VERTICAL_TEXT_ALIGNMENT getVerticalTextAlignment() {
		return verticalTextAlignment;
	}
	
	public GuiProgressBar setVerticalTextAlignment(GuiConstants.VERTICAL_TEXT_ALIGNMENT verticalTextAlignment) {
		this.verticalTextAlignment = verticalTextAlignment;
        return this;
	}
}
