package fr.aym.acsguis.component.textarea;

import fr.aym.acsguis.api.GuiAPIClientHelper;
import fr.aym.acsguis.component.EnumComponentType;
import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.cssengine.parsing.ACsGuisCssParser;
import fr.aym.acsguis.cssengine.style.CssComponentStyle;
import fr.aym.acsguis.utils.GuiConstants;
import fr.aym.acsguis.utils.GuiTextureSprite;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class GuiProgressBar extends GuiComponent implements NumericComponent {
    protected int minProgress, maxProgress = 100, progress;
    protected final boolean horizontal;

    protected String progressText = "";

    public GuiProgressBar() {
        this(true);
    }

    public GuiProgressBar(boolean horizontal) {
        this.horizontal = horizontal;
        setMin(0);
        setMax(100);
        setProgress(0);
    }

    @Override
    public EnumComponentType getType() {
        return EnumComponentType.PROGRESS_BAR;
    }

    @Override
    protected ProgressBarStyle createStyleManager() {
        return new ProgressBarStyle(this);
    }

    @Override
    public ProgressBarStyle getStyle() {
        return (ProgressBarStyle) super.getStyle();
    }

    @Override
    public void drawTexturedBackground(int mouseX, int mouseY, float partialTicks) {
        float relProgress = (progress - minProgress) / (maxProgress - minProgress);

        GuiAPIClientHelper.drawRect(getScreenX(), getScreenY(), (int) (getScreenX() + getWidth() * relProgress), getScreenY() + getHeight(), getStyle().fullProgressBarColor);
        GL11.glColor4f(1, 1, 1, 1);

        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(getBackgroundSrcBlend(), getBackgroundDstBlend());

        setBackgroundSrcBlend(GL11.GL_ONE);
        setBackgroundDstBlend(GL11.GL_ONE_MINUS_SRC_ALPHA);

        if (getStyle().getTexture() != null) {
            if (horizontal) {
                int xOffset = (int) Math.ceil(getStyle().getTextureWidth() * relProgress);
                int spriteWidth = (int) Math.ceil(getStyle().getTextureWidth() * (1 - relProgress));
                int uOffset = (int) Math.ceil(getStyle().getTexture().getTextureWidth() * relProgress);
                int textureWidth = (int) Math.ceil(getStyle().getTexture().getTextureWidth() * (1 - relProgress));
                getStyle().getTexture().drawSprite(getScreenX() + xOffset, getScreenY(), spriteWidth, getStyle().getTextureHeight(), uOffset, 0, textureWidth, getStyle().getTexture().getTextureHeight(), spriteWidth, getStyle().getTextureHeight());
            } else {
                int spriteHeight = (int) Math.ceil(getStyle().getTextureHeight() * (1 - relProgress));
                int textureHeight = (int) Math.ceil(getStyle().getTexture().getTextureHeight() * (1 - relProgress));
                getStyle().getTexture().drawSprite(getScreenX(), getScreenY(), getStyle().getTextureWidth(), spriteHeight, 0, 0, getStyle().getTexture().getTextureWidth(), textureHeight, getStyle().getTextureWidth(), spriteHeight);
            }
        }

        if (getStyle().fullTexture != null) {
            if (horizontal) {
                int spriteWidth = (int) Math.ceil(getStyle().getTextureWidth() * relProgress);
                int textureWidth = (int) Math.ceil(getStyle().fullTexture.getTextureWidth() * relProgress);
                getStyle().fullTexture.drawSprite(getScreenX(), getScreenY(), spriteWidth, getStyle().getTextureHeight(), 0, 0, textureWidth, getStyle().fullTexture.getTextureHeight(), spriteWidth, getStyle().getTextureHeight());
            } else {
                int yOffset = (int) Math.ceil(getStyle().getTextureHeight() * (1 - relProgress));
                int spriteHeight = (int) Math.ceil(getStyle().getTextureHeight() * relProgress);
                int vOffset = (int) Math.ceil(getStyle().fullTexture.getTextureHeight() * (1 - relProgress));
                int textureHeight = (int) Math.ceil(getStyle().fullTexture.getTextureHeight() * relProgress);
                getStyle().fullTexture.drawSprite(getScreenX(), getScreenY() + yOffset, getStyle().getTextureWidth(), spriteHeight, 0, vOffset, getStyle().fullTexture.getTextureWidth(), textureHeight, getStyle().getTextureWidth(), spriteHeight);
            }
        }

        GlStateManager.disableBlend();

        drawString(mc.fontRenderer, progressText, (int) (getScreenX() + GuiAPIClientHelper.getRelativeTextX(progressText, getWidth(), getStyle().horizontalTextAlignment, ACsGuisCssParser.DEFAULT_FONT, 1)), (int) (getScreenY() + GuiAPIClientHelper.getRelativeTextY(0, 1, getHeight(), getStyle().verticalTextAlignment, mc.fontRenderer.FONT_HEIGHT)), getStyle().progressTextColor);
    }

    @Override
    public int getMin() {
        return minProgress;
    }

    @Override
    public int getMax() {
        return maxProgress;
    }

    @Override
    public void setMin(int min) {
        this.minProgress = min;
    }

    @Override
    public void setMax(int max) {
        this.maxProgress = max;
    }

    public static class ProgressBarStyle extends CssComponentStyle {
        protected GuiTextureSprite fullTexture;

        protected int fullProgressBarColor;

        /**
         * Text horizontal alignment, relative to the GuiLabel {@link GuiConstants.HORIZONTAL_TEXT_ALIGNMENT}
         **/
        protected GuiConstants.HORIZONTAL_TEXT_ALIGNMENT horizontalTextAlignment = GuiConstants.HORIZONTAL_TEXT_ALIGNMENT.CENTER;
        /**
         * Text horizontal alingment, relative to the GuiLabel {@link GuiConstants.VERTICAL_TEXT_ALIGNMENT}
         **/
        protected GuiConstants.VERTICAL_TEXT_ALIGNMENT verticalTextAlignment = GuiConstants.VERTICAL_TEXT_ALIGNMENT.CENTER;

        protected int progressTextColor;

        public ProgressBarStyle(GuiProgressBar component) {
            super(component);
        }

        public ProgressBarStyle setProgressTextColor(int progressTextColor) {
            this.progressTextColor = progressTextColor;
            return this;
        }

        public int getProgressTextColor() {
            return progressTextColor;
        }

        public ProgressBarStyle setFullProgressBarColor(int fullProgressBarColor) {
            this.fullProgressBarColor = fullProgressBarColor;
            return this;
        }

        public ProgressBarStyle setFullTexture(GuiTextureSprite fullTexture) {
            this.fullTexture = fullTexture;
            return this;
        }

        public GuiConstants.HORIZONTAL_TEXT_ALIGNMENT getHorizontalTextAlignment() {
            return horizontalTextAlignment;
        }

        public ProgressBarStyle setHorizontalTextAlignment(GuiConstants.HORIZONTAL_TEXT_ALIGNMENT horizontalTextAlignment) {
            this.horizontalTextAlignment = horizontalTextAlignment;
            return this;
        }

        public GuiConstants.VERTICAL_TEXT_ALIGNMENT getVerticalTextAlignment() {
            return verticalTextAlignment;
        }

        public ProgressBarStyle setVerticalTextAlignment(GuiConstants.VERTICAL_TEXT_ALIGNMENT verticalTextAlignment) {
            this.verticalTextAlignment = verticalTextAlignment;
            return this;
        }
    }

    public GuiProgressBar setProgress(int progress) {
        this.progress = MathHelper.clamp(progress, minProgress, maxProgress);
        return this;
    }

    public int getProgress() {
        return progress;
    }

    public GuiProgressBar setProgressText(String progressText) {
        this.progressText = progressText;
        return this;
    }

    public String getProgressText() {
        return progressText;
    }
}
