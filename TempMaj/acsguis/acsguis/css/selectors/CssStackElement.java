package fr.aym.acsguis.cssengine.selectors;

import fr.aym.acsguis.cssengine.style.ComponentStyleManager;
import fr.aym.acsguis.cssengine.style.CssStyleProperty;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperties;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Chooses which style to apply, depending on the state of the element
 */
public class CssStackElement
{
    private final CssStackElement parent;
    private final Map<CompoundCssSelector, Map<EnumCssStyleProperties, CssStyleProperty<?>>> propertyMap;
    /* Not multi-thread compatible */
    private final List<CssStyleProperty<?>> matchingProperties = new ArrayList<>();

    public CssStackElement(CssStackElement parent, Map<CompoundCssSelector, Map<EnumCssStyleProperties, CssStyleProperty<?>>> propertyMap) {
        this.parent = parent;
        this.propertyMap = propertyMap;
        //System.out.println("Property map is "+propertyMap);
    }

    public CssStackElement getParent() {
        return parent;
    }

    public void applyProperty(EnumSelectorContext context, EnumCssStyleProperties property, ComponentStyleManager to) {
        boolean out = false;//to.getOwner() instanceof GuiLabel && property == EnumCssStyleProperties.PADDING_LEFT;//(property == EnumCssStyleProperties.VISIBILITY) && to.getOwner() instanceof GuiButton;
        propertyMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach((e) -> {
            if(out)
                System.out.println("Try stack "+property+" "+context+" to "+to.getOwner()+" "+e.getKey());
            if(e.getKey().applies(to, context))
            {
                CssStyleProperty<?> p = e.getValue().get(property);
                if(out)
                    System.out.println("Able to do it ! Found prop "+p);
                if(p != null) {
                    matchingProperties.add(p);
                }
            }
        });
        if(!matchingProperties.isEmpty()) {
            for (int i = (matchingProperties.size() - 1); i >= 0; i--) {
                if (!matchingProperties.get(i).apply(context, to)) {
                    matchingProperties.clear();
                    return;
                }
                //Else, inherit
            }
            matchingProperties.clear();
        }
        if(out)
            System.out.println("Parents : "+getParent()+" "+to.getParent()+" "+matchingProperties);
        if(getParent() != null && to.getParent() != null && property.inheritable)
        {
            CssStackElement parentStack = this;
            ComponentStyleManager parentTo = to;
            while(parentStack.getParent() != null && parentTo.getParent() != null) {
                parentStack = parentStack.getParent();
                parentTo = parentTo.getParent();
                ComponentStyleManager finalParentTo = parentTo;
                parentStack.propertyMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach((e) -> {
                    if (out)
                        System.out.println("PAR Try stack " + property + " " + context + " to " + finalParentTo.getOwner() + " " + e.getKey());
                    if (e.getKey().applies(finalParentTo, context)) {
                        CssStyleProperty<?> p = e.getValue().get(property);
                        if (out)
                            System.out.println("PAR Able to do it ! Found prop " + p+" for "+to.getOwner());
                        if (p != null) {
                            matchingProperties.add(p);
                        }
                    }
                });
                if(!matchingProperties.isEmpty()) {
                    for (int i = (matchingProperties.size() - 1); i >= 0; i--) {
                        if (!matchingProperties.get(i).apply(context, to)) {
                            matchingProperties.clear();
                            return;
                        }
                        //Else, inherit
                    }
                }
                matchingProperties.clear();
            }
        }
        else
            matchingProperties.clear();
    }

    public void applyAllProperties(EnumSelectorContext context, ComponentStyleManager to) {
        //System.out.println("Prop map is "+propertyMap);
        //Reset
        //if(to.getOwner() instanceof GuiButton)
          //  System.out.println("Set ctx RESET");
        for (EnumCssStyleProperties properties : EnumCssStyleProperties.values()) {
            applyProperty(EnumSelectorContext.NORMAL, properties, to);
        }
        //if(to.getOwner() instanceof GuiButton)
          //  System.out.println("Set ctx "+context);
        if(context != EnumSelectorContext.NORMAL) {
            if(context == EnumSelectorContext.ACTIVE) //TODO proper selector hierarchy system
            {
                for (EnumCssStyleProperties properties : EnumCssStyleProperties.values()) {
                    applyProperty(EnumSelectorContext.HOVER, properties, to);
                }
            }
            //Apply
            for (EnumCssStyleProperties properties : EnumCssStyleProperties.values()) {
                applyProperty(context, properties, to);
            }
        }
    }

    public List<String> getProperties(EnumSelectorContext context, ComponentStyleManager to) {
        List<String> l = new ArrayList<>();
        /*if(parent != null && to.getParent() != null) {
            l.add(TextFormatting.GOLD+"Parent :");
            l.addAll(parent.getProperties(to.getParent().getContext(), to.getParent()));
            l.add(TextFormatting.GOLD+"========");
        }*/
        propertyMap.forEach((sel, props) -> {
            l.add((sel.applies(to, context) ? TextFormatting.GREEN : TextFormatting.RED)+"Selector : ");
            sel.addProperties(l);
            l.add("Properties : ");
            props.forEach((prop, value) -> {
                l.add(prop.key+" = "+value.getValue());
            });
            l.add("========");
        });
        return l;
    }
}
