package fr.aym.acsguis.component.textarea;

import fr.aym.acsguis.api.GuiAPIClientHelper;
import fr.aym.acsguis.component.EnumComponentType;
import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.style.TextComponentStyleManager;
import fr.aym.acsguis.cssengine.font.CssFontHelper;
import fr.aym.acsguis.cssengine.style.CssTextComponentStyleManager;
import fr.aym.acsguis.event.listeners.IFocusListener;
import fr.aym.acsguis.event.listeners.IKeyboardListener;
import fr.aym.acsguis.event.listeners.ITickListener;
import fr.aym.acsguis.event.listeners.mouse.IMouseClickListener;
import fr.aym.acsguis.event.listeners.mouse.IMouseMoveListener;
import fr.aym.acsguis.event.listeners.mouse.IMouseWheelListener;
import fr.aym.acsguis.utils.ComponentRenderContext;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.List;
import java.util.regex.Pattern;

public class GuiTextArea extends GuiComponent<TextComponentStyleManager> implements ITickListener, IKeyboardListener, IMouseClickListener, IMouseMoveListener, IFocusListener, IMouseWheelListener, TextComponent {
    private String text = "";
    private String hintText = "";

    protected List<String> cachedTextLines;

    /**
     * Used to allow only a certain type of character or pattern
     **/
    protected Pattern regexPattern = Pattern.compile("(?s).*");

    protected int maxTextLength;

    protected int cursorCounter;
    protected int cursorIndex;
    protected int selectionEndIndex;

    protected int lineScrollOffsetX;
    protected int lineScrollOffsetY;

    protected boolean editable;

    protected float textScale = 1;

    public GuiTextArea() {
        this(0, 0, 100, 50);
    }

    public GuiTextArea(int x, int y, int width, int height) {
        super(x, y, width, height);
        setEditable(true);

        setMaxTextLength(200);
        cursorIndex = 0;
        selectionEndIndex = 0;
        lineScrollOffsetX = 0;
        lineScrollOffsetY = 0;

        addTickListener(this);
        addKeyboardListener(this);
        addClickListener(this);
        addMoveListener(this);
        addFocusListener(this);
        addWheelListener(this);
    }

    @Override
    public EnumComponentType getType() {
        return EnumComponentType.TEXT_AREA;
    }

    @Override
    protected TextComponentStyleManager createStyleManager() {
        return new CssTextComponentStyleManager(this) {
            @Override
            public void updateComponentSize(int screenWidth, int screenHeight) {
                super.updateComponentSize(screenWidth, screenHeight);
                clearCachedTextLines();
            }
        };
    }

    public boolean allowLineBreak() {
        return true;
    }

    @Override
    public boolean isInput() {
        return isEditable() || clickListeners.size() > 1 || !extraClickListeners.isEmpty();
    }

