package fr.aym.acsguis.cssengine.selectors;

import fr.aym.acsguis.component.style.ComponentStyle;
import fr.aym.acsguis.cssengine.parsing.core.objects.CssSelectorCombinator;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Hierarchical css selector : checks for states of parent elements
 */
public class CompoundCssSelector implements Comparable<CompoundCssSelector>
{
    /**
     * The base selector
     */
    private final CssSelector<?> target;
    /**
     * Null if the selector has no parents <br>
     *     Used to check if a parent element must be exactly before the last matched selector
     */
    @Nullable
    private final Boolean[] parentStrict;
    /**
     * Null if the selector has no parents <br>
     *     All (ordered) parent selectors, may not be a strict hierarchy
     */
    @Nullable
    private final CssSelector<?>[] parents;

    /**
     * Id of the selector, used for sorting
     */
    private int id;
    private static int lastId;

    /**
     * Use the {@link Builder} for easier usage of this
     *
     * @param target The base selector of the element to which this selector applies
     * @param parents Null if the selector has no parents <br>
     *      All (ordered) parent selectors, may not be a strict hierarchy
     * @param parentStrict Null if the selector has no parents <br>
     *      Used to check if a parent element must be exactly before the last matched selector
     */
    public CompoundCssSelector(CssSelector<?> target, @Nullable CssSelector<?>[] parents, @Nullable Boolean[] parentStrict) {
        this.target = target;
        this.parents = parents;
        this.parentStrict = parentStrict;
        id = lastId;
        lastId++;
    }

    /**
     * @return The base selector
     */
    public CssSelector<?> getTarget() {
        return target;
    }

    /**
     * First check if target applied to the given css element, then, if required, checks if all parents apply to the parents of the given element
     *
     * @param to The element to test
     * @param targetContext If null, the context is not checked, else checks if it's equals to the context of this selector, or inherits from it (NORMAL is the base context)
     * @return
     */
    public boolean applies(ComponentStyle to, @Nullable EnumSelectorContext targetContext) {
        //Debug
        boolean out = false;//to.getOwner().getCssId() != null && to.getOwner().getCssId().equals("lolt");//to.getOwner() instanceof GuiButton && ((GuiButton)to.getOwner()).getText().equals("Vehicles");// to.getOwner() instanceof TestGui;
        if(out)
            System.out.println("Apply "+this+" to "+to.getOwner());
        if(!target.applies(to.getOwner(), targetContext)) {
            if(out)
                System.out.println("Test apply "+target+" on "+to+" : fail "+to.getOwner());
            return false;
        }
        //Check for parents
        if(parents != null)
        {
            assert parentStrict != null;
            ComponentStyle lastParent = to;
            for(int i=0;i<parents.length;i++)
            {
                if(lastParent.getParent() == null)
                    return false;
                if(parentStrict[i]) {
                    if(out)
                        System.out.println("Strict check bet "+lastParent.getParent().getOwner()+" and "+parents[i]);
                    if (!parents[i].applies(lastParent.getParent().getOwner(), targetContext))
                        return false;
                    if(out)
                        System.out.println("Passed");
                    lastParent = lastParent.getParent();
                }
                else
                {
                    if(out)
                        System.out.println("Lazy check bet "+lastParent.getParent().getOwner()+" and "+parents[i]);
                    while(lastParent.getParent() != null && !parents[i].applies(lastParent.getParent().getOwner(), targetContext))
                    {
                        lastParent = lastParent.getParent();
                    }
                    if(lastParent.getParent() == null)
                        return false;
                }
            }
        }
        if(out)
            System.out.println("Test apply "+this+" on "+to+" : ok ! "+to.getOwner());
        return true;
    }

    @Override
    public String toString() {
        return "{" +
                "target=" + target +
                ", parentStrict=" + Arrays.toString(parentStrict) +
                ", parents=" + Arrays.toString(parents) +
                ", id=" + id +
                '}';
    }

    public void addProperties(List<String> data) {
        data.add(TextFormatting.BLUE+"target="+target);
        if(parents != null)
        {
            data.add(TextFormatting.GOLD+"parents="+Arrays.toString(parents));
        }
        if(parentStrict != null)
        {
            data.add(TextFormatting.GOLD+"parentStrict="+Arrays.toString(parentStrict));
        }
        data.add(TextFormatting.BLUE+"priority="+id);
    }

    @Override
    public int compareTo(CompoundCssSelector o) {
        return Integer.compare(id, o.id);
    }

    /**
     * Helps to create a {@link CompoundCssSelector}
     */
    public static class Builder
    {
        private CssSelector<?> target;
        private Map<CssSelector<?>, Boolean> parents;
        private CssSelectorCombinator nextCombinator;

        /**
         * @return A new {@link CompoundCssSelector} matching with previous inputs
         */
        public CompoundCssSelector build()
        {
            if(target == null) {
                throw new IllegalStateException("Target not set");
            }
            if(parents == null)
                return new CompoundCssSelector(target, null, null);
            else
                return new CompoundCssSelector(target, parents.keySet().toArray(new CssSelector[0]), parents.values().toArray(new Boolean[0]));
        }

        /**
         * Sets the target of this selector, parsing the input {@link CssSelector}
         */
        private void setTarget(CssSelector<?> selector)
        {
            target = selector;
        }

        public void withPseudo(String pseudo) { //Context, see EnumSelectorContext
            if(target == null) {
                throw new NullPointerException("Target not set before setting context (aka pseudo)");
            }
            target.setContext(EnumSelectorContext.fromString(pseudo));
        }

        /**
         * Parses a {@link CssSelector}, and adds its settings
         */
        public void withChild(String sourceLocation, CssSelector<?> member)
        {
            if(target == null) //No target is defined
            {
                //System.out.println("Add "+member.getValue());
                setTarget(member);
            }
            else
            {
                if(nextCombinator != null) //Combine the last target with member
                {
                    //System.out.println("Combine "+member.getValue()+" with "+target+" "+nextCombinator.getName());
                    switch (nextCombinator)
                    {
                        case BLANK:
                            if(parents == null)
                                parents = new HashMap<>();
                            parents.put(target, false); //don't be strict
                            setTarget(member);
                            break;
                        case GREATER:
                            if(parents == null)
                                parents = new HashMap<>();
                            parents.put(target, true); //be strict
                            setTarget(member);
                            break;
                        default: //We don't support all css combinators
                            throw new IllegalArgumentException(nextCombinator.name()+" combinator not supported, at "+sourceLocation);
                    }
                    nextCombinator = null;
                }
                else //Bad css code
                {
                    throw new IllegalArgumentException("AND selector not supported, at "+sourceLocation);
                }
            }
        }

        /**
         * Parses a css combinators, only BLANK and GREATER are supported
         */
        public void withCombinator(CssSelectorCombinator combinator) {
            nextCombinator = combinator;
        }

        /**
         * Unsupported
         */
       /* public void withAttribute(CSSSelectorAttribute attribute) {
            throw new UnsupportedOperationException("CSS selector attribute");
        }*/

        /**
         * Unsupported
         */
        /*public void withMemberNot(CSSSelectorMemberNot attribute) {
            throw new UnsupportedOperationException("CSS selector :not()");
        }*/
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompoundCssSelector)) return false;
        CompoundCssSelector that = (CompoundCssSelector) o;
        return id == that.id && target.equals(that.target) && Arrays.equals(parentStrict, that.parentStrict) && Arrays.equals(parents, that.parents);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(target, id);
        result = 31 * result + Arrays.hashCode(parentStrict);
        result = 31 * result + Arrays.hashCode(parents);
        return result;
    }
}
