package com.denizenscript.clientizen.scripts.containers.gui.elements;

import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer;
import com.denizenscript.clientizen.tags.ClientizenTagContext;
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
        TagContext contextFromScript = new ClientizenTagContext(container);
        ParseableTag parseableTag = TagManager.parseTextToTag(text, contextFromScript);
        WDynamicLabel dynamicLabel = new WDynamicLabel(() -> parseableTag.parse(contextFromScript).toString());
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
