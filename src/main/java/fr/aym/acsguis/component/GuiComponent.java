package fr.aym.acsguis.component;

import fr.aym.acsguis.api.GuiAPIClientHelper;
import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.cssengine.parsing.ACsGuisCssParser;
import fr.aym.acsguis.cssengine.selectors.CompoundCssSelector;
import fr.aym.acsguis.cssengine.selectors.EnumSelectorContext;
import fr.aym.acsguis.component.style.AutoStyleHandler;
import fr.aym.acsguis.component.style.ComponentStyleManager;
import fr.aym.acsguis.cssengine.style.CssComponentStyleManager;
import fr.aym.acsguis.cssengine.style.CssStyleProperty;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperties;
import fr.aym.acsguis.event.ComponentKeyboardEvent;
import fr.aym.acsguis.event.ComponentMouseEvent;
import fr.aym.acsguis.event.ComponentRenderEvent;
import fr.aym.acsguis.event.ComponentStateEvent;
import fr.aym.acsguis.event.listeners.*;
import fr.aym.acsguis.event.listeners.mouse.IMouseClickListener;
import fr.aym.acsguis.event.listeners.mouse.IMouseExtraClickListener;
import fr.aym.acsguis.event.listeners.mouse.IMouseMoveListener;
import fr.aym.acsguis.event.listeners.mouse.IMouseWheelListener;
import fr.aym.acsguis.utils.CircleBackground;
import fr.aym.acsguis.utils.IGuiTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Base of any gui component
 *
 * @param <T> The type of the {@link ComponentStyleManager}
 */
public abstract class GuiComponent<T extends ComponentStyleManager> extends Gui implements Comparable<GuiComponent<T>>
{
    /**
     * Very useful
     */
    protected static final Minecraft mc = Minecraft.getMinecraft();

    /**
     * The parent of this component
     */
    protected GuiPanel parent;

    /**
     * The {@link ComponentStyleManager} of this component
     */
    protected T style;

    /**
     * The css id of this component
     */
    protected String cssId;

    /**
     * The css class of this component
     */
    protected String cssClass;

    protected boolean enabled;
    protected boolean hovered, pressed;

    protected boolean focused, canLooseFocus;

    /**Blend function for the textured background**/
    protected int backgroundSrcBlend = GL11.GL_ONE, backgroundDstBlend = GL11.GL_ONE_MINUS_SRC_ALPHA;

    /**Text to display when the component is hovered**/
    protected List<String> hoveringText = new ArrayList<String>();

    protected final List<IMouseClickListener> clickListeners = new ArrayList<IMouseClickListener>();
    protected final List<IMouseExtraClickListener> extraClickListeners = new ArrayList<IMouseExtraClickListener>();
    protected final List<IMouseMoveListener> moveListeners = new ArrayList<IMouseMoveListener>();
    protected final List<IMouseWheelListener> wheelListeners = new ArrayList<IMouseWheelListener>();
    protected final List<IKeyboardListener> keyboardListeners = new ArrayList<IKeyboardListener>();
    protected final List<ITickListener> tickListeners = new ArrayList<ITickListener>();
    protected final List<IGuiOpenListener> openListeners = new ArrayList<IGuiOpenListener>();
    protected final List<IGuiCloseListener> closeListeners = new ArrayList<IGuiCloseListener>();
    protected final List<IResizeListener> resizeListeners = new ArrayList<IResizeListener>();
    protected final List<IFocusListener> focusListeners = new ArrayList<IFocusListener>();

    /**
     * Creates a new component
     */
    public GuiComponent() {
        this(0, 0, 0, 0);
    }

    /**
     * Creates a new component this custom pos and size
     *
     * @deprecated Use the css to modify element size and position
     */
    @Deprecated
    public GuiComponent(int x, int y, int width, int height) {
        style = createStyleManager();
        setEnabled(true);
        setVisible(true);
        style.getXPos().setAbsolute(x);
        style.getYPos().setAbsolute(y);
        style.getWidth().setAbsolute(width);
        style.getHeight().setAbsolute(height);
        setCanLooseFocus(true);
    }

    /**
     * Sets the css class of this component, use <code>.cssId</code> in your css code to refer to this element
     */
    public GuiComponent<T> setCssClass(@Nullable String cssClass) {
        this.cssClass = cssClass;
        getStyle().refreshCss(true, "set_class");
        return this;
    }

