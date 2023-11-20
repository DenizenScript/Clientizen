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

public class ButtonElement implements GuiScriptContainer.GuiElementParser {

    @Override
    public WWidget parse(GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context) {
        String label = config.getString("label");
        Icon icon = GuiScriptContainer.parseIcon(config.getConfigurationSection("icon"), pathToElement + ".icon", context);
        WButton button = new WButton(icon, label != null ? Text.literal(label) : null);
        HorizontalAlignment textAlignment = GuiScriptContainer.getEnum(HorizontalAlignment.class, config, pathToElement, "text_alignment", context);
        if (textAlignment != null) {
            button.setAlignment(textAlignment);
        }
        List<ScriptEntry> onClick = container.getEntries(new ClientizenScriptEntryData(), pathToElement.substring(pathToElement.indexOf('.') + 1) + ".on_click");
        button.setOnClick(() -> {
            if (onClick != null) {
                String buttonName = pathToElement.substring(pathToElement.lastIndexOf('.') + 1);
                ScriptUtilities.createAndStartQueueArbitrary(buttonName + "_pressed", onClick, null, null, null);
            }
            ClientizenGuiButtonPressedScriptEvent.instance.handleButtonPress(pathToElement);
        });
        return button;
    }
}
