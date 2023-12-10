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
import net.minecraft.text.Text;

import java.util.Map;

import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedString;

public class LabeledSliderElement implements GuiScriptContainer.GuiElementParser {

    @Override
    public WWidget parse(GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context) {
        WLabeledSlider labeledSlider = SliderElement.parseSlider(WLabeledSlider::new, container, config, pathToElement, context);
        if (labeledSlider == null) {
            return null;
        }
        String label = getTaggedString(config, "label", context);
        if (label != null) {
            labeledSlider.setLabel(Text.literal(label));
        }
        String dynamicLabel = config.getString("dynamic_label");
        if (dynamicLabel != null) {
            final ParseableTag dynamicLabelTag = TagManager.parseTextToTag(dynamicLabel, context);
            final String errorContext = "while parsing dynamic label for labeled slider '<A>" + pathToElement + "<LR>'";
            final TagContext labelContext = context.clone();
            final ContextSource.SimpleMap contextSource = new ContextSource.SimpleMap();
            labelContext.contextSource = contextSource;
            labeledSlider.setLabelUpdater(newValue -> {
                contextSource.contexts = Map.of("value", new ElementTag(newValue));
                Debug.pushErrorContext(errorContext);
                try {
                    return Text.literal(dynamicLabelTag.parse(labelContext).toString());
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
