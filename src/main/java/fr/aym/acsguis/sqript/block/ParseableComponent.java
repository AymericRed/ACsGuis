package fr.aym.acsguis.sqript.block;

import com.helger.commons.functional.ITriConsumer;
import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.button.GuiButton;
import fr.aym.acsguis.component.button.GuiCheckBox;
import fr.aym.acsguis.component.entity.GuiEntityRender;
import fr.aym.acsguis.component.panel.GuiComboBox;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.panel.GuiScrollPane;
import fr.aym.acsguis.component.panel.GuiTabbedPane;
import fr.aym.acsguis.component.textarea.*;
import fr.nico.sqript.blocks.ScriptBlock;
import fr.nico.sqript.compiling.ScriptCompileGroup;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.TypePlayer;
import net.minecraftforge.common.util.EnumHelper;
import scala.tools.nsc.doc.model.TypeEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

public enum ParseableComponent
{
    PANEL("panel", GuiPanel::new),
    TABBED_PANE("tabbed_pane", GuiTabbedPane::new),
    SCROL_PANE("scroll_pane", GuiScrollPane::new),
    LABEL("label", () -> new GuiLabel("not set"), (c, n, b) -> {
        if(n.equalsIgnoreCase("text")) {
            ((GuiLabel)c).setText(b.getRawContent());
        }
        else
            throw new IllegalArgumentException("Cannot set "+n+" on component of type label");
    }),
    TEXT_FIELD("text_field", GuiTextField::new, GuiTextArea.PROPERTIES_PARSER),
    TEXT_AREA("text_area", GuiTextArea::new, GuiTextArea.PROPERTIES_PARSER),
    PASSWORD_FIELD("password_field", GuiPasswordField::new, GuiTextArea.PROPERTIES_PARSER),
    INTEGER_FIELD("integer_field", () -> new GuiIntegerField(0, 255), (c, n, b) -> {
        if(n.equalsIgnoreCase("text")) {
            ((GuiIntegerField)c).setText(b.getRawContent());
        }
        else if(n.equalsIgnoreCase("min_value")) {
            ((GuiIntegerField)c).setMin(Integer.parseInt(b.getRawContent()));
        }
        else if(n.equalsIgnoreCase("max_value")) {
            ((GuiIntegerField)c).setMax(Integer.parseInt(b.getRawContent()));
        }
        else
            throw new IllegalArgumentException("Cannot set "+n+" on component of type integer_field");
    }),
    CHECKBOX("checkbox", GuiCheckBox::new, (c, n, b) -> {
        if(n.equalsIgnoreCase("text")) {
            ((GuiCheckBox)c).setText(b.getRawContent());
        }
        else if(n.equalsIgnoreCase("checked")) {
            ((GuiCheckBox)c).setChecked(Boolean.parseBoolean(b.getRawContent()));
        }
        else
            throw new IllegalArgumentException("Cannot set "+n+" on component of type checkbox");
    }),
    BUTTON("button", () -> new GuiButton("not set"), (c, n, b) -> {
        if(n.equalsIgnoreCase("text")) {
            ((GuiButton)c).setText(b.getRawContent());
        }
        else
            throw new IllegalArgumentException("Cannot set "+n+" on component of type button");
    }),
    ENTITY_RENDER("entity_render", () -> new GuiEntityRender(null), (c, n, b) -> {
        if(n.equalsIgnoreCase("entity_to_render")) {
            try {
                ((GuiEntityRender)c).setEntity(((TypePlayer)b.evaluate(new ScriptCompileGroup(), ScriptContext.fromGlobal())).getObject());
            } catch (Exception e) {
                throw new RuntimeException("Cannot parse entity "+b.getRawContent(), e);
            }
        }
        else
            throw new IllegalArgumentException("Cannot set "+n+" on component of type entity_render");
    }),
    COMBO_BOX("combo_box", () -> new GuiComboBox("not set", null), (c, n, b) -> {
        if(n.equalsIgnoreCase("text")) {
            ((GuiComboBox)c).setDefaultText(b.getRawContent());
        }
        else if(n.equalsIgnoreCase("choices")) {
            try {
                TypeArray array = (TypeArray) b.evaluate(new ScriptCompileGroup(), ScriptContext.fromGlobal());
                List<String> entries = new ArrayList<>();
                for(ScriptType<String> s : array.getObject())
                {
                    entries.add(s.getObject());
                }
                ((GuiComboBox)c).setEntries(entries);
            } catch (Exception e) {
                throw new RuntimeException("Cannot parse string array "+b.getRawContent(), e);
            }
        }
        else
            throw new IllegalArgumentException("Cannot set "+n+" on component of type combo_box");
    });

    //TODO PARSER L'INTERIEUR DES VALEURS (genre dans text:, lire les variables
    //NOT SUPPORTED : GuiResizableButton, GuiSlider, GuiCameraView, GuiList, GuiKeyLabel, GuiProgressBar, GuiSearchField
    //TODO SLOTS

    private final String key;
    private final Callable<GuiComponent<?>> componentCallable;
    private final ITriConsumer<GuiComponent<?>, String, ScriptBlock.ScriptLineBlock> fieldHandler;

    ParseableComponent(String key, Callable<GuiComponent<?>> componentCallable) {
        this(key, componentCallable, (c, n, s) -> {
            throw new IllegalArgumentException("Cannot set "+n+" on component of type "+key);
        });
    }

    ParseableComponent(String key, Callable<GuiComponent<?>> componentCallable, ITriConsumer<GuiComponent<?>, String, ScriptBlock.ScriptLineBlock> fieldHandler) {
        this.key = key;
        this.componentCallable = componentCallable;
        this.fieldHandler = fieldHandler;
    }

    public static ParseableComponent find(String name) {
        for(ParseableComponent c : values()) {
            if(name.equalsIgnoreCase(c.getKey()))
                return c;
        }
        throw new IllegalArgumentException("Gui component type "+name+" does not exists !");
    }

    public String getKey() {
        return key;
    }

    public ITriConsumer<GuiComponent<?>, String, ScriptBlock.ScriptLineBlock> getFieldHandler() {
        return fieldHandler;
    }

    public GuiComponent<?> create() throws Exception {
        return componentCallable.call();
    }

    public static ParseableComponent injectComponentParser(String key, Callable<GuiComponent<?>> componentCallable, ITriConsumer<GuiComponent<?>, String, ScriptBlock.ScriptLineBlock> fieldHandler) {
        return EnumHelper.addEnum(ParseableComponent.class, key, new Class<?>[] {String.class, Callable.class, ITriConsumer.class}, key, componentCallable, fieldHandler);
    }
}