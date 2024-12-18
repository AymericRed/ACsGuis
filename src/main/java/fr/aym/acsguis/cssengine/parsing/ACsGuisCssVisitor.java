package fr.aym.acsguis.cssengine.parsing;

import fr.aym.acsguis.cssengine.font.CssFontStyle;
import fr.aym.acsguis.cssengine.parsing.core.CssException;
import fr.aym.acsguis.cssengine.parsing.core.CssFileVisitor;
import fr.aym.acsguis.cssengine.parsing.core.objects.CssObject;
import fr.aym.acsguis.cssengine.parsing.core.objects.CssProperty;
import fr.aym.acsguis.cssengine.parsing.core.phcss.shorthand.CSSShortHandDescriptor;
import fr.aym.acsguis.cssengine.parsing.core.phcss.shorthand.CSSShortHandRegistry;
import fr.aym.acsguis.cssengine.selectors.CompoundCssSelector;
import fr.aym.acsguis.cssengine.style.CssStyleProperty;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperty;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The css sheet parser
 */
public class ACsGuisCssVisitor implements CssFileVisitor {
    private final ResourceLocation styleSheetName;
    private final Map<CompoundCssSelector, Map<EnumCssStyleProperty, CssStyleProperty<?>>> styleSheet;

    public ACsGuisCssVisitor(ResourceLocation styleSheetName, Map<CompoundCssSelector, Map<EnumCssStyleProperty, CssStyleProperty<?>>> styleSheet) {
        this.styleSheetName = styleSheetName;
        this.styleSheet = styleSheet;
    }

    public void onEndStyleRule(@Nonnull CssObject aStyleRule) throws CssException {
        Map<EnumCssStyleProperty, CssStyleProperty<?>> propertyMap = new HashMap<>();
        for (CssProperty d : aStyleRule.getAllDeclarations()) {
            try {
                if (EnumCssStyleProperty.fromKey(d.getKey()) == null && CSSShortHandRegistry.isShortHandProperty(d.getKey())) {
                    CSSShortHandDescriptor c = CSSShortHandRegistry.getShortHandDescriptor(d.getKey());
                    for (CssProperty d1 : c.getSplitIntoPieces(d)) {
                        //System.out.println("Sub-declaration : " + d1.getKey() + " " + d1.getValue());
                        d1.setSourceLocation(d.getSourceLocation());
                        mapProperty(d1, propertyMap);
                    }
                } else {
                    //System.out.println("Declaration : " + d.getProperty() + " " + d.getValue().stringValue());
                    mapProperty(d, propertyMap);
                }
            } catch (Exception e) {
                throw new CssException("CSS error at " + d.getSourceLocation() + " in " + styleSheetName, e);
            }
        }

        for (CompoundCssSelector selector : aStyleRule.getSelectors()) {
            styleSheet.put(selector, propertyMap);
        }
    }

    private void mapProperty(CssProperty declaration, Map<EnumCssStyleProperty, CssStyleProperty<?>> propertyMap) {
        EnumCssStyleProperty prop = EnumCssStyleProperty.fromKey(declaration.getKey());
        if (prop == null)
            throw new IllegalArgumentException("CSS property " + declaration.getKey() + " at " + declaration.getSourceLocation() + " is not supported !");
        propertyMap.put(prop, new CssStyleProperty<>(prop, declaration.getValue()));
    }

    public void onEndFontFaceRule(@Nonnull CssObject aFontFaceRule) {
        ResourceLocation family = null;
        ResourceLocation url = null;
        String style = "";
        int size = 16;
        for (CssProperty d : aFontFaceRule.getAllDeclarations()) {
            //System.out.println("Propery "+d.getKey()+": "+d.getValue().stringValue());
            switch (d.getKey()) {
                case "font-family":
                    family = new ResourceLocation(d.getValue().stringValue());
                    break;
                case "font-style":
                    style = d.getValue().stringValue();
                    break;
                case "src":
                    if (d.getValue().stringValue().startsWith("url(")) {
                        Pattern p = Pattern.compile("^\\s*url\\(\"(.*)\"\\)$");
                        Matcher m = p.matcher(d.getValue().stringValue());
                        if (m.matches()) {
                            url = new ResourceLocation(m.group(1));
                            break;
                        } else
                            throw new IllegalArgumentException("Invalid url(...) definition " + d.getValue().stringValue());
                    } else
                        throw new IllegalArgumentException("Invalid font src definition " + d.getValue().stringValue());
                case "font-size":
                    size = d.getValue().intValue();
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported font face property " + d.getKey());
            }
        }
        if (family == null)
            throw new IllegalArgumentException("Invalid font face : no font-family defined !");
        if (url == null)
            throw new IllegalArgumentException("Invalid font face : no src defined !");
        ACsGuisCssParser.addFont(family, url, new CssFontStyle(style, size));
    }

    @Override
    public void onObjectComplete(CssObject currentObject) throws CssException {
        //System.out.println("End "+currentObject.getSourceLocation()+" "+currentObject+" n = "+currentObject.getAllDeclarations().size());
        if (currentObject instanceof CssObject.AnnotationObject) {
            onEndFontFaceRule(currentObject);
        } else {
            onEndStyleRule(currentObject);
        }
    }
}
