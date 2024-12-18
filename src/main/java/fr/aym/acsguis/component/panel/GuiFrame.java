package fr.aym.acsguis.component.panel;

import fr.aym.acsguis.api.GuiAPIClientHelper;
import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.button.GuiButton;
import fr.aym.acsguis.component.button.GuiSlider;
import fr.aym.acsguis.component.layout.GridLayout;
import fr.aym.acsguis.component.layout.GuiScaler;
import fr.aym.acsguis.component.style.AutoStyleHandler;
import fr.aym.acsguis.component.style.ComponentStyleManager;
import fr.aym.acsguis.component.style.PanelStyleManager;
import fr.aym.acsguis.component.textarea.GuiLabel;
import fr.aym.acsguis.cssengine.parsing.ACsGuisCssParser;
import fr.aym.acsguis.cssengine.parsing.core.objects.CssValue;
import fr.aym.acsguis.cssengine.style.CssPanelStyleManager;
import fr.aym.acsguis.event.listeners.IKeyboardListener;
import fr.aym.acsguis.utils.ACsScaledResolution;
import fr.aym.acsguis.utils.ComponentRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class GuiFrame extends GuiPanel implements IKeyboardListener {

    /**
     * The instance of the GuiScreen linked to this GuiFrame
     **/
    protected final APIGuiScreen guiScreen;

    public static ACsScaledResolution resolution = new ACsScaledResolution(GuiComponent.mc);
    //MAY BE USED World render public static final Framebuffer worldRenderBuffer = new Framebuffer(GuiComponent.mc.displayWidth, GuiComponent.mc.displayHeight, true);

    protected boolean pauseGame = true;
    protected boolean enableRepeatEvents = true;
    protected boolean escapeQuit = true;
    /**
     * You can disable Minecraft vanilla guiScale scaling here
     */
    protected boolean applyMcScale = true;

    protected GuiType guiType = GuiType.ON_SCREEN;

    /**
     * Either to reload all css sheets when the gui is loaded, only for creating/debugging the gui
     */
    protected boolean needsCssReload = false;
    /**
     * Enables show debug option by pressing 'K' on the keyboard while hovering a component
     */
    protected boolean enableDebugPanel = false;

    public static long lastClickTime;
    public static int mouseX, mouseY;
    public static int mouseButton;
    public static int lastMouseX, lastMouseY;
    public static int lastPressedX, lastPressedY;
    public static List<String> hoveringText;

    public static boolean hasDebugInfo;
    private static GuiScrollPane debugPane;

    public static void setupDebug(ComponentStyleManager parent, List<String> hoveringDebugText) {
        debugPane.getChildComponents().forEach(c -> {
            if (!(c instanceof GuiSlider))
                debugPane.remove(c);
        });
        debugPane.getLayout().clear();
        hoveringDebugText.forEach(s -> debugPane.add(new GuiLabel(s)));
        if (parent != null) {
            debugPane.add(new GuiButton("Parent").addClickListener((x, y, b) -> {
                List<String> debug = new ArrayList<>();
                debug.add(TextFormatting.AQUA + "Parent element : " + parent.getOwner().getType() + " id=" + parent.getOwner().getCssId() + " class=" + parent.getOwner().getCssClass());
                debug.add("-------------");
                debug.addAll(ACsGuisCssParser.getStyleFor(parent).getProperties(parent.getOwner().getState(), parent));
                //debug.add("-------------");
                debug.add(TextFormatting.BLUE + "Auto styles :");
                parent.getAutoStyleHandlers().forEach(h -> {
                    AutoStyleHandler<ComponentStyleManager> hc = (AutoStyleHandler<ComponentStyleManager>) h;
                    debug.add(hc.getPriority(parent) + " " + hc + " " + hc.getModifiedProperties(parent));
                });
                GuiFrame.setupDebug(parent.getParent(), debug);
            }));
        }
        //System.out.println("Pane setuped with "+debugPane.getQueuedComponents()+" "+debugPane.getStyle().getRenderWidth()+" "+debugPane.getStyle().getRenderHeight());
        hasDebugInfo = true;
    }

    public static APIGuiScreen lastOpenedGuiScreen;

    /**
     * @since 22/09/2020 by Aym'
     */
    private final GuiScaler scale;

    public GuiFrame(GuiScaler scale) {
        this(0, 0, scale);
        style.getWidth().setRelative(1, CssValue.Unit.RELATIVE_INT);
        style.getHeight().setRelative(1, CssValue.Unit.RELATIVE_INT);
    }

    public GuiFrame(int width, int height, GuiScaler scale) {
        this(0, 0, width, height, scale);
    }

    public GuiFrame(int x, int y, int width, int height, GuiScaler scale) {
        super(x, y, width, height);
        this.scale = scale;
        this.guiScreen = new APIGuiScreen(this);
        setFocused(true);
        addKeyboardListener(this);

        hasDebugInfo = true;
        debugPane = new GuiScrollPane();
        debugPane.setCssId("css_debug_pane");
        debugPane.setLayout(new GridLayout(-1, 10, 0, GridLayout.GridDirection.HORIZONTAL, 1));
    }

    /**
     * @return The list of css style sheets used by the gui, default style must not be included
     */
    public abstract List<ResourceLocation> getCssStyles();

    /**
     * @return True to add default style sheet to this gui, recommended
     */
    public boolean usesDefaultStyle() {
        return true;
    }

    /**
     * If you return true, you should use the ACsGuiApi class to show your gui, in order to make the reload effective
     *
     * @return True to reload all css sheets when the gui is loaded, only for creating/debugging the gui
     */
    public boolean needsCssReload() {
        return needsCssReload;
    }

    public void setNeedsCssReload(boolean needsCssReload) {
        this.needsCssReload = needsCssReload;
    }

    /**
     * @deprecated Replaced by isEnableDebugPanel()
     */
    @Deprecated
    public boolean allowDebugInGui() {
        return enableDebugPanel;
    }

    /**
     * @return True to enable show debug option by pressing 'K' on the keyboard while hovering a component
     */
    public boolean isEnableDebugPanel() {
        return enableDebugPanel;
    }

    public void setEnableDebugPanel(boolean enableDebugPanel) {
        this.enableDebugPanel = enableDebugPanel;
    }

    @Override
    public boolean isFocused() {
        return true;
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        if (keyCode == 1 && doesEscapeQuit()) {
            GuiComponent.mc.displayGuiScreen(null);
        }
    }

    @Override
    protected void bindLayerBounds() {
        super.bindLayerBounds();
    }

    @Override
    protected void unbindLayerBounds() {
        super.unbindLayerBounds();
    }

    public GuiFrame enableRepeatEvents(boolean enableRepeatEvents) {
        this.enableRepeatEvents = enableRepeatEvents;
        return this;
    }

    /**
     * Directly handles keyboard events from GuiScreen.handleKeyboardInput
     *
     * @param keyPressed If the key is pressed (or released)
     * @param keyCode    The key code
     * @param typedChar  The typed char
     */
    public void onKeyboardEvent(boolean keyPressed, int keyCode, char typedChar) {
        if (allowDebugInGui() && keyCode == Keyboard.KEY_K) {
            hasDebugInfo = false;
            return;
        }
        if (keyCode == 0 && typedChar >= ' ' || keyPressed) {
            this.keyTyped(typedChar, keyCode);
        }
    }

    public GuiScaler getScale() {
        return scale;
    }

    public float getScaledX() {
        return guiScreen.scaleX;
    }

    public float getScaleY() {
        return guiScreen.scaleY;
    }

    @Override
    protected PanelStyleManager createStyleManager() {
        PanelStyleManager s = new CssPanelStyleManager(this) {
            @Override
            public void updateComponentPosition(int screenWidth, int screenHeight) {
                //Re-compute scales after computation of width and height
                float[] scale = getScale().getScale(GuiFrame.resolution, mc.displayWidth, GuiFrame.resolution.getScaledWidth(), getStyle().getRenderWidth(),
                        mc.displayHeight, GuiFrame.resolution.getScaledHeight(), getStyle().getRenderHeight());
                getGuiScreen().scaleX = scale[0];
                getGuiScreen().scaleY = scale[1];

                //And adapt component position (for relative positions, eg : center of the screen)
                //Fix 23.05.23 : don't use given screenWidth and screenHeight because the old scale was applied
                int parentWidth = (int) (GuiFrame.resolution.getScaledWidth() / getGuiScreen().scaleX);
                int parentHeight = (int) (GuiFrame.resolution.getScaledHeight() / getGuiScreen().scaleY);

                computedX = getXPos().computeValue(parentWidth, parentHeight, parentWidth, getRenderWidth());
                computedY = getYPos().computeValue(parentWidth, parentHeight, parentHeight, getRenderHeight());
            }
        };
        s.addAutoStyleHandler(this);
        return s;
    }

    public static boolean doubleClick() {
        return Minecraft.getSystemTime() - GuiFrame.lastClickTime <= 500;
    }

    public static boolean press() {
        return Minecraft.getSystemTime() - GuiFrame.lastClickTime <= 500;
    }

    /**
     * Instance of the GuiScreen linked to this GuiFrame
     */
    public class APIGuiScreen extends GuiScreen {
        private float scaleX = 1, scaleY = 1;
        protected final GuiFrame frame;

        APIGuiScreen(GuiFrame frame) {
            this.frame = frame;
            frame.guiOpen();
        }

        @Override
        public void setWorldAndResolution(Minecraft mc, int width, int height) {
            super.setWorldAndResolution(mc, width, height);
            resolution = new ACsScaledResolution(mc, mc.displayWidth, mc.displayHeight, isApplyMcScale());
            frame.resize(this, width, height);
            debugPane.resize(this, width, height);
            debugPane.updateSlidersVisibility();

            //Needed for scale
            style.update(this);
            debugPane.getStyle().update(this);
            //worldRenderBuffer.createBindFramebuffer(width * resolution.getScaleFactor(), height * resolution.getScaleFactor());
        }

        /**
         * Store the Minecraft's game render in the buffer
         */
		/*private void updateWorldRenderBuffer()
		{
			Framebuffer mcBuffer = mc.getFramebuffer();
			worldRenderBuffer.bindFramebuffer(true);
			mcBuffer.bindFramebufferTexture();
			Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, resolution.getScaledWidth(), resolution.getScaledHeight(), resolution.getScaledWidth(), resolution.getScaledHeight());
			mcBuffer.unbindFramebufferTexture();
			mcBuffer.bindFramebuffer(true);
		}*/
        @Override
        public void updateScreen() {
            frame.tick();
            debugPane.tick();
        }

        @Override
        public void initGui() {
            flushComponentsQueue();
            debugPane.flushComponentsQueue();
            flushRemovedComponents();
            debugPane.flushRemovedComponents();
            Keyboard.enableRepeatEvents(enableRepeatEvents);
            frame.setVisible(true);
        }

        @Override
        public void onGuiClosed() {
            enableRepeatEvents(false);
            frame.setVisible(false);
            frame.guiClose();
            debugPane.guiClose();
            lastOpenedGuiScreen = this;
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            drawScreen(mouseX, mouseY, partialTicks, new ComponentRenderContext(getFrame(), true, guiType));
        }

        public void drawScreen(int mouseX, int mouseY, float partialTicks, ComponentRenderContext renderContext) {
            //updateWorldRenderBuffer();
            hoveringText = null;

            //frame.setHovered(false);

            mouseX /= scaleX;
            mouseY /= scaleY;

            GuiFrame.mouseX = mouseX;
            GuiFrame.mouseY = mouseY;
            if (mouseX != lastMouseX || mouseY != lastMouseY) {
                lastMouseX = mouseX;
                lastMouseY = mouseY;
                frame.mouseMoved(mouseX, mouseY, true);
                if (debugPane.getChildComponents().size() > 2)
                    debugPane.mouseMoved(mouseX, mouseY, true);
            }

            GlStateManager.scale(scaleX, scaleY, 1);
            GuiAPIClientHelper.setCurrentScissorScaling(scaleX, scaleY);
            frame.scale.onApplyScale(scaleX, scaleY);
            frame.render(mouseX, mouseY, partialTicks, renderContext);
            frame.scale.onRemoveScale(scaleX, scaleY);
            GuiAPIClientHelper.resetScissorScaling();
            GL11.glScalef(1 / scaleX, 1 / scaleY, 1);

            mouseX *= scaleX;
            mouseY *= scaleY;

            if (hoveringText != null && !hoveringText.isEmpty())
                GuiAPIClientHelper.drawHoveringText(hoveringText, mouseX, mouseY);

            if (debugPane.getChildComponents().size() > 2)
                debugPane.render(mouseX, mouseY, partialTicks, renderContext);
            //if(hoveringDebugText != null && !hoveringDebugText.isEmpty())
            //	GuiAPIClientHelper.drawHoveringText(hoveringDebugText, mouseX, mouseY);
        }

        @Override
        public void handleMouseInput() throws IOException {
            super.handleMouseInput();
            frame.mouseWheel(Mouse.getEventDWheel());
            if (debugPane.getChildComponents().size() > 2)
                debugPane.mouseWheel(Mouse.getEventDWheel());
        }

        @Override
        public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
            mouseX /= scaleX;
            mouseY /= scaleY;

            frame.mouseClicked(mouseX, mouseY, mouseButton, true);
            if (debugPane.getChildComponents().size() > 2)
                debugPane.mouseClicked(mouseX, mouseY, mouseButton, true);
            GuiFrame.mouseButton = mouseButton;
            lastClickTime = Minecraft.getSystemTime();
            lastPressedX = mouseX;
            lastPressedY = mouseY;
        }

        @Override
        public void mouseReleased(int mouseX, int mouseY, int state) {
            mouseX /= scaleX;
            mouseY /= scaleY;
            frame.mouseReleased(mouseX, mouseY, mouseButton);
            if (debugPane.getChildComponents().size() > 2)
                debugPane.mouseReleased(mouseX, mouseY, mouseButton);
        }

        @Override
        public void handleKeyboardInput() throws IOException {
            frame.onKeyboardEvent(Keyboard.getEventKeyState(), Keyboard.getEventKey(), Keyboard.getEventCharacter());
            this.mc.dispatchKeypresses();
        }

        @Override
        public boolean doesGuiPauseGame() {
            return frame.doesPauseGame();
        }

        public GuiFrame getFrame() {
            return frame;
        }

        public float getScaleX() {
            return scaleX;
        }

        public float getScaleY() {
            return scaleY;
        }
    }

    public boolean doesPauseGame() {
        return pauseGame;
    }

    public GuiFrame setPauseGame(boolean pauseGame) {
        this.pauseGame = pauseGame;
        return this;
    }

    public boolean doesEscapeQuit() {
        return escapeQuit;
    }

    public GuiFrame setEscapeQuit(boolean escapeQuit) {
        this.escapeQuit = escapeQuit;
        return this;
    }

    public boolean isApplyMcScale() {
        return applyMcScale;
    }

    public GuiFrame setApplyMcScale(boolean applyMcScale) {
        this.applyMcScale = applyMcScale;
        return this;
    }

    public GuiType getGuiType() {
        return guiType;
    }

    public void setGuiType(GuiType type) {
        this.guiType = type;
    }

    public APIGuiScreen getGuiScreen() {
        return guiScreen;
    }

    @Override
    public APIGuiScreen getGui() {
        return getGuiScreen();
    }

    public enum GuiType {
        ON_SCREEN,
        OVERLAY,
        IN_WORLD
    }
}
