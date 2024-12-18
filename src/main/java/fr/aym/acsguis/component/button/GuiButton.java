package fr.aym.acsguis.component.button;

import fr.aym.acsguis.component.EnumComponentType;
import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.component.style.InternalComponentStyle;
import fr.aym.acsguis.component.style.TextComponentStyle;
import fr.aym.acsguis.component.textarea.TextComponent;
import fr.aym.acsguis.cssengine.font.CssFontHelper;
import fr.aym.acsguis.cssengine.selectors.EnumSelectorContext;
import fr.aym.acsguis.cssengine.style.CssTextComponentStyle;
import fr.aym.acsguis.event.listeners.mouse.IMouseClickListener;
import fr.aym.acsguis.event.listeners.mouse.IMouseExtraClickListener;
import fr.aym.acsguis.event.listeners.mouse.IMouseMoveListener;
import fr.aym.acsguis.utils.ComponentRenderContext;
import fr.aym.acsguis.utils.GuiConstants;
import fr.aym.acsguis.utils.GuiTextureSprite;
import fr.aym.acsguis.utils.IGuiTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;

public class GuiButton extends GuiComponent implements IMouseClickListener, IMouseMoveListener, IMouseExtraClickListener, TextComponent {
    public static final SoundEvent defaultClickButtonSound = SoundEvents.UI_BUTTON_CLICK;

    /*protected static final ResourceLocation widgetsTexture = new ResourceLocation("textures/gui/widgets.png");
    protected static final GuiTextureSprite buttonTexture = new GuiTextureSprite(widgetsTexture, 0, 66, 200, 20);
    protected static final GuiTextureSprite buttonHoveredTexture = new GuiTextureSprite(widgetsTexture, 0, 86, 200, 20);
    protected static final GuiTextureSprite buttonPressedTexture = buttonHoveredTexture;
    protected static final GuiTextureSprite buttonDisabledTexture = new GuiTextureSprite(widgetsTexture, 0, 46, 200, 20);
*/
    protected GuiTextureSprite iconTexture, hoveredIconTexture, pressedIconTexture, disabledIconTexture;

    protected int iconWidth, iconHeight;
    protected float iconRelWidth = 1, iconRelHeight = 1;

    protected GuiConstants.ENUM_SIZE iconHorizontalSize = GuiConstants.ENUM_SIZE.ABSOLUTE;
    protected GuiConstants.ENUM_SIZE iconVerticalSize = GuiConstants.ENUM_SIZE.ABSOLUTE;

    protected GuiConstants.ENUM_ICON_POSITION iconPosition = GuiConstants.ENUM_ICON_POSITION.LEFT;

    protected SoundEvent clickButtonSound = defaultClickButtonSound;
    protected SoundEvent releaseButtonSound = null;
    protected SoundEvent hoverButtonSound = null;

    protected int iconPadding = 3;

    protected String text;

    public GuiButton() {
        addClickListener(this);
    }

    public GuiButton(String text) {
        this();
        setText(text);
    }

    @Override
    public EnumComponentType getType() {
        return EnumComponentType.BUTTON;
    }

    @Override
    protected InternalComponentStyle createStyleManager() {
        return new CssTextComponentStyle(this);
    }

    @Override
    public TextComponentStyle getStyle() {
        return (TextComponentStyle) super.getStyle();
    }

    @Override
    public TextComponentStyle.TextComponentStyleCustomizer getStyleCustomizer() {
        return getStyle().getCustomizer();
    }

