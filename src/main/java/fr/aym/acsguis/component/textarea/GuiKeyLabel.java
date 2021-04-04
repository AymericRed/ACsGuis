package fr.aym.acsguis.component.textarea;

import org.lwjgl.input.Keyboard;

import java.util.List;

public class GuiKeyLabel extends GuiLabel
{
    private int buttonPressed;
    private int keyBind;

    public GuiKeyLabel(int keyBind) {
        super("");
        this.keyBind = keyBind;
        if(keyBind == 0){
            setText("NONE");
        }
    }

    boolean editMode;
    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.onMouseClicked(mouseX, mouseY, mouseButton);
       // buttonPressed= keyBind;
        if(!editMode)
        editMode = true;
        setText(">" + getText() +"<");
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (editMode) {
            /*if (keyCode == 1) {
                this.buttonPressed.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.NONE, 0);
                //this.options.setOptionKeyBinding(this.buttonId, 0);
            } else if (keyCode != 0) {
                this.buttonPressed.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.getActiveModifier(), keyCode);
                //this.options.setOptionKeyBinding(this.buttonId, keyCode);
            } else if (typedChar > 0) {
                this.buttonPressed.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.getActiveModifier(), typedChar + 256);
               // this.options.setOptionKeyBinding(this.buttonId, typedChar + 256);
            }*/
            keyBind = keyCode;
            editMode = false;
            setText(Keyboard.getKeyName(keyCode)+"");
            //KeyBinding.resetKeyBindingArrayAndHash();
        }else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void drawTextLines(List<String> lines, float scale) {
        super.drawTextLines(lines, scale);
        /*for (int i = 0; i < lines.size(); i++) {
            if(editMode){
                lines.add(i, ">"+lines.get(i) +"<");
            }else{
                lines.add(i, keyBind+"");
            }
        }*/
    }

    public int getKeyBind() {
        return keyBind;
    }

    public int getButtonPressed() {
        return buttonPressed;
    }
}
