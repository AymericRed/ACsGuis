package fr.aym.acsguis.cssengine.parsing;

import com.helger.css.decl.*;
import com.helger.css.decl.shorthand.CSSShortHandDescriptor;
import com.helger.css.decl.shorthand.CSSShortHandRegistry;
import com.helger.css.decl.visit.ICSSVisitor;
import com.helger.css.property.ECSSProperty;
import fr.aym.acsguis.cssengine.font.CssFontStyle;
import fr.aym.acsguis.cssengine.selectors.CompoundCssSelector;
import fr.aym.acsguis.cssengine.style.CssStyleProperty;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperties;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The css sheet parser
 */
public class ACsGuisCssVisitor implements ICSSVisitor
{
    private final ResourceLocation styleSheetName;
    private final Map<CompoundCssSelector, Map<EnumCssStyleProperties, CssStyleProperty<?>>> styleSheet;

    public ACsGuisCssVisitor(ResourceLocation styleSheetName, Map<CompoundCssSelector, Map<EnumCssStyleProperties, CssStyleProperty<?>>> styleSheet) {
        this.styleSheetName = styleSheetName;
        this.styleSheet = styleSheet;
    }

    @Override
    public void begin() {}

    @Override
    public void onImport(@Nonnull CSSImportRule aImportRule) {
        throw new UnsupportedOperationException("Import rule");
    }

    @Override
    public void onNamespace(@Nonnull CSSNamespaceRule aNamespaceRule) {
        throw new UnsupportedOperationException("Namespace rule");
    }

    //Style :

    @Override
    public void onDeclaration(@Nonnull CSSDeclaration aDeclaration) {}

    @Override
    public void onBeginStyleRule(@Nonnull CSSStyleRule aStyleRule) {}

    @Override
    public void onStyleRuleSelector(@Nonnull CSSSelector aSelector) {}

    @Override
    public void onEndStyleRule(@Nonnull CSSStyleRule aStyleRule) {
        try {
            Map<EnumCssStyleProperties, CssStyleProperty<?>> propertyMap = new HashMap<>();
            for(CSSDeclaration d : aStyleRule.getAllDeclarations())
            {
                ECSSProperty p = ECSSProperty.getFromNameOrNullHandlingHacks(d.getProperty());
                if(EnumCssStyleProperties.fromKey(d.getProperty())  == null && p != null && CSSShortHandRegistry.isShortHandProperty(ECSSProperty.getFromNameOrNullHandlingHacks(d.getProperty()))) {
                    CSSShortHandDescriptor c = CSSShortHandRegistry.getShortHandDescriptor(ECSSProperty.getFromNameOrNullHandlingHacks(d.getProperty()));
                    for (CSSDeclaration d1 : c.getSplitIntoPieces(d))
                    {
                        //System.out.println("Sub-declaration : "+d1.getProperty()+" "+d1.getExpression().getAsCSSString());
                        d1.setSourceLocation(d.getSourceLocation());
                        mapProperty(d1, propertyMap);
                    }
                }
                else {
                    //System.out.println("Declaration : " + d.getProperty() + " " + d.getExpression().getAsCSSString());
                    mapProperty(d, propertyMap);
                }
            }

            for(CSSSelector s : aStyleRule.getAllSelectors())
            {
                CompoundCssSelector.Builder bu = new CompoundCssSelector.Builder();
                for(ICSSSelectorMember member : s.getAllMembers()) {
                    //System.out.println("Selector member : " + member.getAsCSSString()+" "+member.getClass().getName());
                    if(member instanceof CSSSelectorSimpleMember)
                    {
                        bu.withChild((CSSSelectorSimpleMember) member);
                    }
                    else if(member instanceof ECSSSelectorCombinator)
                    {
                        bu.withCombinator((ECSSSelectorCombinator) member);
                    }
                    else if(member instanceof CSSSelectorAttribute)
                    {
                        bu.withAttribute((CSSSelectorAttribute) member);
                    }
                    else if(member instanceof CSSSelectorMemberNot)
                    {
                        bu.withMemberNot((CSSSelectorMemberNot) member);
                    }
                    else throw new IllegalArgumentException("Unknown CSS member type "+member);
                }
                styleSheet.put(bu.build(), propertyMap);
            }
        } catch (Exception e) {
            throw new RuntimeException("CSS error at "+aStyleRule.getSourceLocation()+" in "+styleSheetName, e);
        }
    }