    /**
     * @return The css class of this component
     */
    @Nullable
    public String getCssClass() {
        return cssClass;
    }

    /**
     * Sets the css id of this component, use <code>#cssId</code> in your css code to refer to this element <br>
     *     It's a convention to make it unique for each component in your gui
     */
    public GuiComponent<T> setCssId(@Nullable String cssId) {
        this.cssId = cssId;
        getStyle().refreshCss(true, "set_id");
        return this;
    }

    /**
     * @return The css id of this component
     */
    @Nullable
    public String getCssId() {
        return cssId;
    }

    /**
     * Sets custom css code for this element <br>
     *     This code overrides (but does not replace) the style in css sheets <br>
     *     This css is applied in all {@link EnumSelectorContext} <br>
     *     <strong>You must set the css id of this element before calling this (or call the other setCssCode method). Also do not change the id or you will cancel the disable css code.</strong>
     *
     * @param cssCode The css code to set, this must be properties and values, but no selector is allowed here (as in html code)
     */
    public GuiComponent<T> setCssCode(String cssCode) {
        if(getCssId() == null)
            throw new IllegalArgumentException("You should the css id of the element before !");
        Map<CompoundCssSelector, Map<EnumCssStyleProperties, CssStyleProperty<?>>> data = ACsGuisCssParser.parseRawCss(this, cssCode);
        getStyle().setCustomParsedStyle(data);
        return this;
    }

    /**
     * Sets custom css code for this element <br>
     *     This code overrides (but does not replace) the style in css sheets <br>
     *     This css is applied in all {@link EnumSelectorContext} <br>
     *     <strong>This also sets the css id of the component, do not change it or you will cancel the disable css code</strong>
     *
     * @param cssId the css id to set, use <code>#cssId</code> in your css code to refer to this element
     * @param cssCode The css code to set, this must be properties and values, but no selector is allowed here (as in html code)
     */
    public GuiComponent<T> setCssCode(String cssId, String cssCode) {
        setCssId(cssId);
        return setCssCode(cssCode);
    }

    /**
     * The component type, usable in css code to refer to all elements of this type
     */
    public abstract EnumComponentType getType();

    /**
     * Called before class init to create a {@link ComponentStyleManager}
     *
     * @return By default, a new {@link CssComponentStyleManager}
     */
    protected T createStyleManager() {
        return (T) new CssComponentStyleManager(this);
    }

    /**
     * Used to sort the render pipeline depending on the {@code zLevel}
     */
    @Override
    public int compareTo(GuiComponent other) {
        return Integer.compare(style.getZLevel(), other.style.getZLevel());
    }

    /**
     * Draws this component <br>
     *     You can override drawBackground and drawForeground
     */
    public final void render(int mouseX, int mouseY, float partialTicks)
    {
        if(isVisible() && !MinecraftForge.EVENT_BUS.post(new ComponentRenderEvent.ComponentRenderAllEvent(this))) {

            bindLayerBounds();

            GlStateManager.translate(0, 0, getStyle().getZLevel());
            //if (isVisible()) {
                if (!MinecraftForge.EVENT_BUS.post(new ComponentRenderEvent.ComponentRenderBackgroundEvent(this)))
                    drawBackground(mouseX, mouseY, partialTicks);
                if (!MinecraftForge.EVENT_BUS.post(new ComponentRenderEvent.ComponentRenderForegroundEvent(this)))
                    drawForeground(mouseX, mouseY, partialTicks);
            //}
            GlStateManager.translate(0, 0, -getStyle().getZLevel());

            unbindLayerBounds();

        }
    }