    @Override
    public void drawTexturedBackground(int mouseX, int mouseY, float partialTicks) {
        IGuiTexture renderTexture = getStyle().getTexture();
        if (renderTexture != null) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(getBackgroundSrcBlend(), getBackgroundDstBlend());

            int borderSize = 3;
            renderTexture.drawSprite(getScreenX(), getScreenY(), borderSize, borderSize, 0, 0, borderSize, borderSize, borderSize, borderSize);
            renderTexture.drawSprite(getScreenX(), getScreenY() + getHeight() - borderSize, borderSize, borderSize, 0, renderTexture.getTextureHeight() - borderSize, borderSize, borderSize, borderSize, borderSize);
            renderTexture.drawSprite(getScreenX() + getWidth() - borderSize, getScreenY(), borderSize, borderSize, renderTexture.getTextureWidth() - borderSize, 0, borderSize, borderSize, borderSize, borderSize);
            renderTexture.drawSprite(getScreenX() + getWidth() - borderSize, getScreenY() + getHeight() - borderSize, borderSize, borderSize, renderTexture.getTextureWidth() - 3, renderTexture.getTextureHeight() - borderSize, borderSize, borderSize, borderSize, borderSize);

            if (getHeight() > borderSize * 2) {
                renderTexture.drawSprite(getScreenX(), getScreenY() + borderSize, borderSize, getHeight() - borderSize * 2, 0, borderSize, borderSize, renderTexture.getTextureHeight() - borderSize * 2, borderSize, getHeight() - borderSize * 2);
                renderTexture.drawSprite(getScreenX() + getWidth() - borderSize, getScreenY() + borderSize, borderSize, getHeight() - borderSize * 2, renderTexture.getTextureWidth() - borderSize, borderSize, borderSize, renderTexture.getTextureHeight() - borderSize * 2, borderSize, getHeight() - borderSize * 2);
            }

            if (getWidth() > borderSize * 2) {
                renderTexture.drawSprite(getScreenX() + borderSize, getScreenY(), getWidth() - borderSize * 2, borderSize, borderSize, 0, renderTexture.getTextureWidth() - borderSize * 2, borderSize, getWidth() - borderSize * 2, borderSize);
                renderTexture.drawSprite(getScreenX() + borderSize, getScreenY() + getHeight() - borderSize, getWidth() - borderSize * 2, borderSize, borderSize, renderTexture.getTextureHeight() - borderSize, renderTexture.getTextureWidth() - borderSize * 2, borderSize, getWidth() - borderSize * 2, borderSize);
            }

            if (getWidth() > borderSize * 2 || getHeight() > borderSize * 2) {
                renderTexture.drawSprite(getScreenX() + borderSize, getScreenY() + borderSize, renderTexture.getTextureWidth() - borderSize * 2, renderTexture.getTextureHeight() - borderSize * 2, borderSize, borderSize, renderTexture.getTextureWidth() - borderSize * 2, renderTexture.getTextureHeight() - borderSize * 2, getWidth() - borderSize * 2, getHeight() - borderSize * 2);
            }
            //renderTexture.drawSprite(getScreenX(), getScreenY(), getWidth(), getHeight());
            //renderTexture.drawSprite(getScreenX(), getScreenY(), getWidth(), getHeight(), 3, 3, renderTexture.getTextureWidth(), renderTexture.getTextureHeight(), getWidth(), getHeight());

            GlStateManager.disableBlend();
        }

