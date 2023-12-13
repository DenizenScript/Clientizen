package com.denizenscript.clientizen.scripts.containers.gui.elements;

import com.denizenscript.clientizen.events.ClientizenGuiButtonPressedScriptEvent;
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

import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.*;

public class ButtonElement implements GuiScriptContainer.GuiElementParser {

    @Override
    public WWidget parse(GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context) {
        String label = getTaggedString(config, "label", context);
        Icon icon = parseIcon(config, "icon", context);
        WButton button = new WButton(icon, label != null ? Text.literal(label) : null);
        HorizontalAlignment horizontalTextAlignment = getTaggedEnum(HorizontalAlignment.class, config, "horizontal_text_alignment", context);
        if (horizontalTextAlignment != null) {
            button.setAlignment(horizontalTextAlignment);
        }
        final List<ScriptEntry> onClick = container.getEntries(new ClientizenScriptEntryData(), getSubPath(pathToElement, "on_click"));
        final String queueName = onClick != null ? getWidgetId(pathToElement) + "_pressed" : null;
        button.setOnClick(() -> {
            if (onClick != null) {
                ScriptUtilities.createAndStartQueueArbitrary(queueName, onClick, null, null, null);
            }
            ClientizenGuiButtonPressedScriptEvent.instance.handleButtonPress(pathToElement);
        });
        return button;
    }
}
