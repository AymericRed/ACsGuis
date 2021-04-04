package fr.aym.acsguis.component.panel;

import fr.aym.acsguis.utils.GuiConstants;
import fr.aym.acsguis.component.EnumComponentType;
import fr.aym.acsguis.component.button.GuiButton;
import fr.aym.acsguis.cssengine.selectors.EnumSelectorContext;
import fr.aym.acsguis.component.style.ComponentStyleManager;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperties;
import fr.aym.acsguis.event.listeners.IFocusListener;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GuiComboBox extends GuiPanel {
	
	protected GuiComboBoxButton guiComboBoxButton;
	
	protected List<String> entries = new ArrayList<String>();
	protected List<GuiEntryButton> entriesButton = new ArrayList<GuiEntryButton>();
	
	protected boolean developed = false;
	
	protected int selectedEntry = -1;
	
	protected String defaultText = "";
	
	public GuiComboBox(String defaultText, List<String> entries) {
		this.defaultText = defaultText;

		style.setBackgroundColor(new Color(0,0,0,0).getRGB());
		
		guiComboBoxButton = new GuiComboBoxButton(defaultText);
		add(guiComboBoxButton);
		
		setEntries(entries);
		setSelectedEntry(-1);
		getStyle().addAutoStyleHandler(this);
	}

	public void setDefaultText(String defaultText) {
		this.defaultText = defaultText;
		if(selectedEntry == -1)
			guiComboBoxButton.setText(defaultText);
	}

	@Override
	public EnumComponentType getType() {
		return EnumComponentType.COMBO_BOX;
	}
	
	public class GuiComboBoxButton extends GuiButton implements IFocusListener {
		
		public GuiComboBoxButton(String defaultText) {
			super(0, 0, 0, 0, defaultText);
			style.getWidth().setRelative(1);
			style.getHeight().setRelative(1);
			addFocusListener(this);
		}
		
		@Override
		public void onMouseClicked(int mouseX, int mouseY, int mouseButton)
		{
			super.onMouseClicked(mouseX, mouseY, mouseButton);
			
			if(!isDeveloped()) {
				developComboBox();
			} else {
				retractComboBox();
			}
		}
		
		@Override public void onFocus() {}
		
		@Override
		public void onFocusLoose() {
			retractComboBox();
		}
	}

	@Override
	public boolean handleProperty(EnumCssStyleProperties property, EnumSelectorContext context, ComponentStyleManager target) {
		if(property == EnumCssStyleProperties.HEIGHT && developed)
		{
			int comboBoxHeight;
			if (guiComboBoxButton.getStyle().getHeight().getValue().type() == GuiConstants.ENUM_SIZE.RELATIVE) {
				comboBoxHeight = (int) (guiComboBoxButton.getParent().getHeight() * guiComboBoxButton.getStyle().getHeight().getRawValue());
			} else {
				comboBoxHeight = guiComboBoxButton.getHeight();
			}
			style.getHeight().setAbsolute(comboBoxHeight + sumEntriesButtonHeight());
			return true;
		}
		return super.handleProperty(property, context, target);
	}

	/**
	 * Unroll the combo box
	 */
	public void developComboBox()
	{
		if(!isDeveloped()) {
			int comboBoxHeight;

			if (guiComboBoxButton.getStyle().getHeight().getValue().type() == GuiConstants.ENUM_SIZE.RELATIVE) {
				comboBoxHeight = (int) (guiComboBoxButton.getParent().getHeight() * guiComboBoxButton.getStyle().getHeight().getRawValue());
			} else {
				comboBoxHeight = guiComboBoxButton.getHeight();
			}

			//style.setHeight(comboBoxHeight + sumEntriesButtonHeight());
			style.refreshCss(false, "combo_deploy");
			guiComboBoxButton.getStyle().getHeight().setAbsolute(comboBoxHeight);
			
			for (GuiEntryButton entryButton : entriesButton) {
				entryButton.getStyle().getYPos().setAbsolute(2 + comboBoxHeight + 12 * entryButton.getEntryId());
				entryButton.setVisible(true);
			}

			style.refreshCss(false, "combo_deploy");
			style.setZLevel(500);
			developed = true;
		}
	}

	/**
	 * Repack the combo box
	 */
	public void retractComboBox()
	{
		if(isDeveloped()) {
			//style.setHeight(getHeight() - sumEntriesButtonHeight());
			guiComboBoxButton.getStyle().getHeight().setRelative(1);
			
			for(GuiEntryButton entryButton : entriesButton) {
				entryButton.setVisible(false);
			}

			style.refreshCss(false, "combo_deploy");
			style.setZLevel(1);
			developed = false;
		}
	}
	
	public GuiComboBox setSelectedEntry(int n)
	{
		if(n >= entries.size() || n < 0) {
			selectedEntry = -1;
			guiComboBoxButton.setText(defaultText);
		} else {
			selectedEntry = n;
			guiComboBoxButton.setText(getEntryButton(n).getText());
		}
		return this;
	}
	
	public GuiComboBox setEntries(List<String> entries)
	{
		for(String entry : this.entries) {
			removeEntry(entry);
		}
		
		if(entries != null) {
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
	
	protected void updateEntriesButtons()
	{
		removeEntriesButtons();
		
		for(String entry : entries) {
			GuiEntryButton entryButton = getNewEntryButton(entries.indexOf(entry), entry);
			entryButton.getStyle().getWidth().setRelative(1);
			entriesButton.add(entryButton);
			
			if(!isDeveloped())
				entryButton.setVisible(false);

			entryButton.setCssClass("combo_button");
			add(entryButton);
		}
	}
	
	public GuiEntryButton getEntryButton(int n) {
		
		for(GuiEntryButton entryButton : entriesButton) {
			if(entryButton.getEntryId() == n) {
				return entryButton;
			}
		}
		
		return null;
	}
	
	protected void removeEntriesButtons()
	{
		for(GuiButton entry : entriesButton) {
			remove(entry);
		}
		
		entriesButton.clear();
	}
	
	protected int sumEntriesButtonHeight() {
		
		int height = 0;
		
		for(GuiEntryButton entryButton : entriesButton) {
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
			super(0, 0, 0, 0, entryName);
			style.getWidth().setRelative(1);
			this.entryId = n;
		}
		
		public int getEntryId() {
			return entryId;
		}
		
		public GuiEntryButton setRelativeHeight(float height) {
			System.out.println("Can't set a relative height to an entry button.");
			return this;
		}
		
		@Override
		public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
			super.onMouseClicked(mouseX, mouseY, mouseButton);
			setSelectedEntry(getEntryId());
			retractComboBox();
		}
		
		@Override
		public int getRenderMinX() {
			return getScreenX();
		}
		
		@Override
		public int getRenderMaxX() {
			return getScreenX() + getWidth();
		}
		
		@Override
		public int getRenderMinY() {
			return getScreenY();
		}
		
		@Override
		public int getRenderMaxY() {
			return getScreenY() + getHeight();
		}
		
	}
	
}
