package fr.aym.acsguis.cssengine.parsing;

import com.helger.commons.io.IHasInputStream;
import com.helger.css.ECSSVersion;
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.decl.visit.CSSVisitor;
import com.helger.css.decl.visit.ICSSVisitor;
import com.helger.css.reader.CSSReader;
import fr.aym.acsguis.api.ACsGuiApi;
import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.cssengine.font.CssFontStyle;
import fr.aym.acsguis.cssengine.font.ICssFont;
import fr.aym.acsguis.cssengine.font.McFontRenderer;
import fr.aym.acsguis.cssengine.font.TtfFontRenderer;
import fr.aym.acsguis.cssengine.selectors.CompoundCssSelector;
import fr.aym.acsguis.cssengine.selectors.CssStackElement;
import fr.aym.acsguis.component.style.ComponentStyleManager;
import fr.aym.acsguis.cssengine.style.CssStyleProperty;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * This class handles loading of all loaded css content, and keeps it in cache
 */
public class ACsGuisCssParser
{
    /**
     * Holds all css properties, sorted by sheet name, selector and property type
     */
    private static final Map<ResourceLocation, Map<CompoundCssSelector, Map<EnumCssStyleProperties, CssStyleProperty<?>>>> cssStyleSheets = new HashMap<>();
    /**
     * Holds all css fonts, sorted by name
     */
    private static final Map<ResourceLocation, ICssFont> fonts = new HashMap<>();

    /**
     * Default built-in style sheet
     */
    public static final ResourceLocation DEFAULT_STYLE_SHEET = new ResourceLocation(ACsGuiApi.RES_LOC_ID, "css/default_style.css");
    /**
     * Default minecraft font
     */
    public static final ICssFont DEFAULT_FONT = new McFontRenderer();

    /**
     * Used before reloading css sheets
     */
    public static void clearFonts() {
        fonts.clear();
    }

    /**
     * Loads fonts, must be called in client thread
     */
    public static void loadFonts(ICssFont.FontReloadOrigin reloadOrigin) {
        fonts.put(null, DEFAULT_FONT);
        IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
        for (Map.Entry<ResourceLocation, ICssFont> entry : fonts.entrySet()) {
            ResourceLocation l = entry.getKey();
            ICssFont f = entry.getValue();
            try {
                f.load(manager);
            } catch (Exception e) {
                reloadOrigin.handleFontException(l, e);
            }
        }
    }

    /**
     * Registers a font
     *
     * @param family Font name (in css code)
     * @param location Font file location, for the moment, only .ttf file are supported
     * @param style Custom font style, as specified in css code, see {@link CssFontStyle}
     */
    public static void addFont(ResourceLocation family, ResourceLocation location, CssFontStyle style) {
        if(fonts.containsKey(family))
            throw new IllegalStateException("Font "+family+" is already registered !");
        if(location.getPath().endsWith(".ttf")) {
            fonts.put(family, new TtfFontRenderer(location, style));
        }
        else if(location.getPath().endsWith(".png")) {
            throw new UnsupportedOperationException("Png fonts will be supported soon !");
        }
        else {
            throw new UnsupportedOperationException("Unsupported font format for "+location);
        }
    }

    /**
     * Returns the desired font, or default one
     */
    public static ICssFont getFont(@Nullable ResourceLocation location) {
        return fonts.getOrDefault(location, DEFAULT_FONT);
    }

    /**
     * Read a CSS 3.0 declaration from a file using UTF-8 encoding.
     */
    public static void parseCssSheet(ResourceLocation location) {
        // UTF-8 is the fallback if neither a BOM nor @charset rule is present
        final CascadingStyleSheet aCSS = CSSReader.readFromStream(new IHasInputStream() {
            @Override
            public InputStream getInputStream() {
                try {
                    return getResource(location);
                } catch (Exception e) {
                    throw new RuntimeException("Cannot load css resource "+location, e);
                }
            }
            @Override
            public boolean isReadMultiple() {
                return false;
            }
        }, StandardCharsets.UTF_8, ECSSVersion.CSS30);
        if (aCSS == null)
        {
            // Most probably a syntax error
            throw new IllegalStateException("[CSS] Failed to read CSS file "+location+" - please see previous logging entries !");
        }
        cssStyleSheets.put(location, new HashMap<>());
        ICSSVisitor visitor = new ACsGuisCssVisitor(location, cssStyleSheets.get(location));
        CSSVisitor.visitCSS(aCSS, visitor);
        ACsGuiApi.log.info("[CSS] Loaded css style sheet "+location);
        //System.out.println("Got style : "+cssStyleSheets);
    }

