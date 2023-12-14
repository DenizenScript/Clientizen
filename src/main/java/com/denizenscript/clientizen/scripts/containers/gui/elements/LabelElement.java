package com.denizenscript.clientizen.scripts.containers.gui.elements;

import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer;
import com.denizenscript.denizencore.objects.core.ColorTag;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment;
import net.minecraft.text.Text;

import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedEnum;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedObject;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedString;

public class LabelElement implements GuiScriptContainer.GuiElementParser {

    // <--[language]
    // @name Label GUI Element
    // @group GUI System
    // @description
    // Labels are simple, single lines of text, with a UI type of "label".
    // See <@link language Text GUI Element>s for multi-line text,
    // and <@link language Dynamic Label GUI Element> for dynamically updating labels.
    //
    // <code>
    // ui_type: label
    // # The text the label will show, optional.
    // text: <text>
    // # The vertical alignment for the label's text, optional.
    // vertical_alignment: TOP/CENTER/BOTTOM
    // # The horizontal alignment for the label's text, optional.
    // horizontal_alignment: LEFT/CENTER/RIGHT
    // # The label's text color, optional.
    // color: <ColorTag>
    // </code>
    // -->

    @Override
    public WWidget parse(GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context) {
        String text = getTaggedString(config, "text", context);
        if (text == null) {
            Debug.echoError("Must have text.");
            return null;
        }
        WLabel label = new WLabel(Text.literal(text));
        VerticalAlignment verticalAlignment = getTaggedEnum(VerticalAlignment.class, config, "vertical_alignment", context);
        if (verticalAlignment != null) {
            label.setVerticalAlignment(verticalAlignment);
        }
        HorizontalAlignment horizontalAlignment = getTaggedEnum(HorizontalAlignment.class, config, "horizontal_alignment", context);
        if (horizontalAlignment != null) {
            label.setHorizontalAlignment(horizontalAlignment);
        }
        ColorTag color = getTaggedObject(ColorTag.class, config, "color", context);
        if (color != null) {
            label.setColor(color.asRGB());
        }
        return label;
    }
}
