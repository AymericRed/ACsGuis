package fr.aym.acsguis.component.textarea;

public class GuiPasswordField extends GuiTextField {
	
	public GuiPasswordField(int x, int y, int width, int height) {
		super(x, y, width, height);
	}
    
    @Override
    protected String getRenderedText() {
		StringBuilder builder = new StringBuilder();
	
		for(int i = 0; i < getText().length(); i++) {
			builder.append('*');
		}
		
		return builder.toString();
	}

}
