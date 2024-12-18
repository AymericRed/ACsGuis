package fr.aym.acsguis.component.panel;

import fr.aym.acsguis.component.EnumComponentType;
import fr.aym.acsguis.component.button.GuiButton;
import fr.aym.acsguis.component.style.InternalComponentStyle;
import fr.aym.acsguis.component.textarea.TextComponent;
import fr.aym.acsguis.cssengine.parsing.core.objects.CssValue;
import fr.aym.acsguis.cssengine.selectors.EnumSelectorContext;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperty;
import fr.aym.acsguis.event.listeners.IFocusListener;
import fr.aym.acsguis.utils.GuiConstants;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GuiComboBox extends GuiPanel implements TextComponent {
    protected GuiComboBoxButton guiComboBoxButton;

    protected List<String> entries = new ArrayList<String>();
    protected List<GuiEntryButton> entriesButton = new ArrayList<GuiEntryButton>();

    protected boolean developed = false;

    protected int selectedEntry = -1;

    protected String defaultText = "";

    public GuiComboBox(String defaultText, List<String> entries) {
        this.defaultText = defaultText;
        getStyleCustomizer().withAutoStyle(EnumCssStyleProperty.BACKGROUND_COLOR, t -> t.setBackgroundColor(new Color(0, 0, 0, 0).getRGB()));

        guiComboBoxButton = new GuiComboBoxButton(defaultText);
        add(guiComboBoxButton);

        setEntries(entries);
        setSelectedEntry(-1);
    }

    public void setDefaultText(String defaultText) {
        this.defaultText = defaultText;
        if (selectedEntry == -1)
            guiComboBoxButton.setText(defaultText);
    }

    @Override
    public EnumComponentType getType() {
        return EnumComponentType.COMBO_BOX;
    }

    @Override
    public String getText() {
        return guiComboBoxButton.getText();
    }

    @Override
    public TextComponent setText(String text) {
        return guiComboBoxButton.setText(text);
    }

    public class GuiComboBoxButton extends GuiButton implements IFocusListener {
        public GuiComboBoxButton(String defaultText) {
            super(defaultText);
            getStyleCustomizer().withAutoStyle(EnumCssStyleProperty.WIDTH, t -> t.getWidth().setRelative(1, CssValue.Unit.RELATIVE_TO_PARENT));
            getStyleCustomizer().withAutoStyle(EnumCssStyleProperty.HEIGHT, t -> t.getHeight().setRelative(1, CssValue.Unit.RELATIVE_TO_PARENT));
            addFocusListener(this);
        }

        @Override
        public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
            super.onMouseClicked(mouseX, mouseY, mouseButton);
            if (!isDeveloped()) {
                developComboBox();
            } else {
                retractComboBox();
            }
        }

        @Override
        public void onFocus() {
        }

        @Override
        public void onFocusLoose() {
            retractComboBox();
        }
    }

    @Override
    public boolean handleProperty(EnumCssStyleProperty property, EnumSelectorContext context, InternalComponentStyle target) {
        if (property == EnumCssStyleProperty.HEIGHT && developed) {
            float comboBoxHeight = guiComboBoxButton.getStyle().getHeight().computeValue(0, 0, guiComboBoxButton.getParent().getHeight());
            target.getHeight().setAbsolute(comboBoxHeight + sumEntriesButtonHeight());
            return true;
        }
        return super.handleProperty(property, context, target);
    }

    /**
     * Unroll the combo box
     */
    public void developComboBox() {
        if (!isDeveloped()) {
            float comboBoxHeight;

            if (guiComboBoxButton.getStyle().getHeight().getValue().type() == GuiConstants.ENUM_SIZE.RELATIVE) {
                comboBoxHeight = guiComboBoxButton.getParent().getHeight() * guiComboBoxButton.getStyle().getHeight().getRawValue();
            } else {
                comboBoxHeight = guiComboBoxButton.getHeight();
            }

            //style.setHeight(comboBoxHeight + sumEntriesButtonHeight());
            //getStyle().refreshCss(getGui(), false);

            //TODO USE AUTO STYLES FOR THIS
            guiComboBoxButton.getStyleCustomizer().setHeight(comboBoxHeight);
            for (GuiEntryButton entryButton : entriesButton) {
                entryButton.getStyleCustomizer().setYPos(2 + comboBoxHeight + 12 * entryButton.getEntryId());
                ((InternalComponentStyle) entryButton.getStyle()).setVisible(true);
            }
            getStyleCustomizer().setZLevel(500);
            developed = true;
        }
    }

    /**
     * Repack the combo box
     */
    public void retractComboBox() {
        if (isDeveloped()) {
            //style.setHeight(getHeight() - sumEntriesButtonHeight());
            //TODO USE AUTO STYLES FOR THIS
            guiComboBoxButton.getStyleCustomizer().setRelativeHeight(1, CssValue.Unit.RELATIVE_TO_PARENT);
            for (GuiEntryButton entryButton : entriesButton) {
                ((InternalComponentStyle) entryButton.getStyle()).setVisible(false);
            }
            getStyleCustomizer().setZLevel(1);
            developed = false;
        }
    }

    public GuiComboBox setSelectedEntry(int n) {
        if (n >= entries.size() || n < 0) {
            selectedEntry = -1;
            guiComboBoxButton.setText(defaultText);
        } else {
            selectedEntry = n;
            guiComboBoxButton.setText(getEntryButton(n).getText());
        }
        return this;
    }

    public GuiComboBox setEntries(List<String> entries) {
        for (String entry : this.entries) {
            removeEntry(entry);
        }

        if (entries != null) {
            for (String entry : entries) {
                addEntry(entry);
            }
        }
        return this;
    }

    public void addEntry(String entry) {
        entries.add(entry);
        updateEntriesButtons();
    }

    public void removeEntry(String entry) {
        entries.remove(entry);
        updateEntriesButtons();
    }

    protected void updateEntriesButtons() {
        removeEntriesButtons();

        for (String entry : entries) {
            GuiEntryButton entryButton = getNewEntryButton(entries.indexOf(entry), entry);
            entryButton.getStyleCustomizer().setRelativeWidth(1, CssValue.Unit.RELATIVE_TO_PARENT);
            entriesButton.add(entryButton);

            if (!isDeveloped())
                ((InternalComponentStyle) entryButton.getStyle()).setVisible(false);

            entryButton.setCssClass("combo_button");
            add(entryButton);
        }
    }

    public GuiEntryButton getEntryButton(int n) {

        for (GuiEntryButton entryButton : entriesButton) {
            if (entryButton.getEntryId() == n) {
                return entryButton;
            }
        }

        return null;
    }

    protected void removeEntriesButtons() {
        for (GuiButton entry : entriesButton) {
            remove(entry);
        }

        entriesButton.clear();
    }

    protected int sumEntriesButtonHeight() {

        int height = 0;

        for (GuiEntryButton entryButton : entriesButton) {
            height += entryButton.getHeight();
        }

        return height;

    }

    public List<String> getEntries() {
        return entries;
    }

    public int getSelectedEntry() {
        return selectedEntry;
    }

    public boolean isDeveloped() {
        return developed;
    }

    public GuiEntryButton getNewEntryButton(int n, String entry) {
        return new GuiBasicEntryButton(n, entry);
    }

    public class GuiBasicEntryButton extends GuiEntryButton {
        public GuiBasicEntryButton(int n, String entryName) {
            super(n, entryName);
            setBackgroundSrcBlend(GL11.GL_DST_COLOR);
            setBackgroundDstBlend(GL11.GL_ZERO);
            //TODO BLEND effects
        }
    }

    public abstract class GuiEntryButton extends GuiButton {

        protected int entryId;

        public GuiEntryButton(int n, String entryName) {
            super(entryName);
            getStyleCustomizer().withAutoStyle(EnumCssStyleProperty.WIDTH, t -> t.getWidth().setRelative(1, CssValue.Unit.RELATIVE_TO_PARENT));
            this.entryId = n;
        }

        public int getEntryId() {
            return entryId;
        }

        @Override
        public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
            super.onMouseClicked(mouseX, mouseY, mouseButton);
            setSelectedEntry(getEntryId());
            retractComboBox();
        }

        @Override
        public float getRenderMinX() {
            return getScreenX();
        }

        @Override
        public float getRenderMaxX() {
            return getScreenX() + getWidth();
        }

        @Override
        public float getRenderMinY() {
            return getScreenY();
        }

        @Override
        public float getRenderMaxY() {
            return getScreenY() + getHeight();
        }

    }

}