    /**
     * Draws the component background (texture, color and borders)
     */
    public void drawBackground(int mouseX, int mouseY, float partialTicks)
    {
        if(getBorderSize() > 0) {
            if(style.getBorderPosition() == ComponentStyleManager.BORDER_POSITION.EXTERNAL) {
                GuiAPIClientHelper.glScissor(getRenderMinX() - getBorderSize(), getRenderMinY() - getBorderSize(), getRenderMaxX() - getRenderMinX() + getBorderSize() * 2, getRenderMaxY() - getRenderMinY() + getBorderSize() * 2);
                GuiAPIClientHelper.drawBorderedRectangle(getScreenX() - getBorderSize(), getScreenY() - getBorderSize(), getScreenX() + getWidth() + getBorderSize(), getScreenY() + getHeight() + getBorderSize(), getBorderSize(), style.getBackgroundColor(), style.getBorderColor(), style.getBorderRadius());
            } else {
                GuiAPIClientHelper.drawBorderedRectangle(getScreenX(), getScreenY(), getScreenX() + getWidth(), getScreenY() + getHeight(), getBorderSize(), style.getBackgroundColor(), style.getBorderColor(), style.getBorderRadius());
            }
        } else {
            //System.out.println("Back color of "+this+" is "+style.getBackgroundColor());
            CircleBackground.renderBackground(style.getBorderRadius(), getScreenX(), getScreenY(), getScreenX() + getWidth(), getScreenY() + getHeight(), style.getBackgroundColor());
            //GuiScreen.drawRect(getScreenX(), getScreenY(), getScreenX() + getWidth(), getScreenY() + getHeight(), style.getBackgroundColor());
        }

        GlStateManager.color(1, 1, 1, 1);
        drawTexturedBackground(mouseX, mouseY, partialTicks);
    }

    /**
     * Renders the background texture (if any)
     */
    public void drawTexturedBackground(int mouseX, int mouseY, float partialTicks)
    {
        IGuiTexture renderTexture = style.getTexture();

        if (renderTexture != null) {
            GlStateManager.enableBlend();
            //GlStateManager.blendFunc(getBackgroundSrcBlend(), getBackgroundDstBlend());
            //renderTexture.drawSprite(getScreenX(), getScreenY(), getTextureWidth(), getTextureHeight(), isRepeatBackgroundX() ? getWidth() : getTextureWidth(), isRepeatBackgroundY() ? getHeight() : getTextureHeight());
            renderTexture.drawSprite(getScreenX(), getScreenY(), getWidth(), getHeight());
            GlStateManager.disableBlend();
        }
    }

    /**
     * Draws the component foreground (child elements, text, ...)
     */
    public void drawForeground(int mouseX, int mouseY, float partialTicks) {
        if(isHovered() && !hoveringText.isEmpty()) {
            GuiFrame.hoveringText = hoveringText;
        }

        if(isHovered() && !isFocused() && !GuiFrame.hasDebugInfo)
        {
            displayComponentOnDebugPane();
        }
    }

    /**
     * Computes and displays debug info of this component
     */
    public void displayComponentOnDebugPane() {
        List<String> debug = new ArrayList<>();
        debug.add(TextFormatting.AQUA+"Element : "+getType()+" id="+getCssId()+" class="+getCssClass());
        debug.add("-------------");
        debug.addAll(ACsGuisCssParser.getStyleFor(style).getProperties(getState(), style));
        //debug.add("-------------");
        debug.add(TextFormatting.BLUE+"Auto styles :");
        style.getAutoStyleHandlers().forEach(h -> {
            AutoStyleHandler<T> hc = (AutoStyleHandler<T>) h;
            debug.add(hc.getPriority(style)+" "+hc+" "+hc.getModifiedProperties(style));
        });
        GuiFrame.setupDebug(getStyle().getParent(), debug);
    }

    /**
     * Bind the scissor test to render only the child component's part in this component boundaries.
     */
    protected void bindLayerBounds() {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GuiAPIClientHelper.glScissor(getRenderMinX(), getRenderMinY(), getRenderMaxX() - getRenderMinX(), getRenderMaxY() - getRenderMinY());
    }

    protected void unbindLayerBounds() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public int getRenderMinX() {
        return getParent() != null ? Math.max(getScreenX(), getParent().getRenderMinX()) : getScreenX();
    }

    public int getRenderMinY() {
        return getParent() != null ? Math.max(getScreenY(), getParent().getRenderMinY()) : getScreenY();
    }

    public int getRenderMaxX() {
        return getParent() != null ? Math.min(getScreenX() + getWidth(), getParent().getRenderMaxX()) : getScreenX() + getWidth();
    }

    public int getRenderMaxY() {
        return getParent() != null ? Math.min(getScreenY() + getHeight(), getParent().getRenderMaxY()) : getScreenY() + getHeight();
    }

