package com.denizenscript.clientizen.scripts.containers.gui.elements;

import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.denizenscript.denizencore.utilities.text.StringHolder;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.Insets;

public class PlainPanelElement implements GuiScriptContainer.GuiElementParser {

    @Override
    public WWidget parse(GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context) {
        WPlainPanel plainPanel = new WPlainPanel();
        Insets insets = GuiScriptContainer.parseInsets(config.getConfigurationSection("insets"), pathToElement + ".insets", context);
        if (insets != null) {
            plainPanel.setInsets(insets);
        }
        YamlConfiguration children = config.getConfigurationSection("children");
        if (children == null) {
            return plainPanel;
        }
        for (StringHolder childIdHolder : children.contents.keySet()) {
            YamlConfiguration childConfig = children.getConfigurationSection(childIdHolder.str);
            if (childConfig == null) {
                Debug.echoError("Invalid GUI element '" + childIdHolder + "' in plain panel '" + pathToElement + "': no options/config found.");
                continue;
            }
            WWidget child = container.parseGUIWidget(childConfig, pathToElement + ".children." + childIdHolder, context);
            if (child != null) {
                plainPanel.add(child, child.getX(), child.getY(), child.getWidth(), child.getHeight());
            }
        }
        return plainPanel;
    }
}