    @Override
    public void onTick() {
        this.cursorCounter++;
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTicks, ComponentRenderContext enableScissor) {
        GuiAPIClientHelper.glScissor(getRenderMinX() + getPaddingLeft(), getRenderMinY() + getPaddingTop(), (getRenderMaxX() - getRenderMinX()) - (getPaddingLeft() + getPaddingRight()), (getRenderMaxY() - getRenderMinY()) - (getPaddingTop() + getPaddingBottom()));

        textScale = (float) (getStyle().getFontSize()) / mc.fontRenderer.FONT_HEIGHT;
        GlStateManager.scale(textScale, textScale, 1);
        CssFontHelper.pushDrawing(getStyle().getFontFamily(), getStyle().getEffects());
        if (!getText().isEmpty())
            drawTextLines(getCachedTextLines(), textScale);

        GuiAPIClientHelper.glScissor(getRenderMinX() + getScaledBorderSize(), getRenderMinY() + getScaledBorderSize(), getRenderMaxX() - getRenderMinX() - getScaledBorderSize(), getRenderMaxY() - getRenderMinY() - getScaledBorderSize());
        drawHintLines(textScale);

        if (isEditable() && isFocused()) {
            drawCursor(textScale);
            drawSelectedRegion(textScale);
        }
        GlStateManager.scale(1f / textScale, 1f / textScale, 1);

        /*GlStateManager.disableDepth();
        GlStateManager.disableAlpha();
				Gui.drawRect(0, getRenderMinY(), mc.displayWidth, getRenderMinY()+10, Color.RED.getRGB());
				Gui.drawRect(0, (int) y, mc.displayWidth, (int) (y+1), Color.GREEN.getRGB());
				Gui.drawRect(0, (int) y+getStyle().getFontHeight(line), mc.displayWidth, (int) (y+getStyle().getFontHeight(line)+1), Color.BLUE.getRGB());
				Gui.drawRect(0, (int) getRenderMinY()+getHeight()/2-1, mc.displayWidth, (int) (getRenderMinY()+getHeight()/2+1), Color.PINK.getRGB());
        //Gui.drawRect(0, getRenderMinY(), getMaxLineLength(), getRenderMaxX(), Color.PINK.getRGB());
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();*/
        //TODOOLD VISUAL PLACING DEBUG
        CssFontHelper.popDrawing();

        super.drawForeground(mouseX, mouseY, partialTicks, enableScissor);
    }

    protected void drawTextLines(List<String> lines, float scale) {
        GlStateManager.enableTexture2D();
        String formatting = getStyle().getFontColor() == null ? "" : getStyle().getFontColor().toString();

        for (int i = 0; i < lines.size(); i++) {
            float height = scale * getStyle().getFontHeight(lines.get(i));
            CssFontHelper.draw(((getScreenX() + getPaddingLeft() - getLineScrollOffsetX()) / scale), ((getScreenY() + getPaddingTop() +
                    GuiAPIClientHelper.getRelativeTextY(i, lines.size(), getHeight() - (getPaddingTop() + getPaddingBottom()), getStyle().getVerticalTextAlignment(), height) - getLineScrollOffsetY()) / scale), formatting + lines.get(i), style.getForegroundColor());
        }
    }

    protected void drawHintLines(float scale) {
        if (!isFocused() && text.isEmpty()) {
            GlStateManager.enableTexture2D();
            List<String> hintTextLines = getCachedTextLines();
            if (hintTextLines != null) {
                for (int i = 0; i < hintTextLines.size(); i++) {
                    float height = scale * getStyle().getFontHeight(hintTextLines.get(i));
                    CssFontHelper.draw(((getScreenX() + getPaddingLeft() - getLineScrollOffsetX()) / scale), ((getScreenY() + getPaddingTop() +
                            GuiAPIClientHelper.getRelativeTextY(i, hintTextLines.size(), getHeight() - (getPaddingTop() + getPaddingBottom()), getStyle().getVerticalTextAlignment(), height) - getLineScrollOffsetY()) / scale), hintTextLines.get(i), Color.GRAY.getRGB());
                    //old GuiComponent.mc.fontRenderer.drawString(TextFormatting.ITALIC + hintTextLines.get(i), (getScreenX() + getPaddingLeft())/scale, (getScreenY() + getPaddingTop())/scale + i * 9, Color.GRAY.getRGB(), false);
                }
            }
        }
    }

    @Override
    public void onFocus() {
        cursorCounter = 0;
    }

    @Override
    public void onFocusLoose() {
    }