    /**
     * @return X position on screen
     */
    public int getScreenX() {
        return getX() + (getParent() != null ? getParent().getScreenX() : 0) + style.getOffsetX();
    }

    /**
     * @return Y position on screen
     */
    public int getScreenY() {
        return getY() + (getParent() != null ? getParent().getScreenY() : 0) + style.getOffsetY();
    }

    /**
     * @return Return the state of this component
     */
    public EnumSelectorContext getState()
    {
        if(!isEnabled()) return EnumSelectorContext.DISABLED;
        else if(isPressed()) return EnumSelectorContext.ACTIVE;
        else if(isHovered()) return EnumSelectorContext.HOVER;
        else return EnumSelectorContext.NORMAL;
    }

    /**
     * Resizes the component
     *
     * @param screenWidth Scaled screen width
     * @param screenHeight Scaled screen height
     */
    public void resize(int screenWidth, int screenHeight) {
        style.resize(screenWidth, screenHeight);

        for(IResizeListener resizeListener : resizeListeners) {
            resizeListener.onResize(screenWidth, screenHeight);
        }
    }

    /**
     * Updates the component
     */
    public void tick()
    {
        if(isVisible() && !MinecraftForge.EVENT_BUS.post(new ComponentStateEvent.ComponentTickEvent(this))) {

            for(ITickListener tickListener : tickListeners) {
                tickListener.onTick();
            }

            if(isPressed() && GuiFrame.press() && !MinecraftForge.EVENT_BUS.post(new ComponentMouseEvent.ComponentMousePressEvent(this, GuiFrame.mouseX, GuiFrame.mouseY, GuiFrame.mouseButton))) {
                for(IMouseExtraClickListener extraClickListener : extraClickListeners) {
                    extraClickListener.onMousePressed(GuiFrame.mouseX, GuiFrame.mouseY, GuiFrame.mouseButton);
                }
            }
            style.update();

            if(this instanceof GuiPanel) {

                ((GuiPanel) this).flushComponentsQueue();
                ((GuiPanel) this).flushRemovedComponents();

                for(GuiComponent component : ((GuiPanel) this).getChildComponents()) {
                    component.tick();
                }
            }
        }
        else
            style.update();
    }

    /**
     * Handles keyboard input
     */
    public void keyTyped(char typedChar, int keyCode)
    {
        if(canInteract() && !MinecraftForge.EVENT_BUS.post(new ComponentKeyboardEvent.ComponentKeyTypeEvent(this, typedChar, keyCode)))
        {
            for (IKeyboardListener keyboardListener : keyboardListeners) {
                keyboardListener.onKeyTyped(typedChar, keyCode);
            }

            if(this instanceof GuiPanel) {
                for(GuiComponent component : ((GuiPanel) this).getReversedChildComponents()) {
                    component.keyTyped(typedChar, keyCode);
                }
            }
        }
    }

    /**
     * Handles mouse move
     *
     * @param mouseX X position of the mouse
     * @param mouseY Y position of the mouse
     * @param canBeHovered Return false if another component took the priority (depending on zLevel)
     */
    public final void mouseMoved(int mouseX, int mouseY, boolean canBeHovered) {

        if(canInteract() && !MinecraftForge.EVENT_BUS.post(new ComponentMouseEvent.ComponentMouseMoveEvent(this, GuiFrame.lastMouseX, GuiFrame.lastMouseY, mouseX, mouseY)))
        {
            for(IMouseMoveListener moveListener : moveListeners) {
                moveListener.onMouseMoved(mouseX, mouseY);
            }

            if (!MinecraftForge.EVENT_BUS.post(new ComponentMouseEvent.ComponentMouseHoverEvent(this, mouseX, mouseY)))
            {
                boolean wasHovered = isHovered();

                setHovered(isMouseOver(mouseX, mouseY) && canBeHovered);

                if(isHovered() != wasHovered)
                {
                    for(IMouseMoveListener moveListener : moveListeners)
                    {
                        if(isHovered()) {
                            moveListener.onMouseHover(mouseX, mouseY);
                        } else {
                            moveListener.onMouseUnhover(mouseX, mouseY);
                        }
                    }
                }
            }

            if(this instanceof GuiPanel) {

                boolean canBeHovered1 = canBeHovered;

                for(GuiComponent component : ((GuiPanel) this).getReversedChildComponents()) {

                    component.mouseMoved(mouseX, mouseY, canBeHovered1);

                    if(component.isHovered()) {
                        canBeHovered1 = false;
                    }
                }

            }
        }

    }

