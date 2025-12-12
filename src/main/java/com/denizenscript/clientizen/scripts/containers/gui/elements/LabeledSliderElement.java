package com.denizenscript.clientizen.scripts.containers.gui.elements;

import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.scripts.queues.ContextSource;
import com.denizenscript.denizencore.tags.ParseableTag;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.tags.TagManager;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import io.github.cottonmc.cotton.gui.widget.WLabeledSlider;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.minecraft.network.chat.Component;

import java.util.Map;

import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getDebugPath;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedString;

public class LabeledSliderElement implements GuiScriptContainer.GuiElementParser {

    // <--[language]
    // @name Labeled Slider GUI Element
    // @group GUI System
    // @description
    // Labeled sliders work similarly to <@link language Slider GUI Element>s, but have a label and a different design; they have a UI type of "labeled_slider".
    //
    // Labeled sliders have all the same keys and values as normal <@link language Slider GUI Element>s, and additionally:
    // <code>
    // ui_type: labeled_slider
    // # A static unchanging label for the slider, optional.
    // label: <text>
    // # A dynamic label for the slider that updates every time the slider's value changes, optional.
    // # Provides <context.value>, returns an ElementTag(Number) of the slider value the label is being parsed for.
    // # Note that this overrides the normal label if specified.
    // dynamic_label: <text>
    // </code>
    // -->

    @Override
    public WWidget parse(GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context) {
        WLabeledSlider labeledSlider = SliderElement.parseSlider(WLabeledSlider::new, container, config, pathToElement, context);
        if (labeledSlider == null) {
            return null;
        }
        String label = getTaggedString(config, "label", context);
        if (label != null) {
            labeledSlider.setLabel(Component.literal(label));
        }
        String dynamicLabel = config.getString("dynamic_label");
        if (dynamicLabel != null) {
            final ParseableTag dynamicLabelTag = TagManager.parseTextToTag(dynamicLabel, context);
            final String errorContext = "while parsing dynamic label for labeled slider '<A>" + getDebugPath(pathToElement) + "<LR>'";
            final TagContext labelContext = context.clone();
            final ContextSource.SimpleMap contextSource = new ContextSource.SimpleMap();
            labelContext.contextSource = contextSource;
            labeledSlider.setLabelUpdater(newValue -> {
                contextSource.contexts = Map.of("value", new ElementTag(newValue));
                Debug.pushErrorContext(errorContext);
                try {
                    return Component.literal(dynamicLabelTag.parse(labelContext).toString());
                }
                finally {
                    Debug.popErrorContext();
                }
            });
            // Set the label, as LibGUI only uses the updater when the value changes
            labeledSlider.setLabel(labeledSlider.getLabelUpdater().updateLabel(labeledSlider.getValue()));
        }
        return labeledSlider;
    }
}