    protected void drawCursor(float scale) {
        List<String> lines = getCachedTextLines();
        if (cursorCounter / 20 % 2 == 0 && isFocused()) {
            String line = lines.get(getLine(lines, cursorIndex));
            float height = scale * 9; // todo does not supports custom fonts
            float cursorPosX = mc.fontRenderer.getStringWidth(line.substring(0, getPosition(cursorIndex))) * scale - lineScrollOffsetX;
            float cursorPosY = GuiAPIClientHelper.getRelativeTextY(getLine(lines, cursorIndex), lines.size(), getHeight() - (getPaddingTop() + getPaddingBottom()), getStyle().getVerticalTextAlignment(), height) - getLineScrollOffsetY();//(int) (getLine(cursorIndex) * 9 - lineScrollOffsetY);//+ GuiAPIClientHelper.getRelativeTextY(getLine(cursorIndex), getRenderedTextLines().size(), getHeight() - (getPaddingTop() + getPaddingBottom()), getStyle().getVerticalTextAlignment(), 9)); //todo put line height + optimize
            drawRect((int) ((getScreenX() + getPaddingLeft() + cursorPosX) / scale), (int) ((getScreenY() + getPaddingTop() + cursorPosY) / scale), (int) ((getScreenX() + getPaddingLeft() + cursorPosX) / scale + 1), (int) ((getScreenY() + getPaddingTop() + cursorPosY) / scale + 9), Color.WHITE.getRGB());
        }
    }

    protected void drawSelectedRegion(float scale) {
        List<String> lines = getCachedTextLines();
        GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(5387);

        int cursorLine = getLine(lines, cursorIndex);
        int selectionEndLine = getLine(lines, selectionEndIndex);

        int cursorPosition = getPosition(cursorIndex);
        int selectionEndPosition = getPosition(selectionEndIndex);

        for (int i = Math.min(cursorLine, selectionEndLine); i <= Math.max(cursorLine, selectionEndLine); i++) {

            String line = lines.get(i);

            int x1 = 0;
            int x2 = mc.fontRenderer.getStringWidth(line);

            if (i == Math.min(cursorLine, selectionEndLine)) {
                if (cursorLine == selectionEndLine) {
                    x1 = mc.fontRenderer.getStringWidth(line.substring(0, Math.min(cursorPosition, selectionEndPosition)));
                } else if (i == cursorLine) {
                    x1 = mc.fontRenderer.getStringWidth(line.substring(0, cursorPosition));
                } else {
                    x1 = mc.fontRenderer.getStringWidth(line.substring(0, selectionEndPosition));
                }
            }

            if (i == Math.max(cursorLine, selectionEndLine)) {

                if (cursorLine == selectionEndLine) {
                    x2 = mc.fontRenderer.getStringWidth(line.substring(0, Math.max(cursorPosition, selectionEndPosition)));
                } else if (i == cursorLine) {
                    x2 = mc.fontRenderer.getStringWidth(line.substring(0, cursorPosition));
                } else {
                    x2 = mc.fontRenderer.getStringWidth(line.substring(0, selectionEndPosition));
                }

            }
            float height = scale * 9; // todo does not supports custom fonts
            float cursorPosY = GuiAPIClientHelper.getRelativeTextY(i, lines.size(), getHeight() - (getPaddingTop() + getPaddingBottom()), getStyle().getVerticalTextAlignment(), height) - getLineScrollOffsetY();
            drawRect((int) ((getScreenX() + getPaddingLeft() - lineScrollOffsetX) / scale + x1), (int) ((getScreenY() + getPaddingTop() + cursorPosY) / scale), (int) ((getScreenX() + getPaddingLeft() - lineScrollOffsetX) / scale + x2), (int) ((getScreenY() + getPaddingTop() + cursorPosY) / scale + 9), Color.BLUE.getRGB());
        }

        GlStateManager.disableColorLogic();
        GlStateManager.enableTexture2D();
    }

    public void writeText(String text) {
        String part1 = getText().substring(0, Math.min(cursorIndex, selectionEndIndex));
        String part2 = getText().substring(Math.max(cursorIndex, selectionEndIndex));
        setText(part1 + text + part2);

        if (cursorIndex < selectionEndIndex) {
            moveSelectionToCursor();
        } else {
            moveCursorToSelection();
        }
    }

