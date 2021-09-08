/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.aym.acsguis.cssengine.parsing.core.phcss.shorthand;

import fr.aym.acsguis.cssengine.parsing.core.objects.CssProperty;
import fr.aym.acsguis.cssengine.parsing.core.objects.CssStringValue;
import fr.aym.acsguis.cssengine.parsing.core.objects.CssValue;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A single descriptor for a short hand property (like font or border)
 *
 * @author Philip Helger
 * @since 3.7.4
 */
public class CSSShortHandDescriptor {
    private final String m_eProperty;
    private final List<CSSPropertyWithDefaultValue> m_aSubProperties;

    public CSSShortHandDescriptor(@Nonnull final String eProperty,
                                  @Nonnull final CSSPropertyWithDefaultValue... aSubProperties) {
        m_eProperty = eProperty;
        m_aSubProperties = Arrays.asList(aSubProperties);;
    }

    @Nonnull
    public String getProperty() {
        return m_eProperty;
    }

    @Nonnull
    public List<CSSPropertyWithDefaultValue> getAllSubProperties() {
        return m_aSubProperties;
    }

    /**
     * Modify the passed expression members before they are splitted
     *
     * @param aExpressionMembers
     *        The list to be modified. Never <code>null</code> but maybe empty.
     * @return
     */
    protected List<CssValue> modifyExpressionMember(@Nonnull final CssValue aExpressionMember) {
        String name = aExpressionMember.stringValue();
        StringBuilder mot = new StringBuilder();
        byte inArgument = 0;
        List<CssValue> values = new ArrayList<>();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if(inArgument == 0 && c == ' ') {
                values.add(new CssStringValue(mot.toString()));
                mot = new StringBuilder();
            } else if(c == '(') {
                inArgument++;
                mot.append(c);
            } else if(c == ')') {
                inArgument--;
                mot.append(c);
            } else {
                mot.append(c);
            }
        }
        name = mot.toString();
        if(!name.isEmpty()) {
            values.add(new CssStringValue(name));
        }
        return values;
    }

    @Nonnull
    public List<CssProperty> getSplitIntoPieces(@Nonnull final CssProperty aDeclaration) {
        // Check that declaration matches this property
        if (!aDeclaration.getKey().equals(m_eProperty))
            throw new IllegalArgumentException("Cannot split a '" +
                    aDeclaration.getKey() +
                    "' as a '" +
                    m_eProperty +
                    "'");

        // global
        final int nSubProperties = m_aSubProperties.size();
        final List<CssProperty> ret = new ArrayList<>();

        // Modification for margin and padding
        final List<CssValue> aExpressionMembers = modifyExpressionMember(aDeclaration.getValue());
        final int nExpressionMembers = aExpressionMembers.size();
        final boolean[] aHandledSubProperties = new boolean[nSubProperties];

        //System.out.println("Handling shorthand "+m_eProperty+" "+m_aSubProperties+" "+aDeclaration+" "+aExpressionMembers+" "+nExpressionMembers+" "+nSubProperties);

        // For all expression members
        for (int nExprMemberIndex = 0; nExprMemberIndex < nExpressionMembers; ++nExprMemberIndex) {
            final CssValue aMember = aExpressionMembers.get(nExprMemberIndex);

            // For all unhandled sub-properties
            for (int nSubPropIndex = 0; nSubPropIndex < nSubProperties; ++nSubPropIndex)
                if (!aHandledSubProperties[nSubPropIndex]) {
                    final CSSPropertyWithDefaultValue aSubProp = m_aSubProperties.get(nSubPropIndex);
                    final String aProperty = aSubProp.getProperty();
                    final int nMinArgs = aSubProp.getMinArgs();

                    // Always use minimum number of arguments
                    if (nExprMemberIndex + nMinArgs - 1 < nExpressionMembers) {
                        // Build sum of all members
                        /*final StringBuilder aSB = new StringBuilder();
                        for (int k = 0; k < nMinArgs; ++k) {
                            final String sValue = aMember.stringValue();
                            if (aSB.length() > 0)
                                aSB.append(' ');
                            aSB.append(sValue);
                        }*/

                       // An error will be thrown after :) if (aProperty.isValidValue(aSB.toString())) {
                        //FIXME THIS IS A VERY VERY VERY BAD IDEA
                            // We found a match
                            final StringBuilder aExpr = new StringBuilder();
                            for (int k = 0; k < nMinArgs; ++k)
                                aExpr.append(" ").append(aExpressionMembers.get(nExprMemberIndex + k).stringValue());
                            ret.add(new CssProperty(aDeclaration.getSourceLocation(), aProperty, new CssStringValue(aExpr.toString().trim())));
                            nExprMemberIndex += nMinArgs - 1;

                            // Remember as handled
                            aHandledSubProperties[nSubPropIndex] = true;

                            // Next expression member
                            break;
                       // }
                    }
                }
        }

        // Assign all default values that are not present
        for (int nSubPropIndex = 0; nSubPropIndex < nSubProperties; ++nSubPropIndex)
            if (!aHandledSubProperties[nSubPropIndex]) {
                final CSSPropertyWithDefaultValue aSubProp = m_aSubProperties.get(nSubPropIndex);
                // assign default value
                final CssValue aExpr = new CssStringValue(aSubProp.getDefaultValue());
                ret.add(new CssProperty("Default value for "+aDeclaration.getSourceLocation(), aSubProp.getProperty(), aExpr));
            }

        return ret;
    }

    @Override
    public String toString() {
        return "CSSShortHandDescriptor{" +
                "m_eProperty='" + m_eProperty + '\'' +
                ", m_aSubProperties=" + m_aSubProperties +
                '}';
    }
}
