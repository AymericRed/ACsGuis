package fr.aym.acsguis.cssengine;

import com.helger.css.decl.CSSRGB;
import com.helger.css.decl.CSSRGBA;
import com.helger.css.propertyvalue.CSSSimpleValueWithUnit;
import com.helger.css.utils.CSSColorHelper;
import com.helger.css.utils.CSSNumberHelper;
import com.helger.css.utils.ECSSColor;
import fr.aym.acsguis.utils.GuiTextureSprite;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides parsing shortcuts
 */
public class DnxCssHelper
{
    /**
     * Int parser
     */
    public static DefinitionType<Integer> INT = (DefinitionType<Integer>) DefinitionType.DynamXDefinitionTypes.INT.type;
    /**
     * String parser
     */
    public static DefinitionType<String> STRING = (DefinitionType<String>) DefinitionType.DynamXDefinitionTypes.STRING.type;
    /**
     * Resource location parser
     */
    public static DefinitionType<ResourceLocation> RESOURCE_LOCATION = new DefinitionType<>(DnxCssHelper::parseResourceLocation);
    /**
     * Color parser <br>
     *     Supports : <br>
     *     <ul>
     *         <li>default css color names</li>
     *         <li>rgba(r,g,b,a)</li>
     *         <li>rgb(r,g,b)</li>
     *         <li>Hex values like #FFFFFF</li>
     *         <li>translucent (alpha is 0)</li>
     *     </ul>
     */
    public static DefinitionType<Integer> COLOR = new DefinitionType<>((s) ->
    {
        if(ECSSColor.isDefaultColorName(s))
        {
            return DnxCssHelper.toRGB(ECSSColor.getFromNameCaseInsensitiveOrNull(s).getAsRGB());
        }
        if(CSSColorHelper.isRGBAColorValue(s))
        {
            return DnxCssHelper.toRGBA(CSSColorHelper.getParsedRGBAColorValue(s));
        }
        if(CSSColorHelper.isRGBColorValue(s))
        {
            return DnxCssHelper.toRGB(CSSColorHelper.getParsedRGBColorValue(s));
        }
        if(CSSColorHelper.isHexColorValue(s))
        {
            int value = ((0xFF) << 24) |
                    Integer.parseInt(s.replace("#", ""), 16);
            return value;
        }
        if(s.equalsIgnoreCase("translucent"))
            return Color.TRANSLUCENT;
        throw new IllegalArgumentException("Unsupported color format : "+s);
    });
    /**
     * Texture sprite parser, see parseTexture method for format
     */
    public static DefinitionType<GuiTextureSprite> TEXTURE_SPRITE = new DefinitionType<>(DnxCssHelper::parseTexture);
    /**
     * Css number parser (e.g. 10px, 50%....)
     */
    public static DefinitionType<CSSSimpleValueWithUnit> CSS_INT = new DefinitionType<>(CSSNumberHelper::getValueWithUnit);

    /**
     * Parses a GuiTextureSprite
     *
     * @param from A representation of a GuiTextureSprite : <code>texture(modid:resourcepath, texU, texV, uWidth, vHeight)</code> or <code>url(modid:resourcepath)</code>
     */
    public static GuiTextureSprite parseTexture(String from) {
        if(from.startsWith("texture("))
        {
            Pattern p = Pattern.compile("^\\s*texture\\(\"(.*)\",([\\d ]*),([\\d ]*),([\\d ]*),([\\d ]*)\\)$");
            Matcher m = p.matcher(from);
            if(m.matches())
            {
                return new GuiTextureSprite(new ResourceLocation(m.group(1)),
                        Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4)), Integer.parseInt(m.group(5)));
            }
            else throw new IllegalArgumentException("Invalid texture(...) definition "+from+"\n Must be texture(path, u, v, uWidth, vHeight)");
        }
        else if(from.startsWith("url("))
        {
            Pattern p = Pattern.compile("^\\s*url\\((.*)\\)$");
            Matcher m = p.matcher(from);
            if(m.matches())
            {
                return new GuiTextureSprite(new ResourceLocation(m.group(1)));
            }
            else throw new IllegalArgumentException("Invalid url(...) definition "+from+"\n Must be url(path)");
        }
        throw new IllegalArgumentException("Invalid image definition "+from);
    }

    /**
     * Parses a ResourceLocation
     * @param from A representation of a ResourceLocation : <code>modid:resourcepath</code> or <code>default</code> to return a null value
     * @return
     */
    public static ResourceLocation parseResourceLocation(String from) {
        if(from.equalsIgnoreCase("default"))
            return null;
        Pattern p = Pattern.compile("^\\s*(.*)$");
        Matcher m = p.matcher(from);
        if(m.matches())
        {
            return new ResourceLocation(m.group(1));
        }
        else throw new IllegalArgumentException("Invalid resource location definition "+from);
    }

    /**
     * @return rgba color value
     */
    public static int toRGBA(CSSRGBA color)
    {
        int a = Integer.parseInt(color.getOpacity()), r = Integer.parseInt(color.getRed()), g = Integer.parseInt(color.getGreen()), b = Integer.parseInt(color.getBlue());
        testColorValueRange(r,g,b,a);
        int value = ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8)  |
                ((b & 0xFF) << 0);
        return value;
    }

    /**
     * @return rgb color value (alpha is 255)
     */
    public static int toRGB(CSSRGB color) {
        int a = 255, r = Integer.parseInt(color.getRed()), g = Integer.parseInt(color.getGreen()), b = Integer.parseInt(color.getBlue());
        testColorValueRange(r,g,b,a);
        int value = ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8)  |
                ((b & 0xFF) << 0);
        return value;
    }

    /**
     * Checks the color integer components supplied for validity.
     * Throws an {@link IllegalArgumentException} if the value is out of
     * range.
     * @param r the Red component
     * @param g the Green component
     * @param b the Blue component
     **/
    private static void testColorValueRange(int r, int g, int b, int a) {
        boolean rangeError = false;
        String badComponentString = "";

        if ( a < 0 || a > 255) {
            rangeError = true;
            badComponentString = badComponentString + " Alpha";
        }
        if ( r < 0 || r > 255) {
            rangeError = true;
            badComponentString = badComponentString + " Red";
        }
        if ( g < 0 || g > 255) {
            rangeError = true;
            badComponentString = badComponentString + " Green";
        }
        if ( b < 0 || b > 255) {
            rangeError = true;
            badComponentString = badComponentString + " Blue";
        }
        if ( rangeError == true ) {
            throw new IllegalArgumentException("Color parameter outside of expected range:"
                    + badComponentString);
        }
    }
}