    /**
     * Handles mouse click
     *
     * @param mouseX X position of the mouse
     * @param mouseY Y position of the mouse
     * @param mouseButton The pressed mouse button
     * @param canBePressed Return false if another component took the priority (depending on zLevel)
     */
    public final void mouseClicked(int mouseX, int mouseY, int mouseButton, boolean canBePressed)
    {
        if(canInteract() && !MinecraftForge.EVENT_BUS.post(new ComponentMouseEvent.ComponentMouseClickEvent(this, mouseX, mouseY, mouseButton)))
        {
            if(isHovered() && canBePressed) {
                setFocused(true);
                setPressed(true);

                for(IFocusListener focusListener : focusListeners) {
                    focusListener.onFocus();
                }

                for(IMouseClickListener clickListener : clickListeners) {
                    clickListener.onMouseClicked(mouseX, mouseY, mouseButton);
                }

                if(GuiFrame.doubleClick()) {

                    if(!MinecraftForge.EVENT_BUS.post(new ComponentMouseEvent.ComponentMouseDoubleClickEvent(this, GuiFrame.lastClickTime, mouseX, mouseY)))
                    {
                        for(IMouseExtraClickListener extraClickListener : extraClickListeners) {
                            extraClickListener.onMouseDoubleClicked(mouseX, mouseY, mouseButton);
                        }
                    }
                }
            } else {
                if(canLooseFocus()) {
                    setFocused(false);

                    for(IFocusListener focusListener : focusListeners) {
                        focusListener.onFocusLoose();
                    }
                }
                setPressed(false);
            }

            if(this instanceof GuiPanel) {
                boolean canBePressed1 = canBePressed;
                for(GuiComponent<?> component : ((GuiPanel) this).getReversedChildComponents()) {
                    component.mouseClicked(mouseX, mouseY, mouseButton, canBePressed1);
                    if(component.isPressed()) {
                        canBePressed1 = false;
                    }
                }
            }
        } else {
            if(canLooseFocus()) {
                setFocused(false);
            }
            setPressed(false);
        }
    }