    @Override
    public GuiTextArea setText(String text) {
        if (text.isEmpty() || regexPattern.matcher(text).matches()) {
            this.text = text.substring(0, Math.min(text.length(), maxTextLength));
            clearCachedTextLines();
        } else {
            throw new IllegalStateException(String.format("The text %s doesn't match with the regex %s", text, regexPattern.toString()));
        }
        updateIndexes();
        return this;
    }

    protected void updateIndexes() {
        cursorIndex = MathHelper.clamp(cursorIndex, 0, text.length());
        selectionEndIndex = MathHelper.clamp(selectionEndIndex, 0, text.length());
    }

    protected void moveSelectionToCursor() {
        setSelectionEndIndex(cursorIndex);
    }

    protected void moveCursorToSelection() {
        setCursorIndex(selectionEndIndex);
    }

    protected void moveCursorUp() {
        setCursorIndex(getIndexAtRelativeLine(cursorIndex, -1));
    }

    protected void moveCursorDown() {
        setCursorIndex(getIndexAtRelativeLine(cursorIndex, 1));
    }

    protected void moveSelectionUp() {
        setSelectionEndIndex(getIndexAtRelativeLine(selectionEndIndex, -1));
    }

    protected void moveSelectionDown() {
        setSelectionEndIndex(getIndexAtRelativeLine(selectionEndIndex, 1));
    }

    /**
     * @param index The absolute index
     * @return Return the line, k [0; getRenderedTextLines().size()-1]
     */
    protected int getLine(List<String> lines, int index) {
        int k = 0;
        int l = lines.get(k).length();

        while (index > l && k < lines.size() - 1) {
            k++;
            l += lines.get(k).length();
        }

        return k;
    }

    /**
     * @param index The absolute index
     * @return Return the relative position, k [0; line.length()]
     */
    protected int getPosition(int index) {
        List<String> lines = getCachedTextLines();
        int lineIndex = getLine(lines, index);

        int k = index;

        for (int i = 0; i < lineIndex; i++) {
            k -= lines.get(i).length();
        }

        return k;
    }

    /**
     * @param index The absolute index
     * @param i     The number of line to offset by
     * @return Return the new absolute position at actualLine+i
     */
    protected int getIndexAtRelativeLine(int index, int i) {
        List<String> lines = getCachedTextLines();
        int lineIndex = getLine(lines, index);

        if (lineIndex + i >= 0 && lineIndex + i < lines.size()) {
            int relPosition = getPosition(index);

            int w = mc.fontRenderer.getStringWidth(lines.get(lineIndex).substring(0, relPosition));
            String dstLine = lines.get(lineIndex + i);

            String str0 = mc.fontRenderer.trimStringToWidth(dstLine, w);
            String str1 = dstLine.substring(0, MathHelper.clamp(str0.length() + 1, 0, dstLine.length()));

            int newRelPosition;

            if (Math.abs(w - mc.fontRenderer.getStringWidth(str0)) > Math.abs(w - mc.fontRenderer.getStringWidth(str1))) {
                newRelPosition = str1.length();
            } else {
                newRelPosition = str0.length();
            }

            return getIndexAt(lines, lineIndex + i, newRelPosition);
        }

        return index;
    }


    public void moveCursorBy(int i) {
        setCursorIndex(cursorIndex + i);
    }

    protected void moveSelectionEndBy(int i) {
        setSelectionEndIndex(selectionEndIndex + i);
    }

