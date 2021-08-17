package fr.aym.acsguis.sqript.block;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.button.GuiButton;
import fr.aym.acsguis.component.button.GuiCheckBox;
import fr.aym.acsguis.component.entity.GuiEntityRender;
import fr.aym.acsguis.component.panel.*;
import fr.aym.acsguis.component.textarea.*;
import fr.aym.acsguis.sqript.SqriptCompatiblity;
import fr.nico.sqript.structures.ScriptContext;

import java.util.concurrent.Callable;

public enum ParseableComponent
{
    PANEL(GuiPanel.class, "panel", GuiPanel::new, ComponentProperties.LAYOUT),
    TABBED_PANE(GuiTabbedPane.class, "tabbed_pane", GuiTabbedPane::new, ComponentProperties.LAYOUT),
    SCROL_PANE(GuiScrollPane.class, "scroll_pane", GuiScrollPane::new, ComponentProperties.LAYOUT),
    LABEL(GuiLabel.class, "label", () -> new GuiLabel("not set"), ComponentProperties.TEXT),
    TEXT_FIELD(GuiTextField.class, "text_field", GuiTextField::new, SqriptCompatiblity.TEXT_AREA_PROPERTIES_PARSER),
    TEXT_AREA(GuiTextArea.class, "text_area", GuiTextArea::new, SqriptCompatiblity.TEXT_AREA_PROPERTIES_PARSER),
    PASSWORD_FIELD(GuiPasswordField.class, "password_field", GuiPasswordField::new, SqriptCompatiblity.TEXT_AREA_PROPERTIES_PARSER),
    INTEGER_FIELD(GuiIntegerField.class, "integer_field", () -> new GuiIntegerField(0, 255), ComponentProperties.TEXT, ComponentProperties.MIN_VALUE, ComponentProperties.MAX_VALUE),
    CHECKBOX(GuiCheckBox.class, "checkbox", GuiCheckBox::new, ComponentProperties.TEXT, ComponentProperties.CHECKED),
    BUTTON(GuiButton.class, "button", () -> new GuiButton("not set"), ComponentProperties.TEXT),
    ENTITY_RENDER(GuiEntityRender.class, "entity_render", () -> new GuiEntityRender(null), ComponentProperties.ENTITY_TO_RENDER),
    COMBO_BOX(GuiComboBox.class, "combo_box", () -> new GuiComboBox("not set", null), ComponentProperties.TEXT, ComponentProperties.COMBO_CHOICES),
    PROGRESS_BAR(GuiProgressBar.class, "progress_bar", GuiProgressBar::new, ComponentProperties.TEXT),
    PROGRESS_BAR_VERTICAL(GuiProgressBar.class, "progress_bar_vertical", () -> new GuiProgressBar(false), ComponentProperties.TEXT);

    //TODO NOT SUPPORTED : GuiResizableButton, GuiSlider, GuiCameraView, GuiList, GuiKeyLabel, GuiSearchField, tabs of GuiTabbedPane
    //TODO SLOTS

    private final Class<?> clazz;
    private final String key;
    private final Callable<GuiComponent<?>> componentCallable;
    private final ComponentProperties<?,?>[] properties;

    ParseableComponent(Class<?> clazz, String key, Callable<GuiComponent<?>> componentCallable, ComponentProperties<?, ?>... properties) {
        this.clazz = clazz;
        this.key = key;
        this.componentCallable = componentCallable;
        this.properties = properties;
    }
    
    public static ParseableComponent find(GuiComponent<?> component) {
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

    public void setupContext(ScriptContext context, GuiComponent<?> component) {
        for(ComponentProperties<?, ?> property : properties) {
            property.getValueFromComponent(component, context);
        }
    }

    public void fillComponent(ScriptContext context, GuiComponent<?> component) {
        for(ComponentProperties<?, ?> property : properties) {
            property.getValueFromScript(context, component);
        }
    }

    public GuiComponent<?> create() throws Exception {
        return componentCallable.call();
    }

   /* TODO public static ParseableComponent injectComponentParser(String key, Callable<GuiComponent<?>> componentCallable, ITriConsumer<GuiComponent<?>, String, ScriptBlock.ScriptLineBlock> fieldHandler) {
        return EnumHelper.addEnum(ParseableComponent.class, key, new Class<?>[] {String.class, Callable.class, ITriConsumer.class}, key, componentCallable, fieldHandler);
    } */
}