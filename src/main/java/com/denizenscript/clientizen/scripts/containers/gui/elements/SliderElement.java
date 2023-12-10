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

import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getEnum;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedInt;

public class SliderElement implements GuiScriptContainer.GuiElementParser {

    @Override
    public WWidget parse(GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context) {
        return parseSlider(WSlider::new, container, config, pathToElement, context);
    }

    public static <T extends WAbstractSlider> T parseSlider(TriFunction<Integer, Integer, Axis, T> constructor, GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context) {
        Integer min = getTaggedInt(config, "min", context);
        Integer max = getTaggedInt(config, "max", context);
        Axis axis = getEnum(Axis.class, config, "axis", context);
        if (min == null || max == null || axis == null) {
            Debug.echoError("must specify min and max values, and an axis.");
            return null;
        }
        T slider = constructor.apply(min, max, axis);
        Integer value = getTaggedInt(config, "value", context);
        if (value != null) {
            slider.setValue(value);
        }
        WAbstractSlider.Direction direction = getEnum(WAbstractSlider.Direction.class, config, "direction", context);
        if (direction != null) {
            slider.setDirection(direction);
        }
        // TODO: proper id system - events
        addListener(slider::setValueChangeListener, container, pathToElement, "on_change", "_change");
        addListener(slider::setDraggingFinishedListener, container, pathToElement, "on_set", "_set");
        return slider;
    }

    private static void addListener(Consumer<IntConsumer> setter, GuiScriptContainer container, String pathToElement, String scriptPath, String queueSuffix) {
        final List<ScriptEntry> toRun = container.getEntries(new ClientizenScriptEntryData(), pathToElement.substring(container.getName().length() + 1) + '.' + scriptPath);
        if (toRun == null) {
            return;
        }
        final String queueName = pathToElement.substring(pathToElement.lastIndexOf('.') + 1) + queueSuffix;
        final ContextSource.SimpleMap contextSource = new ContextSource.SimpleMap();
        setter.accept(newValue -> {
            contextSource.contexts = Map.of("new_value", new ElementTag(newValue));
            ScriptUtilities.createAndStartQueueArbitrary(queueName, toRun, null, contextSource, null);
        });
    }
}
