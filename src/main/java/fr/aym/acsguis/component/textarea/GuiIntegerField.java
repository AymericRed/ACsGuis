package fr.aym.acsguis.component.textarea;

public class GuiIntegerField extends GuiTextField
{
    private int min;
    private int max;

    public GuiIntegerField(int min, int max) {
        super();
        this.min = min;
        this.max = max;
        setText(String.valueOf(0));
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    @Override
    public GuiTextArea setText(String text) {
        if (!text.isEmpty()) {
            try {
                int color = Integer.parseInt(text.equals("-") ? text+"0" : text);
                if (color > getMax()) {
                    this.text = ""+getMax();
                } else if (color < getMin()) {
                    this.text = ""+getMin();
                } else {
                    this.text = text;
                }
            } catch (NumberFormatException ignored) {}
        } else
            this.text = text;
        return this;
    }

    public int getValue() {
        return Integer.parseInt(getText());
    }
}
