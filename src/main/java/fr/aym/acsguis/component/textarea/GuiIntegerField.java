package fr.aym.acsguis.component.textarea;

public class GuiIntegerField extends GuiTextField implements NumericComponent {
    private int value;
    private int min;
    private int max;

    public GuiIntegerField(int min, int max) {
        this(min, max, 0);
    }

    public GuiIntegerField(int min, int max, int value) {
        this.min = min;
        this.max = max;
        setValue(value);
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
                int color = Integer.parseInt(text.equals("-") ? text + "0" : text);
                if (color > getMax()) {
                    super.setText(""+getMax());
                } else if (color < getMin()) {
                    super.setText(""+getMin());
                } else {
                    super.setText(text);
                }
                value = Integer.parseInt(text);
            } catch (NumberFormatException ignored) {
            }
        } else {
            super.setText(text);
            value = 0;
        }
        return this;
    }

    public void setValue(int value) {
        this.value = value;
        this.setText("" + value);
    }

    public int getValue() {
        return value;
    }
}
