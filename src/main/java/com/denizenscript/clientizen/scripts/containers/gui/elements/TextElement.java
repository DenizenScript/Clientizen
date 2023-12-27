package com.denizenscript.clientizen.scripts.containers.gui.elements;

import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer;
import com.denizenscript.denizencore.objects.core.ColorTag;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import io.github.cottonmc.cotton.gui.widget.WText;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.minecraft.text.Text;

import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedObject;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedString;

public class TextElement implements GuiScriptContainer.GuiElementParser {

    // <--[language]
    // @name Text GUI Element
    // @group GUI System
    // @description
    // Text GUI elements display (potentially multi-line) text; they have a UI type of "text".
    // See also <@link language Label GUI Element>.
    //
    // <code>
    // ui_type: text
    // # The text element's text, required.
    // text: <text>
    // # The text element's text color, optional.
    // color: <ColorTag>
    // </code>
    // -->

    @Override
    public WWidget parse(GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context) {
        String textContent = getTaggedString(config, "text", context);
        if (textContent == null) {
            Debug.echoError("Must have text.");
            return null;
        }
        WText text = new WText(Text.literal(textContent));
        ColorTag color = getTaggedObject(ColorTag.class, config, "color", context);
        if (color != null) {
            text.setColor(color.asRGB());
        }
        return text;
    }
}
