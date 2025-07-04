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
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment;

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
    // # The vertical alignment for the label's text, optional.
    // vertical_alignment: TOP/CENTER/BOTTOM
    // # The horizontal alignment for the label's text, optional.
    // horizontal_alignment: LEFT/CENTER/RIGHT
    // # The color for the label's text, optional.
    // color: <ColorTag>
    // </code>
    // The text, including tags, is parsed every frame.
    // With complex/otherwise computationally expensive tags, you should usually prefer using your own system for updating the text,
    // to avoid performance issues due to rapid parsing of such tags.
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
            dynamicLabel.setHorizontalAlignment(horizontalAlignment);
        }
        VerticalAlignment verticalAlignment = getTaggedEnum(VerticalAlignment.class, config, "vertical_alignment", context);
        if (verticalAlignment != null) {
            dynamicLabel.setVerticalAlignment(verticalAlignment);
        }
        ColorTag color = getTaggedObject(ColorTag.class, config, "color", context);
        if (color != null) {
            dynamicLabel.setColor(color.asARGB(), WDynamicLabel.DEFAULT_DARKMODE_TEXT_COLOR);
        }
        return dynamicLabel;
    }
}
