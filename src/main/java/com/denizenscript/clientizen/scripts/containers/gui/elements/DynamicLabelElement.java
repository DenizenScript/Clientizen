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

import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getDebugPath;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedEnum;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedObject;

public class DynamicLabelElement implements GuiScriptContainer.GuiElementParser {

    // <--[language]
    // @name Dynamic Label GUI Element
    // @group GUI System
    // @description
    // Dynamic labels work similarly to <@link language Label GUI Element>s, but with dynamically updating text; they have a UI type of "dynamic_label".
    //
    // <code>
    // ui_type: dynamic_label
    // # The dynamically updating text for the label, required.
    // text: <text>
    // # The horizontal alignment for the label's text, optional.
    // horizontal_alignment: LEFT/CENTER/RIGHT
    // # The color for the label's text, optional.
    // color: <ColorTag>
    // </code>
    // The text is parsed every time the label is rendered, which means tags in it are constantly read.
    // This is fine for simple tags, but in the case of highly complex ones it may be better to implement
    // your own system for updating the text using a normal label, to only do it when relevant.
    // -->

    @Override
    public WWidget parse(GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context) {
        String text = config.getString("text");
        if (text == null) {
            Debug.echoError("Must have text.");
            return null;
        }
        final ParseableTag parseableTag = TagManager.parseTextToTag(text, context);
        final String errorContext = "while parsing text for dynamic label '<A>" + getDebugPath(pathToElement) + "<LR>'";
        WDynamicLabel dynamicLabel = new WDynamicLabel(() -> {
            Debug.pushErrorContext(errorContext);
            try {
                return parseableTag.parse(context).toString();
            }
            finally {
                Debug.popErrorContext();
            }
        });
        HorizontalAlignment horizontalAlignment = getTaggedEnum(HorizontalAlignment.class, config, "horizontal_alignment", context);
        if (horizontalAlignment != null) {
            dynamicLabel.setAlignment(horizontalAlignment);
        }
        ColorTag color = getTaggedObject(ColorTag.class, config, "color", context);
        if (color != null) {
            dynamicLabel.setColor(color.asRGB(), WDynamicLabel.DEFAULT_DARKMODE_TEXT_COLOR);
        }
        return dynamicLabel;
    }
}
