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

import fr.aym.acsguis.cssengine.parsing.core.objects.CssIntValue;
import fr.aym.acsguis.cssengine.parsing.core.objects.CssValue;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A special {@link CSSShortHandDescriptor} implementation for margin and
 * padding as well as for border-color.
 *
 * @author Philip Helger
 */
public class CSSShortHandDescriptorWithAlignment extends CSSShortHandDescriptor {
    public CSSShortHandDescriptorWithAlignment(@Nonnull final String eProperty,
                                               @Nonnull final CSSPropertyWithDefaultValue... aSubProperties) {
        super(eProperty, aSubProperties);
    }

    @Override
    protected List<CssValue> modifyExpressionMember(@Nonnull final CssValue member) {
        //System.out.println("STRINGIZE "+member+" is "+member.stringValue());
        List<String> aExpressionMembers = Arrays.asList(member.stringValue().split(" "));
        List<CssValue> newMembers = new ArrayList<>();
        final int nSize = aExpressionMembers.size();
        if (nSize != 2) {
            // 4px -> 4px 4px 4px 4px
            final String aMember = aExpressionMembers.get(0).replace("px", ""); //TODO IMPROVE
            for (int i = 0; i < 4; ++i)
                newMembers.add(new CssIntValue(Integer.parseInt(aMember)));
        } else {
            // 4px 10px -> 4px 10px 4px 10px
            final String aMemberY = aExpressionMembers.get(0).replace("px", "");
            final String aMemberX = aExpressionMembers.get(1).replace("px", "");
            newMembers.add(new CssIntValue(Integer.parseInt(aMemberY)));
            newMembers.add(new CssIntValue(Integer.parseInt(aMemberX)));
            newMembers.add(new CssIntValue(Integer.parseInt(aMemberY)));
            newMembers.add(new CssIntValue(Integer.parseInt(aMemberX)));
        }
        // else nothing to do
        return newMembers;
    }
}
