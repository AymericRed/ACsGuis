package fr.aym.acsguis.cssengine.parsing;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.cssengine.parsing.core.CssException;
import fr.aym.acsguis.cssengine.parsing.core.CssFileVisitor;
import fr.aym.acsguis.cssengine.parsing.core.objects.CssObject;
import fr.aym.acsguis.cssengine.parsing.core.objects.CssProperty;
import fr.aym.acsguis.cssengine.parsing.core.phcss.shorthand.CSSShortHandDescriptor;
import fr.aym.acsguis.cssengine.parsing.core.phcss.shorthand.CSSShortHandRegistry;
import fr.aym.acsguis.cssengine.selectors.CompoundCssSelector;
import fr.aym.acsguis.cssengine.style.CssStyleProperty;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperty;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * The css string parser, used when you use 'setCssCode(code)' on a component
 */
public class ACsGuisStringCssVisitor implements CssFileVisitor
{
    private final GuiComponent component;
    private final Map<CompoundCssSelector, Map<EnumCssStyleProperty, CssStyleProperty<?>>> styleSheet;

    public ACsGuisStringCssVisitor(GuiComponent component, Map<CompoundCssSelector, Map<EnumCssStyleProperty, CssStyleProperty<?>>> styleSheet) {
        this.component = component;
        this.styleSheet = styleSheet;
    }

    public void onEndStyleRule(@Nonnull CssObject aStyleRule) throws CssException {
        try {
            Map<EnumCssStyleProperty, CssStyleProperty<?>> propertyMap = new HashMap<>();
            for(CssProperty d : aStyleRule.getAllDeclarations())
            {
                if(EnumCssStyleProperty.fromKey(d.getKey()) == null && CSSShortHandRegistry.isShortHandProperty(d.getKey())) {
                    CSSShortHandDescriptor c = CSSShortHandRegistry.getShortHandDescriptor(d.getKey());
                    for (CssProperty d1 : c.getSplitIntoPieces(d))
                    {
                        //System.out.println("Sub-declaration : "+d1.getProperty()+" "+d1.getExpression().getAsCSSString());
                        d1.setSourceLocation(d.getSourceLocation());
                        mapProperty(d1, propertyMap);
                    }
                }
                else {
                    //System.out.println("Declaration : " + d.getProperty() + " " + d.getValue().stringValue());
                    mapProperty(d, propertyMap);
                }
            }

            for(CompoundCssSelector selector : aStyleRule.getSelectors())
            {
                styleSheet.put(selector, propertyMap);
            }
        } catch (Exception e) {
            throw new CssException("CSS error at "+aStyleRule.getSourceLocation()+" in "+component, e);
        }
    }

    private void mapProperty(CssProperty declaration, Map<EnumCssStyleProperty, CssStyleProperty<?>> propertyMap) {
        EnumCssStyleProperty prop = EnumCssStyleProperty.fromKey(declaration.getKey());
        if(prop == null)
            throw new IllegalArgumentException("CSS property "+declaration.getKey()+" at "+declaration.getSourceLocation()+" is not supported !");
        propertyMap.put(prop, new CssStyleProperty<>(prop, declaration.getValue()));
    }

    @Override
    public void onObjectComplete(CssObject currentObject) throws CssException {
        //System.out.println("End "+currentObject.getSourceLocation()+" "+currentObject+" n = "+currentObject.getAllDeclarations().size());
        if(currentObject instanceof CssObject.AnnotationObject) {
            throw new IllegalArgumentException("Css font face not allowed here !");
        } else {
            onEndStyleRule(currentObject);
        }
    }
}