    public final void mouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        if(!MinecraftForge.EVENT_BUS.post(new ComponentMouseEvent.ComponentMouseReleaseEvent(this, mouseX, mouseY, mouseButton))) {
            setPressed(false);

            for(IMouseExtraClickListener extraClickListener : extraClickListeners) {
                extraClickListener.onMouseReleased(mouseX, mouseY, mouseButton);
            }

            if(this instanceof GuiPanel) {
                for (GuiComponent<?> component : ((GuiPanel) this).getReversedChildComponents()) {
                    component.mouseReleased(mouseX, mouseY, mouseButton);
                }
            }
        }
    }

    public final void mouseWheel(int dWheel)
    {
        if(dWheel != 0 && canInteract() && !MinecraftForge.EVENT_BUS.post(new ComponentMouseEvent.ComponentMouseWheelEvent(this, dWheel))) {
            if(isHovered()) {
                for (IMouseWheelListener wheelListener : wheelListeners) {
                    wheelListener.onMouseWheel(dWheel);
                }
            }

            if(this instanceof GuiPanel) {
                for (GuiComponent component : ((GuiPanel) this).getReversedChildComponents()) {
                    component.mouseWheel(dWheel);
                }
            }
        }
    }

    public void guiOpen()
    {
        if(!MinecraftForge.EVENT_BUS.post(new ComponentStateEvent.ComponentOpenEvent(this))) {
            for(IGuiOpenListener openListener : openListeners) {
                openListener.onGuiOpen();
            }

            if(this instanceof GuiPanel) {
                for(GuiComponent component : ((GuiPanel) this).getReversedChildComponents()) {
                    component.guiOpen();
                }
            }
        }
    }

    public void guiClose() {
        if(!MinecraftForge.EVENT_BUS.post(new ComponentStateEvent.ComponentCloseEvent(this))) {
            for (IGuiCloseListener closeListener : closeListeners) {
                closeListener.onGuiClose();
            }

            if(this instanceof GuiPanel) {
                for(GuiComponent component : ((GuiPanel) this).getReversedChildComponents()) {
                    component.guiClose();
                }
            }
        }
    }

    public boolean isMouseOver(int mouseX, int mouseY)
    {
        return mouseX >= getMinHitboxX() && mouseX < getMaxHitboxX() && mouseY >= getMinHitboxY() && mouseY < getMaxHitboxY();
    }

    public boolean canInteract() {
        return isVisible() && isEnabled();
    }

    public boolean isVisible() {
        return style.isVisible() && (getParent() == null || getParent().isVisible());
    }

    public boolean isEnabled() {
        return enabled && (getParent() == null || getParent().isEnabled());
    }

    public boolean isHovered() {
        return hovered && isVisible() && isEnabled();
    }

    public boolean isPressed() {
        return isVisible() && isEnabled() && pressed;
    }

    public boolean isFocused() {
        return isVisible() && isEnabled() && focused;
    }

    public final GuiComponent<? extends ComponentStyleManager> setFocused(boolean focused)
    {
        if(isFocused() != focused && !MinecraftForge.EVENT_BUS.post(new ComponentStateEvent.ComponentFocusEvent(this))) {
            this.focused = focused;

            if(this instanceof GuiPanel && !focused) {
                for(GuiComponent component : ((GuiPanel) this).getChildComponents()) {
                    if(component.canLooseFocus()) {
                        component.setFocused(false);
                    }
                }
            }
        }

        return this;
    }

    public boolean canLooseFocus() {
        return canLooseFocus;
    }

    public GuiComponent<? extends ComponentStyleManager> setCanLooseFocus(boolean canLooseFocus) {
        this.canLooseFocus = canLooseFocus;
        return this;
    }

    public GuiPanel getParent() {
        return parent;
    }

    public GuiComponent<? extends ComponentStyleManager> setParent(GuiPanel parent) {
        this.parent = parent;
        return this;
    }

    /**
     * @return X position relative to parent component (or to the screen left)
     */
    public int getX() {
        return style.getRenderX();
    }

    /**
     * @return Y position relative to parent component (or to the screen top)
     */
    public int getY() {
        return style.getRenderY();
    }

    public int getWidth() {
        return style.getRenderWidth();
    }

    public int getHeight() {
        return style.getRenderHeight();
    }

    public int getBorderSize() { return style.getBorderSize(); }

    //TODO BETTER IMPLEMENTATION OF THIS
    @Deprecated
    public int getBackgroundSrcBlend() {
        return backgroundSrcBlend;
    }
    @Deprecated
    public GuiComponent<? extends ComponentStyleManager> setBackgroundSrcBlend(int backgroundSrcBlend) {
        this.backgroundSrcBlend = backgroundSrcBlend;
        return this;
    }
    @Deprecated
    public int getBackgroundDstBlend() {
        return backgroundDstBlend;
    }
    @Deprecated
    public GuiComponent<? extends ComponentStyleManager> setBackgroundDstBlend(int backgroundDstBlend) {
        this.backgroundDstBlend = backgroundDstBlend;
        return this;
    }

    public GuiComponent<? extends ComponentStyleManager> setEnabled(boolean enabled) {
        this.enabled = enabled;
        if(!enabled)
        	setHovered(false);
        return this;
    }

    public GuiComponent<? extends ComponentStyleManager> setVisible(boolean visible) {
        style.setVisible(visible);
        return this;
    }

    public GuiComponent<? extends ComponentStyleManager> setHovered(boolean hovered) {
        this.hovered = hovered;
        return this;
    }

    public GuiComponent<? extends ComponentStyleManager> setPressed(boolean pressed) {
        this.pressed = pressed;
        return this;
    }

    public GuiComponent<? extends ComponentStyleManager> setHoveringText(List<String> hoveringText) {
        this.hoveringText = hoveringText;
        return this;
    }

    public GuiComponent<? extends ComponentStyleManager> addClickListener(IMouseClickListener clickListener) {
        this.clickListeners.add(clickListener);
        return this;
    }

    public GuiComponent<? extends ComponentStyleManager> addExtraClickListener(IMouseExtraClickListener extraClickListener) {
        this.extraClickListeners.add(extraClickListener);
        return this;
    }

    public GuiComponent<? extends ComponentStyleManager> addMoveListener(IMouseMoveListener moveListener) {
        this.moveListeners.add(moveListener);
        return this;
    }

    public GuiComponent<? extends ComponentStyleManager> addWheelListener(IMouseWheelListener wheelListener) {
        this.wheelListeners.add(wheelListener);
        return this;
    }

    public GuiComponent<? extends ComponentStyleManager> addKeyboardListener(IKeyboardListener keyboardListener) {
        this.keyboardListeners.add(keyboardListener);
        return this;
    }

    public GuiComponent<? extends ComponentStyleManager> addTickListener(ITickListener tickListener) {
        this.tickListeners.add(tickListener);
        return this;
    }

    public GuiComponent<? extends ComponentStyleManager> addOpenListener(IGuiOpenListener openListener) {
        this.openListeners.add(openListener);
        return this;
    }

    public GuiComponent<? extends ComponentStyleManager> addCloseListener(IGuiCloseListener closeListener) {
        this.closeListeners.add(closeListener);
        return this;
    }

    public GuiComponent<? extends ComponentStyleManager> addResizeListener(IResizeListener resizeListener) {
        this.resizeListeners.add(resizeListener);
        return this;
    }

    public GuiComponent<? extends ComponentStyleManager> addFocusListener(IFocusListener focusListener) {
        this.focusListeners.add(focusListener);
        return this;
    }

    public List<IMouseClickListener> getClickListeners() {
        return clickListeners;
    }

    public List<IMouseExtraClickListener> getExtraClickListeners() {
        return extraClickListeners;
    }

    public List<IMouseMoveListener> getMoveListeners() {
        return moveListeners;
    }

    public List<IMouseWheelListener> getWheelListeners() {
        return wheelListeners;
    }

    public List<IKeyboardListener> getKeyboardListeners() {
        return keyboardListeners;
    }

    public List<ITickListener> getTickListeners() {
        return tickListeners;
    }

    public List<IGuiOpenListener> getOpenListeners() {
        return openListeners;
    }

    public List<IGuiCloseListener> getCloseListeners() {
        return closeListeners;
    }

    public List<IResizeListener> getResizeListeners() {
        return resizeListeners;
    }

    public List<IFocusListener> getFocusListeners() {
        return focusListeners;
    }

    public int getMinHitboxX() {

        if(this instanceof GuiPanel) {

            int renderMinX = getRenderMinX();

            for(GuiComponent component : ((GuiPanel)this).getReversedChildComponents()) {
                if(component.isVisible() && component.getMinHitboxX() < renderMinX){
                    renderMinX = component.getMinHitboxX();
                }
            }

            return renderMinX;
        } else {
            return getRenderMinX();
        }

    }

    public int getMinHitboxY() {

        if(this instanceof GuiPanel) {

            int renderMinY = getRenderMinY();

            for(GuiComponent component : ((GuiPanel)this).getReversedChildComponents()) {
                if(component.isVisible() && component.getMinHitboxY() < renderMinY){
                    renderMinY = component.getMinHitboxY();
                }
            }

            return renderMinY;
        } else {
            return getRenderMinY();
        }

    }

    public int getMaxHitboxX() {

        if(this instanceof GuiPanel) {

            int renderMaxX = getRenderMaxX();

            for(GuiComponent component : ((GuiPanel)this).getReversedChildComponents()) {
                if(component.isVisible() && component.getMaxHitboxX() > renderMaxX){
                    renderMaxX = component.getMaxHitboxX();
                }
            }

            return renderMaxX;
        } else {
            return getRenderMaxX();
        }

    }

    public int getMaxHitboxY() {

        if(this instanceof GuiPanel) {

            int renderMaxY = getRenderMaxY();

            for(GuiComponent component : ((GuiPanel)this).getReversedChildComponents()) {
                if(component.isVisible() && component.getMaxHitboxY() > renderMaxY){
                    renderMaxY = component.getMaxHitboxY();
                }
            }

            return renderMaxY;
        } else {
            return getRenderMaxY();
        }

    }

    /**
     * @return The {@link ComponentStyleManager} of this component
     */
    public T getStyle() {
        return style;
    }

    @Override
    public String toString() {
        return getType()+"{" +
                "cssId='" + cssId + '\'' +
                ", cssClass='" + cssClass + '\'' +
                '}';
    }
}
