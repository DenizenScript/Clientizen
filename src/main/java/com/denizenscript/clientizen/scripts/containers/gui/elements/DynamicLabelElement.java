package com.denizenscript.clientizen.scripts.containers.gui.elements;

import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer;
import com.denizenscript.denizencore.objects.core.ColorTag;
import com.denizenscript.denizencore.tags.ParseableTag;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.tags.TagManager;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import io.github.cottonmc.cotton.gui.widget.WDynamicLabel;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;

public class DynamicLabelElement implements GuiScriptContainer.GuiElementParser {

    @Override
    public WWidget parse(GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context) {
        String text = config.getString("text");
        if (text == null) {
            Debug.echoError("must have text.");
            return null;
        }
        final ParseableTag parseableTag = TagManager.parseTextToTag(text, context);
        final String errorContext = "while parsing text for dynamic label '<A>" + pathToElement + "<LR>'";
        WDynamicLabel dynamicLabel = new WDynamicLabel(() -> {
            Debug.pushErrorContext(errorContext);
            try {
                return parseableTag.parse(context).toString();
            }
            finally {
                Debug.popErrorContext();
            }
        });
        HorizontalAlignment horizontalAlignment = GuiScriptContainer.getEnum(HorizontalAlignment.class, config, "horizontal_alignment", context);
        if (horizontalAlignment != null) {
            dynamicLabel.setAlignment(horizontalAlignment);
        }
        ColorTag color = GuiScriptContainer.getTaggedObject(ColorTag.class, config, "color", context);
        if (color != null) {
            dynamicLabel.setColor(color.asRGB(), WDynamicLabel.DEFAULT_DARKMODE_TEXT_COLOR);
        }
        return dynamicLabel;
    }
}
