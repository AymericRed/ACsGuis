package fr.aym.acsguis.cssengine.font;

/**
 * Special style properties of a font <br>
 *     Only size, bold an italic are supported for the moment
 */
public class CssFontStyle
{
    private final String style;
    private final int size;

    public CssFontStyle(String style, int size) {
        this.style = style;
        this.size = size;
    }

    public String getStyle() {
        return style;
    }

    public boolean isBold() {
        return style.equals("bold");
    }

    public boolean isItalic() {
        return style.equals("italic");
    }

    /**
     * @return Font size, default is 16
     */
    public int getSize() {
        return size;
    }
}
