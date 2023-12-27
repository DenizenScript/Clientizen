package com.denizenscript.clientizen.scripts.containers.gui.elements;

import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer;
import com.denizenscript.clientizen.util.impl.ClientizenScriptEntryData;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.queues.ContextSource;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.ScriptUtilities;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import io.github.cottonmc.cotton.gui.widget.WAbstractSlider;
import io.github.cottonmc.cotton.gui.widget.WSlider;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import org.apache.commons.lang3.function.TriFunction;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getSubPath;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedEnum;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedInt;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getWidgetId;

public class SliderElement implements GuiScriptContainer.GuiElementParser {

    // <--[language]
    // @name Slider GUI Element
    // @group GUI System
    // @description
    // Sliders allow controlling a number (integer) value, with a customizable min/max values; they have a UI type of "slider".
    //
    // <code>
    // ui_type: slider
    // # The minimum value the slider can hold, optional (defaults to 0).
    // min: <number>
    // # The maximum value the slider can hold, required.
    // max: <number>
    // # The axis the slider slides along, required.
    // axis: HORIZONTAL/VERTICAL
    // # The direction the slider slides in along its axis to increase its value, optional.
    // # Defaults to UP for vertical sliders, and RIGHT for horizontal sliders.
    // # Note that the direction must match the axis, UP/DOWN for VERTICAL, and LEFT/RIGHT for HORIZONTAL.
    // direction: UP/DOWN/LEFT/RIGHT
    // # The value the slider should be on, optional.
    // value: <number>
    // # Code to run when the slider's value is changed in the slightest (each number it goes through while dragging), optional.
    // # Provides <context.new_value>, returning an ElementTag(Number) of the slider's new value.
    // # Should generally prefer listening to the final value set instead of this.
    // on_change:
    // - <script>
    // # Code to run when the slider's value is set (the user finished changing it), optional.
    // # Provides <context.new_value>, returning an ElementTag(Number) of the slider's new value.
    // on_set:
    // - <script>
    // </code>
    // -->

    @Override
    public WWidget parse(GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context) {
        return parseSlider(WSlider::new, container, config, pathToElement, context);
    }

    public static <T extends WAbstractSlider> T parseSlider(TriFunction<Integer, Integer, Axis, T> constructor, GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context) {
        Integer min = getTaggedInt(config, "min", 0, context);
        if (min == null) {
            return null;
        }
        Integer max = getTaggedInt(config, "max", context);
        if (max == null) {
            Debug.echoError("Must specify a max value");
            return null;
        }
        Axis axis = getTaggedEnum(Axis.class, config, "axis", context);
        if (axis == null) {
            Debug.echoError("Must specify an axis.");
            return null;
        }
        T slider = constructor.apply(min, max, axis);
        WAbstractSlider.Direction direction = getTaggedEnum(WAbstractSlider.Direction.class, config, "direction", context);
        if (direction != null) {
            if (direction.getAxis() != axis) {
                Debug.echoError("Invalid direction '" + direction + "': can't be used with axis '" + axis + "'.");
                return null;
            }
            slider.setDirection(direction);
        }
        Integer value = getTaggedInt(config, "value", context);
        if (value != null) {
            slider.setValue(value);
        }
        // TODO: proper id system - events
        addListener(slider::setValueChangeListener, container, pathToElement, "on_change", "_change");
        addListener(slider::setDraggingFinishedListener, container, pathToElement, "on_set", "_set");
        return slider;
    }

    private static void addListener(Consumer<IntConsumer> setter, GuiScriptContainer container, String pathToElement, String scriptPath, String queueSuffix) {
        final List<ScriptEntry> toRun = container.getEntries(new ClientizenScriptEntryData(), getSubPath(pathToElement, scriptPath));
        if (toRun == null) {
            return;
        }
        final String queueName = getWidgetId(pathToElement) + queueSuffix;
        final ContextSource.SimpleMap contextSource = new ContextSource.SimpleMap();
        setter.accept(newValue -> {
            contextSource.contexts = Map.of("new_value", new ElementTag(newValue));
            ScriptUtilities.createAndStartQueueArbitrary(queueName, toRun, null, contextSource, null);
        });
    }
}
