package fr.aym.acsguis.sqript.component;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.button.GuiButton;
import fr.aym.acsguis.component.button.GuiCheckBox;
import fr.aym.acsguis.component.entity.GuiEntityRender;
import fr.aym.acsguis.component.panel.*;
import fr.aym.acsguis.component.textarea.*;
import fr.aym.acsguis.sqript.SqriptCompatiblity;
import fr.nico.sqript.structures.ScriptContext;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.concurrent.Callable;

@SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
public enum ParseableComponent
{
    PANEL(GuiPanel.class, "panel", GuiPanel::new, ComponentProperties.SET_STYLE, ComponentProperties.LAYOUT, ComponentProperties.NEXT_TAB_PANE),
    TABBED_PANE(GuiTabbedPane.class, "tabbed_pane", GuiTabbedPane::new, ComponentProperties.SET_STYLE, ComponentProperties.LAYOUT),
    SCROLL_PANE(GuiScrollPane.class, "scroll_pane", GuiScrollPane::new, ComponentProperties.SET_STYLE, ComponentProperties.LAYOUT, ComponentProperties.NEXT_TAB_PANE),
    LABEL(GuiLabel.class, "label", () -> new GuiLabel("not set"), ComponentProperties.SET_STYLE, ComponentProperties.TEXT, ComponentProperties.MAX_TEXT_LENGTH),
    TEXT_FIELD(GuiTextField.class, "text_field", GuiTextField::new, SqriptCompatiblity.TEXT_AREA_PROPERTIES_PARSER),
    TEXT_AREA(GuiTextArea.class, "text_area", GuiTextArea::new, SqriptCompatiblity.TEXT_AREA_PROPERTIES_PARSER),
    PASSWORD_FIELD(GuiPasswordField.class, "password_field", GuiPasswordField::new, SqriptCompatiblity.TEXT_AREA_PROPERTIES_PARSER),
    INTEGER_FIELD(GuiIntegerField.class, "integer_field", () -> new GuiIntegerField(0, 255), ComponentProperties.SET_STYLE, ComponentProperties.TEXT, ComponentProperties.MIN_VALUE, ComponentProperties.MAX_VALUE),
    CHECKBOX(GuiCheckBox.class, "checkbox", GuiCheckBox::new, ComponentProperties.SET_STYLE, ComponentProperties.TEXT, ComponentProperties.CHECKED),
    BUTTON(GuiButton.class, "button", () -> new GuiButton("not set"), ComponentProperties.SET_STYLE, ComponentProperties.TEXT),
    ENTITY_RENDER(GuiEntityRender.class, "entity_render", () -> new GuiEntityRender(null), ComponentProperties.SET_STYLE, ComponentProperties.ENTITY_TO_RENDER),
    COMBO_BOX(GuiComboBox.class, "combo_box", () -> new GuiComboBox("not set", null), ComponentProperties.SET_STYLE, ComponentProperties.TEXT, ComponentProperties.COMBO_CHOICES),
    PROGRESS_BAR(GuiProgressBar.class, "progress_bar", GuiProgressBar::new, ComponentProperties.SET_STYLE, ComponentProperties.TEXT, ComponentProperties.PROGRESS),
    PROGRESS_BAR_VERTICAL(GuiProgressBar.class, "progress_bar_vertical", () -> new GuiProgressBar(false), ComponentProperties.SET_STYLE, ComponentProperties.TEXT, ComponentProperties.PROGRESS);

    //FIXME NOT SUPPORTED : GuiResizableButton, GuiSlider, GuiCameraView, GuiList, GuiKeyLabel, GuiSearchField
    //TODO SLOTS

    private final Class<?> clazz;
    private final String key;
    private final Callable<GuiComponent> componentCallable;
    private final ComponentProperties<?,?>[] properties;

    ParseableComponent(Class<?> clazz, String key, Callable<GuiComponent> componentCallable, ComponentProperties<?, ?>... properties) {
        this.clazz = clazz;
        this.key = key;
        this.componentCallable = componentCallable;
        this.properties = properties;
    }
    
    public static ParseableComponent find(GuiComponent component) {
        if(component instanceof GuiFrame) {
            return PANEL;
        }
        for(ParseableComponent c : values()) {
            if(component.getClass() == c.clazz)
                return c;
        }
        throw new IllegalArgumentException("Gui component type "+component+" isn't supported ! "+component.getClass());
    }

    public static ParseableComponent find(String name) {
        for(ParseableComponent c : values()) {
            if(name.equalsIgnoreCase(c.getKey()))
                return c;
        }
        throw new UnsupportedOperationException("Gui component type "+name+" does not exists !");
    }

    public String getKey() {
        return key;
    }

    public ComponentProperties<?, ?>[] getProperties() {
        return properties;
    }

    public void setupContext(ScriptContext context, GuiComponent component) {
        for(ComponentProperties<?, ?> property : properties) {
            property.getValueFromComponent(component, context);
        }
    }

    public void fillComponent(ScriptContext context, GuiComponent component) {
        for(ComponentProperties<?, ?> property : properties) {
            property.getValueFromScript(context, component);
        }
    }

    public GuiComponent create() throws Exception {
        return componentCallable.call();
    }

    public static ParseableComponent injectComponentParser(String key, Callable<GuiComponent> componentCallable, ComponentProperties<?, ?>... properties) {
        return EnumHelper.addEnum(ParseableComponent.class, key, new Class<?>[] {String.class, Callable.class, ComponentProperties[].class}, key, componentCallable, properties);
    }
}