        if (getRenderIcon() != null) {
            GuiTextureSprite renderIconTexture = getRenderIcon();
            float iconX = getIconX(renderIconTexture);
            float iconY = getIconY(renderIconTexture);
            renderIconTexture.drawSprite(getScreenX() + iconX, getScreenY() + iconY, getIconWidth(), getIconHeight());
        }
    }

    @Override
    public void resize(GuiFrame.APIGuiScreen gui, int screenWidth, int screenHeight) {
        super.resize(gui, screenWidth, screenHeight);

        if (getIconHorizontalSize() == GuiConstants.ENUM_SIZE.RELATIVE) {
            setIconWidth((int) (getWidth() * getIconRelativeWidth()));
        }

        if (getIconVerticalSize() == GuiConstants.ENUM_SIZE.RELATIVE) {
            setIconHeight((int) (getHeight() * getIconRelativeHeight()));
        }
    }

    protected float getIconX(GuiTextureSprite renderIconTexture) {
        if (getIconPosition() == GuiConstants.ENUM_ICON_POSITION.LEFT)
            return (getWidth() - mc.fontRenderer.getStringWidth(getText()) - getIconWidth() - getIconPadding()) / 2;
        else if (getIconPosition() == GuiConstants.ENUM_ICON_POSITION.RIGHT)
            return (getWidth() + mc.fontRenderer.getStringWidth(getText()) - getIconWidth() + getIconPadding()) / 2;
        else
            return (getWidth() - getIconWidth()) / 2;

    }

    protected float getIconY(GuiTextureSprite renderIconTexture) {
        if (getIconPosition() == GuiConstants.ENUM_ICON_POSITION.TOP)
            return (getHeight() - mc.fontRenderer.FONT_HEIGHT * 2 - getIconHeight() - getIconPadding()) / 2;
        else if (getIconPosition() == GuiConstants.ENUM_ICON_POSITION.BOTTOM)
            return (getHeight() - mc.fontRenderer.FONT_HEIGHT * 2 + getIconHeight() + getIconPadding()) / 2;
        else
            return (getHeight() - getIconHeight()) / 2;

    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTicks, ComponentRenderContext enableScissor) {
        if (getText() != null && !getText().isEmpty()) {
            float scale = (float) (getStyle().getFontSize()) / mc.fontRenderer.FONT_HEIGHT;
            float textX = (getWidth() / scale - mc.fontRenderer.getStringWidth(getText())) / 2;
            float textY = (getHeight() / scale - mc.fontRenderer.FONT_HEIGHT) / 2;

            if (getRenderIcon() != null) {
                GuiTextureSprite renderIconTexture = getRenderIcon();
                textX = getTextX(renderIconTexture);
                textY = getTextY(renderIconTexture);
            }

            String formatting = getStyle().getFontStyle() == null ? "" : getStyle().getFontStyle().toString();
            GlStateManager.scale(scale, scale, 1);
            CssFontHelper.drawDirect(getStyle().getFontFamily(), getScreenX() / scale + textX, getScreenY() / scale + textY, formatting + getText(), getStyle().getForegroundColor(), getStyle().getEffects());
            GlStateManager.scale(1f / scale, 1f / scale, 1);
        }

        super.drawForeground(mouseX, mouseY, partialTicks, enableScissor);
    }

    protected float getTextX(GuiTextureSprite renderIconTexture) {
        if (getIconPosition() == GuiConstants.ENUM_ICON_POSITION.LEFT)
            return getIconX(renderIconTexture) + getIconWidth() + getIconPadding();
        else if (getIconPosition() == GuiConstants.ENUM_ICON_POSITION.RIGHT)
            return (getWidth() - mc.fontRenderer.getStringWidth(getText()) - getIconWidth() - getIconPadding()) / 2;
        else
            return (getWidth() - mc.fontRenderer.getStringWidth(getText())) / 2;

    }

    protected float getTextY(GuiTextureSprite renderIconTexture) {
        if (getIconPosition() == GuiConstants.ENUM_ICON_POSITION.TOP)
            return getIconY(renderIconTexture) + getIconHeight() + getIconPadding();
        else if (getIconPosition() == GuiConstants.ENUM_ICON_POSITION.BOTTOM)
            return (getHeight() - getIconHeight() - getIconPadding()) / 2;
        else
            return (getHeight() - mc.fontRenderer.FONT_HEIGHT) / 2;

    }

    protected GuiTextureSprite getRenderIcon() {

        EnumSelectorContext state = getState();

        switch (state) {
            case DISABLED:
                return disabledIconTexture != null ? disabledIconTexture : iconTexture;
            case ACTIVE:
                return pressedIconTexture != null ? pressedIconTexture : iconTexture;
            case HOVER:
                return hoveredIconTexture != null ? hoveredIconTexture : iconTexture;
            default:
                return iconTexture;
        }
    }

    public int getIconWidth() {
        return iconWidth;
    }

    public GuiButton setIconWidth(int iconWidth) {
        this.iconWidth = iconWidth;
        return this;
    }

    public int getIconHeight() {
        return iconHeight;
    }

    public GuiButton setIconHeight(int iconHeight) {
        this.iconHeight = iconHeight;
        return this;
    }

    public float getIconRelativeWidth() {
        return iconRelWidth;
    }

    public GuiButton setIconRelativeWidth(float iconRelWidth) {

        setIconHorizontalSize(GuiConstants.ENUM_SIZE.RELATIVE);
        this.iconRelWidth = MathHelper.clamp(iconRelWidth, 0, Float.MAX_VALUE);

        if (getParent() != null) {
            setIconWidth((int) (getIconRelativeWidth() * getParent().getWidth()));
        }

        return this;
    }

    public float getIconRelativeHeight() {
        return iconRelHeight;
    }

    public GuiButton setIconRelativeHeight(float iconRelHeight) {

        setIconVerticalSize(GuiConstants.ENUM_SIZE.RELATIVE);
        this.iconRelHeight = MathHelper.clamp(iconRelHeight, 0, Float.MAX_VALUE);

        if (getParent() != null) {
            setIconHeight((int) (getIconRelativeHeight() * getParent().getHeight()));
        }

        return this;
    }

    public GuiConstants.ENUM_SIZE getIconHorizontalSize() {
        return iconHorizontalSize;
    }

    public GuiButton setIconHorizontalSize(GuiConstants.ENUM_SIZE iconHorizontalSize) {
        this.iconHorizontalSize = iconHorizontalSize;
        return this;
    }

    public GuiConstants.ENUM_SIZE getIconVerticalSize() {
        return iconVerticalSize;
    }

    public GuiButton setIconVerticalSize(GuiConstants.ENUM_SIZE iconVerticalSize) {
        this.iconVerticalSize = iconVerticalSize;
        return this;
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            if (clickButtonSound != null)
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(clickButtonSound, 1.0F));
        }
    }

    @Override
    public void onMouseHover(int mouseX, int mouseY) {
        if (hoverButtonSound != null)
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(hoverButtonSound, 1.0F));
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {
        if (releaseButtonSound != null)
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(releaseButtonSound, 1.0F));
    }

    @Override
    public void onMouseDoubleClicked(int mouseX, int mouseY, int mouseButton) {
    }

    @Override
    public void onMousePressed(int mouseX, int mouseY, int mouseButton) {
    }

    @Override
    public void onMouseMoved(int mouseX, int mouseY) {
    }

    @Override
    public void onMouseUnhover(int mouseX, int mouseY) {
    }

    public SoundEvent getClickButtonSound() {
        return clickButtonSound;
    }

    public GuiButton setClickButtonSound(SoundEvent clickButtonSound) {
        this.clickButtonSound = clickButtonSound;
        return this;
    }

    public SoundEvent getReleaseButtonSound() {
        return releaseButtonSound;
    }

    public GuiButton setReleaseButtonSound(SoundEvent releaseButtonSound) {
        this.releaseButtonSound = releaseButtonSound;
        return this;
    }

    public SoundEvent getHoverButtonSound() {
        return hoverButtonSound;
    }

    public GuiButton setHoverButtonSound(SoundEvent hoverButtonSound) {
        this.hoverButtonSound = hoverButtonSound;
        return this;
    }

    public GuiButton setIconPadding(int iconPadding) {
        this.iconPadding = iconPadding;
        return this;
    }

    public int getIconPadding() {
        return iconPadding;
    }

    public GuiButton setIconTexture(GuiTextureSprite iconTexture) {
        this.iconTexture = iconTexture;
        setIconWidth(iconTexture.getTextureWidth());
        setIconHeight(iconTexture.getTextureHeight());
        return this;
    }

    public GuiButton setHoveredIconTexture(GuiTextureSprite hoveredIconTexture) {
        this.hoveredIconTexture = hoveredIconTexture;
        setIconWidth(hoveredIconTexture.getTextureWidth());
        setIconHeight(hoveredIconTexture.getTextureHeight());
        return this;
    }

    public GuiButton setPressedIconTexture(GuiTextureSprite pressedIconTexture) {
        this.pressedIconTexture = pressedIconTexture;
        setIconWidth(pressedIconTexture.getTextureWidth());
        setIconHeight(pressedIconTexture.getTextureHeight());
        return this;
    }

    public GuiButton setDisabledIconTexture(GuiTextureSprite disabledIconTexture) {
        this.disabledIconTexture = disabledIconTexture;
        setIconWidth(disabledIconTexture.getTextureWidth());
        setIconHeight(disabledIconTexture.getTextureHeight());
        return this;
    }

    @Override
    public GuiButton setText(String text) {
        this.text = text;
        return this;
    }

    @Override
    public String getText() {
        return text;
    }

    public GuiConstants.ENUM_ICON_POSITION getIconPosition() {
        return iconPosition;
    }

    public GuiButton setIconPosition(GuiConstants.ENUM_ICON_POSITION iconPosition) {
        this.iconPosition = iconPosition;
        return this;
    }
}