    public void updateTextOffset() {
        List<String> lines = getCachedTextLines();
        int selectionEndLine = getLine(lines, this.selectionEndIndex);
        int selectionEndPosition = getPosition(this.selectionEndIndex);

        String line = lines.get(selectionEndLine).substring(0, selectionEndPosition);

        int k = mc.fontRenderer.getStringWidth(line) - lineScrollOffsetX;
        int l = (int) (selectionEndLine * textScale * 9 - lineScrollOffsetY);

        if (k <= 0) {
            lineScrollOffsetX += k;
        } else if (k >= getWidth() - (getPaddingLeft() + getPaddingRight())) {
            lineScrollOffsetX += k - (getWidth() - (getPaddingLeft() + getPaddingRight()));
        }

        float height = getHeight();//style.getVerticalSize() == GuiConstants.ENUM_SIZE.RELATIVE ? (int) (style.getRelativeHeight() * getParent().getHeight()) : getHeight();

        if (l <= 0) {
            lineScrollOffsetY += l;
        } else if (l >= height - (getPaddingLeft() + getPaddingRight()) - 9) {
            lineScrollOffsetY += (int) (l - (height - (getPaddingTop() + getPaddingBottom())) + 9);
        }

        if (lineScrollOffsetX >= mc.fontRenderer.getStringWidth(line) && !line.isEmpty()) {
            lineScrollOffsetX = mc.fontRenderer.getStringWidth(line.substring(0, line.length() - 1));
        }
    }

    protected void setSelectionEndIndex(int selectionEndIndex) {
        this.selectionEndIndex = selectionEndIndex;
        updateIndexes();
        updateTextOffset();
    }

    protected void setCursorIndex(int cursorIndex) {
        this.cursorIndex = cursorIndex;
        updateIndexes();
    }

    protected int getIndexFromMouse(int mouseX, int mouseY) {
        List<String> lines = getCachedTextLines();

        int d0 = (int) (MathHelper.clamp(mouseX - (getScreenX() + getPaddingLeft()), 0, Integer.MAX_VALUE) + lineScrollOffsetX);
        float d1 = mouseY - (getScreenY() + getPaddingTop()) + lineScrollOffsetY;

        int lineIndex = MathHelper.clamp((int) (d1 / 9.0), 0, lines.size() - 1);

        String str0 = mc.fontRenderer.trimStringToWidth(lines.get(lineIndex), d0);
        String str1 = lines.get(lineIndex).substring(0, MathHelper.clamp(str0.length() + 1, 0, lines.get(lineIndex).length()));

        int position;

        if (Math.abs(d0 - mc.fontRenderer.getStringWidth(str0)) > Math.abs(d0 - mc.fontRenderer.getStringWidth(str1))) {
            position = str1.length();
        } else {
            position = str0.length();
        }

        return getIndexAt(lines, lineIndex, position);
    }

    protected int getIndexAt(List<String> lines, int line, int position) {
        int index = 0;

        for (int i = 0; i < line; i++) {
            index += lines.get(i).length();
        }

        return index + position;
    }

    public int getNthWordFromCursor(int n) {
        return this.getNthWordFromPos(n, cursorIndex);
    }

    /**
     * Gets the position of the nth word. N may be negative, then it looks backwards. params: N, position
     */
    public int getNthWordFromPos(int n, int position) {
        return this.getWord(n, position);
    }

    public int getWord(int n, int position) {
        int i = position;
        boolean flag = n < 0;
        int j = Math.abs(n);

        for (int k = 0; k < j; ++k) {
            if (!flag) {
                int l = this.text.length();
                i = this.text.indexOf(32, i);

                if (i == -1) {
                    i = l;
                } else {
                    while (i < l && this.text.charAt(i) == 32) {
                        ++i;
                    }
                }
            } else {
                while (i > 0 && this.text.charAt(i - 1) == 32) {
                    --i;
                }

                while (i > 0 && this.text.charAt(i - 1) != 32) {
                    --i;
                }
            }
        }

        return i;
    }

    public String getText() {
        return text;
    }

    /**
     * @return Simple method to return a different rendered text
     * without modifying the actual text.
     */
    protected String getRenderedText() {
        return getText().isEmpty() ? getHintText() : getText();
    }

    public String getSelectedText() {
        return getText().substring(Math.min(cursorIndex, selectionEndIndex), Math.max(cursorIndex, selectionEndIndex));
    }

