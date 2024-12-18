package fr.aym.acsguis.sqript.component;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.button.GuiCheckBox;
import fr.aym.acsguis.component.entity.GuiEntityRender;
import fr.aym.acsguis.component.layout.PanelLayout;
import fr.aym.acsguis.component.panel.GuiComboBox;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.textarea.GuiProgressBar;
import fr.aym.acsguis.component.textarea.GuiTextArea;
import fr.aym.acsguis.component.textarea.NumericComponent;
import fr.aym.acsguis.component.textarea.TextComponent;
import fr.aym.acsguis.sqript.SqriptCompatiblity;
import fr.aym.acsguis.sqript.expressions.TypePanelLayout;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.TypeEntity;
import fr.nico.sqript.types.primitive.TypeBoolean;
import fr.nico.sqript.types.primitive.TypeNumber;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Pattern;

@SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
public class ComponentProperties<A, B> {
    private static final List<ComponentProperties<?, ?>> properties = new ArrayList<>();

    public static final ComponentProperties<GuiPanel, PanelLayout<?>> LAYOUT = new ComponentProperties<>("layout", c -> new TypePanelLayout(c.getLayout()), GuiPanel::setLayout);

    public static final ComponentProperties<TextComponent, String> TEXT = new ComponentProperties<>("text", c -> new TypeString(c.getText()), TextComponent::setText);

    public static final ComponentProperties<GuiTextArea, Double> MAX_TEXT_LENGTH = new ComponentProperties<>("max_text_length", c -> new TypeNumber(c.getMaxTextLength()), (c, s) -> c.setMaxTextLength(s.intValue()));
    public static final ComponentProperties<GuiTextArea, String> HINT_TEXT = new ComponentProperties<>("hint_text", c -> new TypeString(c.getHintText()), GuiTextArea::setHintText);
    public static final ComponentProperties<GuiTextArea, String> REGEX = new ComponentProperties<>("regex", c -> new TypeString(c.getRegexPattern().pattern()), (c, s) -> c.setRegexPattern(Pattern.compile(s)));

    public static final ComponentProperties<NumericComponent, Double> MIN_VALUE = new ComponentProperties<>("min_value", c -> new TypeNumber(c.getMin()), (c, s) -> c.setMin(s.intValue()));
    public static final ComponentProperties<NumericComponent, Double> MAX_VALUE = new ComponentProperties<>("max_value", c -> new TypeNumber(c.getMax()), (c, s) -> c.setMax(s.intValue()));

    public static final ComponentProperties<GuiCheckBox, Boolean> CHECKED = new ComponentProperties<>("checked_state", c -> new TypeBoolean(c.isChecked()), GuiCheckBox::setChecked);

    public static final ComponentProperties<GuiEntityRender, Entity> ENTITY_TO_RENDER = new ComponentProperties<>("entity_to_render", c -> new TypeEntity(c.getEntity()), (c, s) -> c.setEntity((EntityLivingBase) s));

    public static final ComponentProperties<GuiComboBox, ArrayList<ScriptType<?>>> COMBO_CHOICES = new ComponentProperties<>("combo_choices", c -> {
        ArrayList<ScriptType<?>> entries = new ArrayList<>();
        for (String s : c.getEntries()) {
            entries.add(new TypeString(s));
        }
        return new TypeArray(entries);
    }, (c, s) -> {
        List<String> entries = new ArrayList<>();
        for (ScriptType<?> t : s) {
            entries.add((String) t.getObject());
        }
        c.setEntries(entries);
    });

    public static final ComponentProperties<GuiProgressBar, Double> PROGRESS = new ComponentProperties<>("bar_progress", c -> new TypeNumber(c.getProgress()), (c, s) -> c.setProgress(s.intValue()));

    public static final ComponentProperties<GuiComponent, String> SET_STYLE = new ComponentProperties<>("style", c -> new TypeString(""), (c, s) -> {
        if (!s.isEmpty())
            c.setCssCode(s);
    });

    public static final ComponentProperties<GuiComponent, String> NEXT_TAB_PANE = new ComponentProperties<>("next_tab_pane_name", c -> new TypeString(SqriptCompatiblity.nextPannedTabName), (c, s) -> SqriptCompatiblity.nextPannedTabName = s);

    private final String name;
    private final Function<A, ScriptType<B>> getter;
    private final BiConsumer<A, B> setter;

    public ComponentProperties(String name, Function<A, ScriptType<B>> getter, BiConsumer<A, B> setter) {
        this.name = name;
        this.getter = getter;
        this.setter = setter;
        properties.add(this);
    }

    public void getValueFromScript(ScriptContext context, GuiComponent into) {
        setValueOnComponent(into, ((ScriptType<B>) context.getVariable(name)).getObject());
    }

    public void setValueOnComponent(GuiComponent into, B value) {
        setter.accept((A) into, value);
    }

    public Object getValueFromComponent(GuiComponent from) {
        ScriptType<B> object = getter.apply((A) from);
        return object.getObject();
    }

    public void getValueFromComponent(GuiComponent from, ScriptContext context) {
        ScriptType<B> object = getter.apply((A) from);
        if (object != null) {
            context.put(new ScriptTypeAccessor(object, name));
        }
    }

    public String getName() {
        return name;
    }

    public static List<ComponentProperties<?, ?>> getProperties() {
        return properties;
    }
}
