package com.denizenscript.clientizen.scripts.containers.gui.elements;

import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer;
import com.denizenscript.clientizen.util.impl.ClientizenScriptEntryData;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.ScriptUtilities;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.icon.Icon;
import net.minecraft.text.Text;

import java.util.List;

import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getSubPath;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedEnum;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedInt;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedString;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getWidgetId;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.parseIcon;

public class ButtonElement implements GuiScriptContainer.GuiElementParser {

    // <--[language]
    // @name Button GUI Element
    // @group GUI System
    // @description
    // Buttons are clickable GUI elements that can run code when clicked, with the UI type "button".
    // See also <@link language Toggle Button GUI Element>, for buttons made specifically to control a single boolean value.
    //
    // <code>
    // ui_type: button
    // # A label for the button, optional.
    // label: <text>
    // # An icon for the button, optional.
    // icon: <GUI Icon>
    // # The icon's size (width and height), optional.
    // icon_size: <number>
    // # Horizontal text alignment for the button's label, optional.
    // horizontal_text_alignment: LEFT/CENTER/RIGHT
    // # Code to run when the button is pressed, optional.
    // on_click:
    // - <script>
    // </code>
    // -->

    @Override
    public WWidget parse(GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context) {
        Icon icon = parseIcon(config, "icon", context);
        WButton button = new WButton(icon);
        String label = getTaggedString(config, "label", context);
        if (label != null) {
            button.setLabel(Text.literal(label));
        }
        Integer iconSize = getTaggedInt(config, "icon_size", context);
        if (iconSize != null) {
            button.setIconSize(iconSize);
        }
        HorizontalAlignment horizontalTextAlignment = getTaggedEnum(HorizontalAlignment.class, config, "horizontal_text_alignment", context);
        if (horizontalTextAlignment != null) {
            button.setAlignment(horizontalTextAlignment);
        }
        final List<ScriptEntry> onClick = container.getEntries(new ClientizenScriptEntryData(), getSubPath(pathToElement, "on_click"));
        if (onClick != null) {
            final String queueName = getWidgetId(pathToElement) + "_pressed";
            button.setOnClick(() -> ScriptUtilities.createAndStartQueueArbitrary(queueName, onClick, null, null, null));
        }
        return button;
    }
}
