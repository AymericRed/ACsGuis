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

import fr.aym.acsguis.cssengine.parsing.core.phcss.CCSSValue;
import fr.aym.acsguis.cssengine.parsing.core.phcss.CssColors;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A static registry for all CSS short hand declarations (like
 * <code>border</code> or <code>margin</code>).
 *
 * @author Philip Helger
 * @since 3.7.4
 */
public final class CSSShortHandRegistry {
    private static final Map<String, CSSShortHandDescriptor> s_aMap = new HashMap<>();

    static {
        // Register default short hands
        registerShortHandDescriptor(new CSSShortHandDescriptor("background",
                new CSSPropertyWithDefaultValue(EnumCssStyleProperty.BACKGROUND_COLOR,
                        CCSSValue.TRANSPARENT),
                new CSSPropertyWithDefaultValue(EnumCssStyleProperty.TEXTURE,
                        CCSSValue.NONE),
                new CSSPropertyWithDefaultValue(EnumCssStyleProperty.BACKGROUND_REPEAT,
                        CCSSValue.REPEAT),
                new CSSPropertyWithDefaultValue(EnumCssStyleProperty.BACKGROUND_POSITION,
                        "top left"),
                new CSSPropertyWithDefaultValue(EnumCssStyleProperty.BACKGROUND_ATTACHMENT,
                        CCSSValue.SCROLL),
                new CSSPropertyWithDefaultValue(EnumCssStyleProperty.BACKGROUND_CLIP,
                        CCSSValue.BORDER_BOX),
                new CSSPropertyWithDefaultValue(EnumCssStyleProperty.BACKGROUND_ORIGIN,
                        CCSSValue.PADDING_BOX),
                new CSSPropertyWithDefaultValue(EnumCssStyleProperty.BACKGROUND_SIZE,
                        "auto auto")));
        // Not supported by Firefox 28
        registerShortHandDescriptor(new CSSShortHandDescriptor("font",
                new CSSPropertyWithDefaultValue(EnumCssStyleProperty.FONT_STYLE,
                        CCSSValue.NORMAL),
                                                             /*new CSSPropertyWithDefaultValue(EnumCssStyleProperties.FONT_VARIANT,
                                                                     CCSSValue.NORMAL),
                                                             new CSSPropertyWithDefaultValue(EnumCssStyleProperties.FONT_WEIGHT,
                                                                     CCSSValue.NORMAL),*/
                new CSSPropertyWithDefaultValue(EnumCssStyleProperty.FONT_SIZE,
                        CCSSValue.INHERIT),
                                                             /*new CSSPropertyWithDefaultValue(EnumCssStyleProperties.LINE_HEIGHT,
                                                                     CCSSValue.NORMAL),*/
                new CSSPropertyWithDefaultValue(EnumCssStyleProperty.FONT_FAMILY,
                        CCSSValue.INHERIT)));
        registerShortHandDescriptor(new CSSShortHandDescriptor("border",
                new CSSPropertyWithDefaultValue(EnumCssStyleProperty.BORDER_WIDTH,
                        "3px"),
               /* new CSSPropertyWithDefaultValue(EnumCssStyleProperties.BORDER_STYLE,
                        CCSSValue.SOLID), Only solid in this api, so don't write*/
                new CSSPropertyWithDefaultValue(EnumCssStyleProperty.BORDER_COLOR,
                        CssColors.BLACK.getName())));
    /*registerShortHandDescriptor (new CSSShortHandDescriptor("border-top",
                                                             new CSSPropertyWithDefaultValue(EnumCssStyleProperties.BORDER_TOP_WIDTH,
                                                                     ECSSUnit.px (3)),
                                                             new CSSPropertyWithDefaultValue(EnumCssStyleProperties.BORDER_TOP_STYLE,
                                                                     CCSSValue.SOLID),
                                                             new CSSPropertyWithDefaultValue(EnumCssStyleProperties.BORDER_TOP_COLOR,
                                                                     ECSSColor.BLACK.getName ())));
    registerShortHandDescriptor (new CSSShortHandDescriptor("border-right",
                                                             new CSSPropertyWithDefaultValue(EnumCssStyleProperties.BORDER_RIGHT_WIDTH,
                                                                     ECSSUnit.px (3)),
                                                             new CSSPropertyWithDefaultValue(EnumCssStyleProperties.BORDER_RIGHT_STYLE,
                                                                     CCSSValue.SOLID),
                                                             new CSSPropertyWithDefaultValue(EnumCssStyleProperties.BORDER_RIGHT_COLOR,
                                                                     ECSSColor.BLACK.getName ())));
    registerShortHandDescriptor (new CSSShortHandDescriptor("border-bottom",
                                                             new CSSPropertyWithDefaultValue(EnumCssStyleProperties.BORDER_BOTTOM_WIDTH,
                                                                     ECSSUnit.px (3)),
                                                             new CSSPropertyWithDefaultValue(EnumCssStyleProperties.BORDER_BOTTOM_STYLE,
                                                                     CCSSValue.SOLID),
                                                             new CSSPropertyWithDefaultValue(EnumCssStyleProperties.BORDER_BOTTOM_COLOR,
                                                                     ECSSColor.BLACK.getName ())));
    registerShortHandDescriptor (new CSSShortHandDescriptor("border-left",
                                                             new CSSPropertyWithDefaultValue(EnumCssStyleProperties.BORDER_LEFT_WIDTH,
                                                                     ECSSUnit.px (3)),
                                                             new CSSPropertyWithDefaultValue(EnumCssStyleProperties.BORDER_LEFT_STYLE,
                                                                     CCSSValue.SOLID),
                                                             new CSSPropertyWithDefaultValue(EnumCssStyleProperties.BORDER_LEFT_COLOR,
                                                                     ECSSColor.BLACK.getName ())));
    registerShortHandDescriptor (new CSSShortHandDescriptor("border-width",
                                                             new CSSPropertyWithDefaultValue(EnumCssStyleProperties.BORDER_TOP_WIDTH,
                                                                     ECSSUnit.px (3)),
                                                             new CSSPropertyWithDefaultValue(EnumCssStyleProperties.BORDER_RIGHT_WIDTH,
                                                                     ECSSUnit.px (3)),
                                                             new CSSPropertyWithDefaultValue(EnumCssStyleProperties.BORDER_BOTTOM_WIDTH,
                                                                     ECSSUnit.px (3)),
                                                             new CSSPropertyWithDefaultValue(EnumCssStyleProperties.BORDER_LEFT_WIDTH,
                                                                     ECSSUnit.px (3))));
    registerShortHandDescriptor (new CSSShortHandDescriptorWithAlignment("border-style",
                                                                          new CSSPropertyWithDefaultValue(EnumCssStyleProperties.BORDER_TOP_STYLE,
                                                                                  CCSSValue.SOLID),
                                                                          new CSSPropertyWithDefaultValue(EnumCssStyleProperties.BORDER_RIGHT_STYLE,
                                                                                  CCSSValue.SOLID),
                                                                          new CSSPropertyWithDefaultValue(EnumCssStyleProperties.BORDER_BOTTOM_STYLE,
                                                                                  CCSSValue.SOLID),
                                                                          new CSSPropertyWithDefaultValue(EnumCssStyleProperties.BORDER_LEFT_STYLE,
                                                                                  CCSSValue.SOLID)));
    registerShortHandDescriptor (new CSSShortHandDescriptorWithAlignment("border-color",
                                                                          new CSSPropertyWithDefaultValue(EnumCssStyleProperties.BORDER_TOP_COLOR,
                                                                                  ECSSColor.BLACK.getName ()),
                                                                          new CSSPropertyWithDefaultValue(EnumCssStyleProperties.BORDER_RIGHT_COLOR,
                                                                                  ECSSColor.BLACK.getName ()),
                                                                          new CSSPropertyWithDefaultValue(EnumCssStyleProperties.BORDER_BOTTOM_COLOR,
                                                                                  ECSSColor.BLACK.getName ()),
                                                                          new CSSPropertyWithDefaultValue(EnumCssStyleProperties.BORDER_LEFT_COLOR,
                                                                                  ECSSColor.BLACK.getName ())));
    registerShortHandDescriptor (new CSSShortHandDescriptorWithAlignment("margin",
                                                                          new CSSPropertyWithDefaultValue(EnumCssStyleProperties.MARGIN_TOP,
                                                                                  CCSSValue.AUTO),
                                                                          new CSSPropertyWithDefaultValue(EnumCssStyleProperties.MARGIN_RIGHT,
                                                                                  CCSSValue.AUTO),
                                                                          new CSSPropertyWithDefaultValue(EnumCssStyleProperties.MARGIN_BOTTOM,
                                                                                  CCSSValue.AUTO),
                                                                          new CSSPropertyWithDefaultValue(EnumCssStyleProperties.MARGIN_LEFT,
                                                                                  CCSSValue.AUTO)));*/
        registerShortHandDescriptor(new CSSShortHandDescriptorWithAlignment("padding",
                new CSSPropertyWithDefaultValue(EnumCssStyleProperty.PADDING_TOP,
                        CCSSValue.AUTO),
                new CSSPropertyWithDefaultValue(EnumCssStyleProperty.PADDING_RIGHT,
                        CCSSValue.AUTO),
                new CSSPropertyWithDefaultValue(EnumCssStyleProperty.PADDING_BOTTOM,
                        CCSSValue.AUTO),
                new CSSPropertyWithDefaultValue(EnumCssStyleProperty.PADDING_LEFT,
                        CCSSValue.AUTO)));
    /*registerShortHandDescriptor (new CSSShortHandDescriptor("outline",
                                                             new CSSPropertyWithDefaultValue(EnumCssStyleProperties.OUTLINE_WIDTH,
                                                                     ECSSUnit.px (3)),
                                                             new CSSPropertyWithDefaultValue(EnumCssStyleProperties.OUTLINE_STYLE,
                                                                     CCSSValue.SOLID),
                                                             new CSSPropertyWithDefaultValue(EnumCssStyleProperties.OUTLINE_COLOR,
                                                                     ECSSColor.BLACK.getName ())));
    registerShortHandDescriptor (new CSSShortHandDescriptor("list-style",
                                                             new CSSPropertyWithDefaultValue(EnumCssStyleProperties.LIST_STYLE_TYPE,
                                                                     CCSSValue.DISC),
                                                             new CSSPropertyWithDefaultValue(EnumCssStyleProperties.LIST_STYLE_POSITION,
                                                                     CCSSValue.OUTSIDE),
                                                             new CSSPropertyWithDefaultValue(EnumCssStyleProperties.LIST_STYLE_IMAGE,
                                                                     CCSSValue.NONE)));*/
    }

    private CSSShortHandRegistry() {
    }

    public static void registerShortHandDescriptor(@Nonnull final CSSShortHandDescriptor aDescriptor) {
        final String eProperty = aDescriptor.getProperty();
        if (s_aMap.containsKey(eProperty))
            throw new IllegalStateException("A short hand for property '" +
                    eProperty +
                    "' is already registered!");
        s_aMap.put(eProperty, aDescriptor);
    }

    @Nonnull
    public static Set<String> getAllShortHandProperties() {
        return s_aMap.keySet();
    }

    public static boolean isShortHandProperty(@Nullable final String eProperty) {
        if (eProperty == null)
            return false;
        return s_aMap.containsKey(eProperty);
    }

    @Nullable
    public static CSSShortHandDescriptor getShortHandDescriptor(@Nullable final String eProperty) {
        if (eProperty == null)
            return null;

        return s_aMap.get(eProperty);
    }
}
