package fr.aym.acsguis.component.textarea;

import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.panel.GuiScrollPane;
import fr.aym.acsguis.component.textarea.GuiLabel;
import fr.aym.acsguis.component.textarea.GuiTextField;
import net.minecraft.command.CommandBase;

import javax.annotation.Nullable;
import java.util.List;
import java.util.regex.Pattern;

public abstract class GuiSearchField extends GuiPanel
{
	private final GuiTextField field;
	private final GuiScrollPane potentialMatches;
	private List<String> avaibleNames;
	private boolean multiSearch;
	private boolean showPotentialMatches;
	
	public GuiSearchField()
	{
		super();
		setCssClass("search_bar1");
		add(field = new GuiTextField());
		add(potentialMatches = new GuiScrollPane());
		potentialMatches.setVisible(showPotentialMatches=false);
		potentialMatches.setCssId("search_bar_matches");
		
		field.addKeyboardListener((typedChar, keyCode) -> {
			String txt = field.getText();
			if(isMultiSearch() && txt.contains(","))
			{
				String[] temp = txt.split(",");
				txt = temp[temp.length-1];
			}
			List<String> names = CommandBase.getListOfStringsMatchingLastWord(new String[] {txt}, getAvaibleNames());
			names.remove(txt);

			potentialMatches.removeAllChilds();
			potentialMatches.setVisible(showPotentialMatches=(!names.isEmpty() && !txt.isEmpty()));
			int y1 = 0;
			GuiLabel label;
			for(final String name : names)
			{
				potentialMatches.add(label = new GuiLabel(0, y1, getWidth(), 10, name));
				label.setCssClass("search_bar_match");
				label.addClickListener((mouseX, mouseY, mouseButton) -> {
					if(!isMultiSearch() || !field.getText().contains(","))
						field.setText(name);
					else
					{
						field.setText(field.getText().substring(0, field.getText().lastIndexOf(",")+1) + name);
					}
					potentialMatches.removeAllChilds();
					potentialMatches.setVisible(showPotentialMatches=false);
				});
				y1 += 10;
				if(y1 >= 100)
					break;
			}
			potentialMatches.getStyle().refreshCss(false, "search_bar_upd");
		});
	}
	public void setMultiSearch(Pattern pattern, boolean multiSearch) {
		this.multiSearch = multiSearch;
		setRegexPattern(pattern);
	}
	public boolean isMultiSearch() {
		return multiSearch;
	}
	
	public void setRegexPattern(Pattern pattern) {field.setRegexPattern(pattern);}
	@Override
	public int getRenderMaxY()
	{
		return super.getRenderMaxY() + potentialMatches.getHeight();
	}
	@Override
	public boolean isMouseOver(int mouseX, int mouseY) 
	{
		return mouseX >= getScreenX() && mouseX < getScreenX() + getWidth() && mouseY >= getScreenY() && mouseY < getScreenY() + getHeight() + 50;
	}
	public void setAvaibleNames(@Nullable List<String> avaibleNames)
	{
		this.avaibleNames = avaibleNames;
	}
	public List<String> getAvaibleNames() 
	{		
		if(avaibleNames == null)
		{
			setAvaibleNames(generateAvailableNames());
		}
		return avaibleNames;
	}
	public abstract List<String> generateAvailableNames();

	public void setText(String text)
	{
		field.setText(text);
		field.getKeyboardListeners().get(1).onKeyTyped(' ', -70); //Will update the suggestions of results
	}
	public String getText() 
	{
		return field.getText();
	}
	@Override
	public void tick() {
		if(showPotentialMatches)
			getStyle().setZLevel(499);
		else
			getStyle().setZLevel(-20);
		super.tick();
	}
	@Override
	public void drawForeground(int mouseX, int mouseY, float partialTicks)
	{
		super.drawForeground(mouseX, mouseY, partialTicks);
	}
	@Override
	public void guiClose() 
	{
		showPotentialMatches = false;
		super.guiClose();
	}
}