    /**
     * Read a CSS 3.0 declaration from a string using UTF-8 encoding.
     */
    public static Map<CompoundCssSelector, Map<EnumCssStyleProperties, CssStyleProperty<?>>> parseRawCss(GuiComponent<?> component, String css) {
        // UTF-8 is the fallback if neither a BOM nor @charset rule is present
        final CascadingStyleSheet aCSS = CSSReader.readFromString("#"+component.getCssId()+"{"+css+"}", StandardCharsets.UTF_8, ECSSVersion.CSS30);
        if (aCSS == null)
        {
            // Most probably a syntax error
            throw new IllegalStateException("[CSS] Failed to read CSS "+css+" - please see previous logging entries !");
        }
        Map<CompoundCssSelector, Map<EnumCssStyleProperties, CssStyleProperty<?>>> data = new HashMap<>();
        ICSSVisitor visitor = new ACsGuisStringCssVisitor(component, data);
        CSSVisitor.visitCSS(aCSS, visitor);
        ACsGuiApi.log.info("[CSS] Parsed style "+css);
        //System.out.println("Got style : "+cssStyleSheets);
        return data;
    }

    /**
     * Helper method to create an input stream for a ResourceLocation, usable by the css parser
     */
    public static InputStream getResource(ResourceLocation location) throws IOException, IllegalAccessException {
        List<IResourcePack> list = new ArrayList<>();
        list.addAll((Collection<? extends IResourcePack>) ReflectionHelper.findField(FMLClientHandler.class, "resourcePackList").get(FMLClientHandler.instance()));

        for (ResourcePackRepository.Entry resourcepackrepository$entry : Minecraft.getMinecraft().getResourcePackRepository().getRepositoryEntries())
        {
            if(resourcepackrepository$entry.getResourcePack().getResourceDomains().contains(location.getNamespace()))
                list.add(resourcepackrepository$entry.getResourcePack());
        }

        if (Minecraft.getMinecraft().getResourcePackRepository().getServerResourcePack() != null && Minecraft.getMinecraft().getResourcePackRepository().getServerResourcePack().getResourceDomains().contains(location.getNamespace()))
        {
            list.add(Minecraft.getMinecraft().getResourcePackRepository().getServerResourcePack());
        }

        for (int i = list.size() - 1; i >= 0; --i)
        {
            IResourcePack iresourcepack1 = list.get(i);
            if (iresourcepack1.resourceExists(location))
            {
                return iresourcepack1.getInputStream(location);
            }
        }
        throw new FileNotFoundException(location.toString());
    }

    /**
     * Computes the style to apply to the given css element, checking properties of parent elements <br>
     *     The return {@link CssStackElement} will choose which style to apply, depending on the state of the element <br>
     *     A call to this method <i>may</i> be heavy, depending on the css code behind it
     */
    public static CssStackElement getStyleFor(ComponentStyleManager component) {
        List<ResourceLocation> cssSheets = new ArrayList<>();
        //First retrieve the css sheets used in this gui, so find the gui
        GuiComponent<?> parent = component.getOwner();
        while(parent.getParent() != null)
            parent = parent.getParent();
        if(parent instanceof GuiFrame) {
            if(((GuiFrame) parent).usesDefaultStyle())
                cssSheets.add(DEFAULT_STYLE_SHEET);
            cssSheets.addAll(((GuiFrame) parent).getCssStyles());
        }
        else {
            if(parent.getCssId() == null || !parent.getCssId().equals("css_debug_pane"))
                ACsGuiApi.log.warn("Parent gui frame of " + component.getOwner() + " was not found, cannot apply its style !");
            cssSheets.add(DEFAULT_STYLE_SHEET);
        }
        Map<CompoundCssSelector, Map<EnumCssStyleProperties, CssStyleProperty<?>>> propertyMap = new HashMap<>();
        //Then apply the style of all sheets, keeping the same order
        for(ResourceLocation sheet : cssSheets)
        {
            if(!cssStyleSheets.containsKey(sheet))
                ACsGuiApi.log.warn("Style sheet "+sheet+" not loaded !");
            else
                //Apply style of the sheet, in the css code order
                cssStyleSheets.get(sheet).entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach((e) -> {
                    if(e.getKey().applies(component, null)) {
                        if(!propertyMap.containsKey(e.getKey()))
                            propertyMap.put(e.getKey(), new HashMap<>());
                        propertyMap.get(e.getKey()).putAll(e.getValue());
                    }
                });
        }
        //if(component.getOwner() instanceof GuiPanel && component.getOwner().getCssClass() != null && component.getOwner().getCssId() != null)
        //System.out.println("WDH GET PROP FOR "+component.getOwner()+" / "+component.getOwner().getCssId()+" / "+component.getOwner().getCssClass()+" / "+propertyMap+" / "+cssSheets);

        if(component.getCustomParsedStyle() != null) {
            //Apply custom style, with higher priority
            component.getCustomParsedStyle().entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach((e) -> {
                if (e.getKey().applies(component, null)) {
                    if (!propertyMap.containsKey(e.getKey()))
                        propertyMap.put(e.getKey(), new HashMap<>());
                    propertyMap.get(e.getKey()).putAll(e.getValue());
                }
            });
        }
        //Return the computed style
        return new CssStackElement(component.getParent() != null ? component.getParent().getCssStack() : null, propertyMap);
    }

    /**
     * @return all css properties, sorted by sheet name, selector and property type, used for debug
     */
    public static Map<ResourceLocation, Map<CompoundCssSelector, Map<EnumCssStyleProperties, CssStyleProperty<?>>>> getCssStyleSheets() {
        return cssStyleSheets;
    }
}