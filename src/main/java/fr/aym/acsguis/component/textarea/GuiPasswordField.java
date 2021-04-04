package fr.aym.acsguis.component.textarea;

public class GuiPasswordField extends GuiTextField
{
    @Override
    protected String getRenderedText() {
		StringBuilder builder = new StringBuilder();
	
		for(int i = 0; i < getText().length(); i++) {
			builder.append('*');
		}
		
		return builder.toString();
	}
}
