package fr.aym.acsguis.cssengine;

import fr.aym.acsguis.component.layout.BorderedGridLayout;
import fr.aym.acsguis.component.layout.FlowLayout;
import fr.aym.acsguis.component.layout.GridLayout;
import fr.aym.acsguis.component.layout.PanelLayout;
import fr.aym.acsguis.cssengine.parsing.core.objects.CssIntValue;
import fr.aym.acsguis.cssengine.parsing.core.objects.CssValue;
import fr.aym.acsguis.cssengine.parsing.core.phcss.CSSColorHelper;
import fr.aym.acsguis.cssengine.parsing.core.phcss.CSSRGB;
import fr.aym.acsguis.cssengine.parsing.core.phcss.CSSRGBA;
import fr.aym.acsguis.cssengine.parsing.core.phcss.CssColors;
import fr.aym.acsguis.utils.GuiTextureSprite;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides parsing shortcuts
 */
public class CssHelper {
    /**
     * Int parser
     */
    public static DefinitionType<Integer> INT = new DefinitionType<>(Integer::parseInt);
    /**
     * String parser
     */
    public static DefinitionType<String> STRING = new DefinitionType<>(s -> s);
    /**
     * Resource location parser
     */
    public static DefinitionType<ResourceLocation> RESOURCE_LOCATION = new DefinitionType<>(CssHelper::parseResourceLocation);
    /**
     * Color parser <br>
     * Supports : <br>
     * <ul>
     *     <li>default css color names</li>
     *     <li>rgba(r,g,b,a)</li>
     *     <li>rgb(r,g,b)</li>
     *     <li>Hex values like #FFFFFF</li>
     *     <li>translucent (alpha is 0)</li>
     * </ul>
     */
    public static DefinitionType<Integer> COLOR = new DefinitionType<>((s) ->
    {
        if (CssColors.isDefaultColorName(s)) {
            return CssHelper.toRGB(CssColors.getFromNameCaseInsensitiveOrNull(s).getAsRGB());
        }
        if (CSSColorHelper.isRGBAColorValue(s)) {
            return CssHelper.toRGBA(CSSColorHelper.getParsedRGBAColorValue(s));
        }
        if (CSSColorHelper.isRGBColorValue(s)) {
            return CssHelper.toRGB(CSSColorHelper.getParsedRGBColorValue(s));
        }
        if (CSSColorHelper.isHexColorValue(s)) {
            int value = ((0xFF) << 24) |
                    Integer.parseInt(s.replace("#", ""), 16);
            return value;
        }
        if (s.equalsIgnoreCase("translucent") || s.equalsIgnoreCase("transparent"))
            return Color.TRANSLUCENT;
        throw new IllegalArgumentException("Unsupported color format : " + s);
    });
    /**
     * Texture sprite parser, see parseTexture method for format
     */
    public static DefinitionType<GuiTextureSprite> TEXTURE_SPRITE = new DefinitionType<>(CssHelper::parseTexture);
    /**
     * Css number parser (e.g. 10px, 50%....)
     */
    public static DefinitionType<CssValue> CSS_INT = new DefinitionType<>(s -> new CssIntValue(Integer.parseInt(s.replace("px", "").trim())));
    //TODO DAT IS BORDEL
    /**
     * Panel layout parser
     */
    public static DefinitionType<PanelLayout<?>> PANEL_LAYOUT = new DefinitionType<>((s) -> {
        String[] data = s.split(" ");
        if (data.length > 2) {
            switch (data[0]) {
                case "flow":
                    return new FlowLayout();
                case "row":
                    return new fr.aym.acsguis.component.layout.GridLayout(-1, Integer.parseInt(data[1]), Integer.parseInt(data[2]), fr.aym.acsguis.component.layout.GridLayout.GridDirection.HORIZONTAL, 1);
                case "line":
                    return new fr.aym.acsguis.component.layout.GridLayout(Integer.parseInt(data[1]), -1, Integer.parseInt(data[2]), fr.aym.acsguis.component.layout.GridLayout.GridDirection.VERTICAL, 1);
                case "grid":
                    if (data.length > 7)
                        return new BorderedGridLayout(Integer.parseInt(data[1]), Integer.parseInt(data[2]), Integer.parseInt(data[3]),
                                data[4].equalsIgnoreCase("vertical") ? fr.aym.acsguis.component.layout.GridLayout.GridDirection.VERTICAL : fr.aym.acsguis.component.layout.GridLayout.GridDirection.HORIZONTAL, Integer.parseInt(data[5]), Integer.parseInt(data[6]), Integer.parseInt(data[7]));
                    else if (data.length > 5)
                        return new fr.aym.acsguis.component.layout.GridLayout(Integer.parseInt(data[1]), Integer.parseInt(data[2]), Integer.parseInt(data[3]),
                                data[4].equalsIgnoreCase("vertical") ? fr.aym.acsguis.component.layout.GridLayout.GridDirection.VERTICAL : GridLayout.GridDirection.HORIZONTAL, Integer.parseInt(data[5]));
                    else
                        throw new IllegalArgumentException("Invalid grid layout definition, at least 3 arguments required");
            }
        }
        throw new IllegalArgumentException("Invalid layout definition, at least 3 arguments required");
    });