    private void mapProperty(CSSDeclaration declaration, Map<EnumCssStyleProperties, CssStyleProperty<?>> propertyMap) {
        EnumCssStyleProperties prop = EnumCssStyleProperties.fromKey(declaration.getProperty());
        if(prop == null)
            throw new IllegalArgumentException("CSS property "+declaration.getProperty()+" at "+declaration.getSourceLocation()+" is not supported !");
        propertyMap.put(prop, new CssStyleProperty<>(prop, declaration.getExpression().getAsCSSString()));
    }

    //For printing pages :O

    @Override
    public void onBeginPageRule(@Nonnull CSSPageRule aPageRule) {
        throw new UnsupportedOperationException("Page rule");
    }

    @Override
    public void onBeginPageMarginBlock(@Nonnull CSSPageMarginBlock aPageMarginBlock) {}

    @Override
    public void onEndPageMarginBlock(@Nonnull CSSPageMarginBlock aPageMarginBlock) {}

    @Override
    public void onEndPageRule(@Nonnull CSSPageRule aPageRule) {}

    //For custom fonts : maybe a day !

    @Override
    public void onBeginFontFaceRule(@Nonnull CSSFontFaceRule aFontFaceRule) {}

    @Override
    public void onEndFontFaceRule(@Nonnull CSSFontFaceRule aFontFaceRule) {
        ResourceLocation family = null;
        ResourceLocation url = null;
        String style = "";
        int size = 16;
        for(CSSDeclaration d : aFontFaceRule.getAllDeclarations())
        {
            //System.out.println("Propery "+d.getAsCSSString());
            switch (d.getProperty())
            {
                case "font-family":
                    family = new ResourceLocation(d.getExpression().getAsCSSString());
                    break;
                case "font-style":
                    style = d.getExpression().getAsCSSString();
                    break;
                case "src":
                    if(d.getExpression().getAsCSSString().startsWith("url("))
                    {
                        Pattern p = Pattern.compile("^\\s*url\\((.*)\\)$");
                        Matcher m = p.matcher(d.getExpression().getAsCSSString());
                        if(m.matches())
                        {
                            url = new ResourceLocation(m.group(1));
                            break;
                        }
                        else throw new IllegalArgumentException("Invalid url(...) definition "+d.getExpression().getAsCSSString());
                    }
                    else
                        throw new IllegalArgumentException("Invalid font src definition "+d.getExpression().getAsCSSString());
                case "font-size":
                    size = Integer.parseInt(d.getExpression().getAsCSSString());
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported font face property "+d.getProperty());
            }
        }
        if(family == null)
            throw new IllegalArgumentException("Invalid font face : no font-family defined !");
        if(url == null)
            throw new IllegalArgumentException("Invalid font face : no src defined !");
        ACsGuisCssParser.addFont(family, url, new CssFontStyle(style, size));
    }

    //For different screen sizes : may be interesting

    @Override
    public void onBeginMediaRule(@Nonnull CSSMediaRule aMediaRule) {
        throw new UnsupportedOperationException("Media rule");
    }

    @Override
    public void onEndMediaRule(@Nonnull CSSMediaRule aMediaRule) {}

    //For animations : maybe a day !

    @Override
    public void onBeginKeyframesRule(@Nonnull CSSKeyframesRule aKeyframesRule) {
        throw new UnsupportedOperationException("Keyframes rule");
    }

    @Override
    public void onBeginKeyframesBlock(@Nonnull CSSKeyframesBlock aKeyframesBlock) {}

    @Override
    public void onEndKeyframesBlock(@Nonnull CSSKeyframesBlock aKeyframesBlock) {}

    @Override
    public void onEndKeyframesRule(@Nonnull CSSKeyframesRule aKeyframesRule) {}

    //For different screen sizes : may be interesting

    @Override
    public void onBeginViewportRule(@Nonnull CSSViewportRule aViewportRule) {
        throw new UnsupportedOperationException("Viewport rule");
    }

    @Override
    public void onEndViewportRule(@Nonnull CSSViewportRule aViewportRule) {}

    //Conditional rules : don't care

    @Override
    public void onBeginSupportsRule(@Nonnull CSSSupportsRule aSupportsRule) {
        throw new UnsupportedOperationException("Viewport rule");
    }

    @Override
    public void onEndSupportsRule(@Nonnull CSSSupportsRule aSupportsRule) {}

    @Override
    public void onUnknownRule(@Nonnull CSSUnknownRule aUnknownRule) {
        throw new IllegalArgumentException("Unknown css rule : "+aUnknownRule);
    }

    @Override
    public void end() {}
}
