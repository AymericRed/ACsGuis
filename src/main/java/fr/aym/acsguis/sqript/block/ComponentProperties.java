package fr.aym.acsguis.sqript.block;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.button.GuiCheckBox;
import fr.aym.acsguis.component.entity.GuiEntityRender;
import fr.aym.acsguis.component.panel.GuiComboBox;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.textarea.*;
import fr.aym.acsguis.sqript.expressions.TypePanelLayout;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.structures.ScriptTypeAccessor;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeArray;
import fr.nico.sqript.types.TypePlayer;
import fr.nico.sqript.types.primitive.TypeBoolean;
import fr.nico.sqript.types.primitive.TypeNumber;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Pattern;

public class ComponentProperties<A, B extends ScriptType<?>>
{
    public static final ComponentProperties<GuiPanel, TypePanelLayout> LAYOUT = new ComponentProperties<>("layout", c -> new TypePanelLayout(c.getLayout()), (c, s) -> c.setLayout(s.getObject()));

    public static final ComponentProperties<TextComponent, TypeString> TEXT = new ComponentProperties<>("text", c -> new TypeString(c.getText()), (c, s) -> c.setText(s.getObject()));

    public static final ComponentProperties<GuiTextArea, TypeNumber> MAX_TEXT_LENGTH = new ComponentProperties<>("max_text_length", c -> new TypeNumber(c.getMaxTextLength()), (c, s) -> c.setMaxTextLength((int) Math.round(s.getObject())));
    public static final ComponentProperties<GuiTextArea, TypeString> HINT_TEXT = new ComponentProperties<>("hint_text", c -> new TypeString(c.getHintText()), (c, s) -> c.setHintText(s.getObject()));
    public static final ComponentProperties<GuiTextArea, TypeString> REGEX = new ComponentProperties<>("regex", c -> new TypeString(c.getRegexPattern().pattern()), (c, s) -> c.setRegexPattern(Pattern.compile(s.getObject())));

    public static final ComponentProperties<NumericComponent, TypeNumber> MIN_VALUE = new ComponentProperties<>("min_value", c -> new TypeNumber(c.getMin()), (c, s) -> c.setMin((int) Math.round(s.getObject())));
    public static final ComponentProperties<NumericComponent, TypeNumber> MAX_VALUE = new ComponentProperties<>("max_value", c -> new TypeNumber(c.getMax()), (c, s) -> c.setMax((int) Math.round(s.getObject())));

    public static final ComponentProperties<GuiCheckBox, TypeBoolean> CHECKED = new ComponentProperties<>("checked", c -> new TypeBoolean(c.isChecked()), (c, s) -> c.setChecked(s.getObject()));

    //TODO TypeEntity
    public static final ComponentProperties<GuiEntityRender, TypePlayer> ENTITY_TO_RENDER = new ComponentProperties<>("checked", c -> new TypePlayer((EntityPlayer) c.getEntity()), (c, s) -> c.setEntity(s.getObject()));

    public static final ComponentProperties<GuiComboBox, TypeArray> COMBO_CHOICES = new ComponentProperties<>("checked", c -> new TypeArray((ArrayList<?>) c.getEntries()), (c, s) -> {
        List<String> entries = new ArrayList<>();
        for(ScriptType<String> t : s.getObject())
        {
            entries.add(t.getObject());
        }
        c.setEntries(entries);
    });

    public static final ComponentProperties<GuiProgressBar, TypeNumber> PROGRESS = new ComponentProperties<>("bar_progress", c -> new TypeNumber(c.getProgress()), (c, s) -> c.setProgress((int) Math.round(s.getObject())));

    private final String name;
    private final Function<A, B> getter;
    private final BiConsumer<A, B> setter;

    public ComponentProperties(String name, Function<A, B> getter, BiConsumer<A, B> setter) {
        this.name = name;
        this.getter = getter;
        this.setter = setter;
    }

    public void getValueFromScript(ScriptContext context, GuiComponent<?> into) {
        setter.accept((A) into, (B) context.getVariable(name));
    }

    public void getValueFromComponent(GuiComponent<?> from, ScriptContext context) {
        B object = getter.apply((A) from);
        if(object != null) {
            context.put(new ScriptTypeAccessor(object, name));
        }
    }
}