    /**
     * Parses a GuiTextureSprite
     *
     * @param from A representation of a GuiTextureSprite : <code>texture(modid:resourcepath, texU, texV, uWidth, vHeight)</code> or <code>url(modid:resourcepath)</code>
     */
    public static GuiTextureSprite parseTexture(String from) {
        if (from.startsWith("texture(")) {
            from = from.replaceAll(" ", "");
            Pattern p = Pattern.compile("^\\s*texture\\(\"(.*)\",([\\d ]*),([\\d ]*),([\\d ]*),([\\d ]*)\\)$");
            Matcher m = p.matcher(from);
            if (m.matches()) {
                return new GuiTextureSprite(new ResourceLocation(m.group(1)),
                        Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4)), Integer.parseInt(m.group(5)));
            } else
                throw new IllegalArgumentException("Invalid texture(...) definition " + from + "\n Must be texture(path, u, v, uWidth, vHeight)");
        } else if (from.startsWith("url(")) {
            from = from.replaceAll(" ", "");
            Pattern p = Pattern.compile("^\\s*url\\(\"(.*)\"\\)$");
            Matcher m = p.matcher(from);
            if (m.matches()) {
                return new GuiTextureSprite(new ResourceLocation(m.group(1)));
            } else throw new IllegalArgumentException("Invalid url(...) definition " + from + "\n Must be url(path)");
        }
        throw new IllegalArgumentException("Invalid image definition " + from);
    }

    /**
     * Parses a ResourceLocation
     *
     * @param from A representation of a ResourceLocation : <code>modid:resourcepath</code> or <code>default</code> to return a null value
     * @return
     */
    public static ResourceLocation parseResourceLocation(String from) {
        if (from.equalsIgnoreCase("default"))
            return null;
        Pattern p = Pattern.compile("^\\s*(.*)$");
        Matcher m = p.matcher(from);
        if (m.matches()) {
            return new ResourceLocation(m.group(1));
        } else throw new IllegalArgumentException("Invalid resource location definition " + from);
    }

    /**
     * @return rgba color value
     */
    public static int toRGBA(CSSRGBA color) {
        int a = Integer.parseInt(color.getOpacity()), r = Integer.parseInt(color.getRed()), g = Integer.parseInt(color.getGreen()), b = Integer.parseInt(color.getBlue());
        testColorValueRange(r, g, b, a);
        int value = ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8) |
                ((b & 0xFF) << 0);
        return value;
    }

    /**
     * @return rgb color value (alpha is 255)
     */
    public static int toRGB(CSSRGB color) {
        int a = 255, r = Integer.parseInt(color.getRed()), g = Integer.parseInt(color.getGreen()), b = Integer.parseInt(color.getBlue());
        testColorValueRange(r, g, b, a);
        int value = ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8) |
                ((b & 0xFF) << 0);
        return value;
    }

    /**
     * Checks the color integer components supplied for validity.
     * Throws an {@link IllegalArgumentException} if the value is out of
     * range.
     *
     * @param r the Red component
     * @param g the Green component
     * @param b the Blue component
     **/
    private static void testColorValueRange(int r, int g, int b, int a) {
        boolean rangeError = false;
        String badComponentString = "";

        if (a < 0 || a > 255) {
            rangeError = true;
            badComponentString = badComponentString + " Alpha";
        }
        if (r < 0 || r > 255) {
            rangeError = true;
            badComponentString = badComponentString + " Red";
        }
        if (g < 0 || g > 255) {
            rangeError = true;
            badComponentString = badComponentString + " Green";
        }
        if (b < 0 || b > 255) {
            rangeError = true;
            badComponentString = badComponentString + " Blue";
        }
        if (rangeError == true) {
            throw new IllegalArgumentException("Color parameter outside of expected range:"
                    + badComponentString);
        }
    }
}