    public List<String> getCachedTextLines() {
        if (cachedTextLines == null) {
            boolean fontIsBound = CssFontHelper.getBoundFont() != null;
            if (!fontIsBound)
                CssFontHelper.pushDrawing(getStyle().getFontFamily(), getStyle().getEffects());
            cachedTextLines = CssFontHelper.getBoundFont().trimTextToWidth(getRenderedText(), getMaxLineLength(), getMaxTextHeight());
            if (!fontIsBound)
                CssFontHelper.popDrawing();
        }
        return cachedTextLines;
    }

    public void clearCachedTextLines() {
        cachedTextLines = null;
    }

    public static boolean isKeyComboCtrlX(int keyID) {
        return keyID == 45 && GuiScreen.isCtrlKeyDown() && !GuiScreen.isShiftKeyDown();
    }

    public static boolean isKeyComboCtrlV(int keyID) {
        return keyID == 47 && GuiScreen.isCtrlKeyDown() && !GuiScreen.isShiftKeyDown();
    }

    public static boolean isKeyComboCtrlC(int keyID) {
        return keyID == 46 && GuiScreen.isCtrlKeyDown() && !GuiScreen.isShiftKeyDown();
    }

    public static boolean isKeyComboCtrlA(int keyID) {
        return keyID == 30 && GuiScreen.isCtrlKeyDown() && !GuiScreen.isShiftKeyDown();
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        if (!isFocused() || !isEditable()) {
            return;
        }

        cursorCounter = 0;

        if (isKeyComboCtrlA(keyCode)) {
            setCursorIndex(0);
            setSelectionEndIndex(text.length());
        } else if (isKeyComboCtrlC(keyCode)) {
            GuiScreen.setClipboardString(getSelectedText());
        } else if (isKeyComboCtrlV(keyCode)) {
            if (isEnabled() && regexPattern.matcher(GuiScreen.getClipboardString()).matches()) {
                writeText(GuiScreen.getClipboardString());
            }
        } else if (isKeyComboCtrlX(keyCode)) {
            GuiScreen.setClipboardString(getSelectedText());

            if (isEnabled()) {
                writeText("");
            }
        }

        switch (keyCode) {

            case 14:

                //Delete

                if (!getSelectedText().isEmpty()) {
                    writeText("");
                } else {
                    moveCursorBy(-1);
                    writeText("");
                }

                updateTextOffset();

                break;

            case 203:

                //Left arrow

                if (GuiScreen.isShiftKeyDown()) {
                    if (GuiScreen.isCtrlKeyDown()) {
                        setSelectionEndIndex(getNthWordFromPos(-1, selectionEndIndex));
                    } else {
                        moveSelectionEndBy(-1);
                    }

                } else if (GuiScreen.isCtrlKeyDown()) {
                    setCursorIndex(getNthWordFromCursor(-1));
                    moveSelectionToCursor();
                } else {
                    if (!getSelectedText().isEmpty()) {
                        if (selectionEndIndex < cursorIndex) {
                            moveCursorToSelection();
                        } else {
                            moveSelectionToCursor();
                        }
                    } else {
                        moveCursorBy(-1);
                        moveSelectionToCursor();
                    }
                }

                break;

            case 205:

                //Right arrow

                if (GuiScreen.isShiftKeyDown()) {

                    if (GuiScreen.isCtrlKeyDown()) {
                        setSelectionEndIndex(getNthWordFromPos(1, selectionEndIndex));
                    } else {
                        moveSelectionEndBy(1);
                    }

                } else if (GuiScreen.isCtrlKeyDown()) {
                    setCursorIndex(getNthWordFromCursor(1));
                    moveSelectionToCursor();
                } else {
                    if (!getSelectedText().isEmpty()) {
                        if (selectionEndIndex < cursorIndex) {
                            moveSelectionToCursor();
                        } else {
                            moveCursorToSelection();
                        }
                    } else {
                        moveCursorBy(1);
                        moveSelectionToCursor();
                    }
                }

                break;

            case 208:

                //Bottom arrow

                if (GuiScreen.isShiftKeyDown()) {
                    moveSelectionDown();
                } else {
                    moveCursorDown();
                    moveSelectionToCursor();
                }

                break;


            case 200:

                //Top arrow

                if (GuiScreen.isShiftKeyDown()) {
                    moveSelectionUp();
                } else {
                    moveCursorUp();
                    moveSelectionToCursor();
                }

                break;

            case 211:

                if (!getSelectedText().isEmpty()) {
                    writeText("");
                } else {
                    moveSelectionEndBy(1);
                    writeText("");
                }

                break;

            case 28:
            case 156:
                //Enter
                if (allowLineBreak()) {
                    writeText("\n");
                    moveCursorBy(1);
                    moveSelectionToCursor();
                }
                break;
            default:
                if (ChatAllowedCharacters.isAllowedCharacter(typedChar) && isEnabled() && text.length() < maxTextLength) {
                    if (regexPattern.matcher(getRenderedText() + typedChar).matches()) {
                        writeText(String.valueOf(typedChar));
                        moveCursorBy(1);
                        moveSelectionToCursor();
                    }
                }

                break;
        }
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isEditable()) {
            setCursorIndex(getIndexFromMouse(mouseX, mouseY));
            moveSelectionToCursor();
        }
    }

    @Override
    public void onMouseMoved(int mouseX, int mouseY) {
        if (isEditable() && isPressed()) {
            setSelectionEndIndex(getIndexFromMouse(mouseX, mouseY));
        }
    }

    @Override
    public void onMouseWheel(int dWheel) {
        if (isHovered() && isFocused() && isScrollable()) {
            lineScrollOffsetY -= dWheel / 20;
            lineScrollOffsetY = (int) MathHelper.clamp(lineScrollOffsetY, 0, getCachedTextLines().size() * textScale * mc.fontRenderer.FONT_HEIGHT - getHeight());
        }
    }

    @Override
    public void onMouseHover(int mouseX, int mouseY) {
    }

    @Override
    public void onMouseUnhover(int mouseX, int mouseY) {
    }

    public GuiTextArea setHintText(String hintText) {
        this.hintText = hintText;
        if (text.isEmpty()) {
            clearCachedTextLines();
        }
        return this;
    }

    public String getHintText() {
        return hintText;
    }

    public boolean isEditable() {
        return editable;
    }

    public boolean isScrollable() {
        return true;
    }

    public Pattern getRegexPattern() {
        return regexPattern;
    }

    public int getMaxTextLength() {
        return maxTextLength;
    }

    public int getMaxLineLength() {
        return (int) MathHelper.clamp((getWidth() - (getPaddingLeft() + getPaddingRight())) / textScale, 0, Integer.MAX_VALUE);
    }

    /**
     * @return The maximum height of the rendered text, -1 if there is no limit
     */
    public int getMaxTextHeight() {
        return -1;
    }

    public int getPaddingTop() {
        return getStyle().getPaddingTop();
    }

    public int getPaddingBottom() {
        return getStyle().getPaddingBottom();
    }

    public int getPaddingLeft() {
        return getStyle().getPaddingLeft();
    }

    public int getPaddingRight() {
        return getStyle().getPaddingRight();
    }

    public int getLineScrollOffsetX() {
        return lineScrollOffsetX;
    }

    public GuiTextArea setLineScrollOffsetX(int lineScrollOffsetX) {
        this.lineScrollOffsetX = lineScrollOffsetX;
        return this;
    }

    public int getLineScrollOffsetY() {
        return lineScrollOffsetY;
    }

    public GuiTextArea setLineScrollOffsetY(int lineScrollOffsetY) {
        this.lineScrollOffsetY = lineScrollOffsetY;
        return this;
    }

    public GuiTextArea setEditable(boolean editable) {
        this.editable = editable;
        return this;
    }

    public GuiTextArea setRegexPattern(Pattern regexPattern) {
        this.regexPattern = regexPattern;
        return this;
    }

    public GuiTextArea setMaxTextLength(int maxTextLength) {
        this.maxTextLength = maxTextLength;
        return this;
    }
}
