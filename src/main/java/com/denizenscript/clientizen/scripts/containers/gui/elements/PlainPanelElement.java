package com.denizenscript.clientizen.scripts.containers.gui.elements;

import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.text.StringHolder;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;

public class PlainPanelElement implements GuiScriptContainer.GuiElementParser {

    @Override
    public WWidget parse(GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context) {
        WPlainPanel plainPanel = new WPlainPanel();
        GuiScriptContainer.applyInsets(config, pathToElement, plainPanel::setInsets, context);
        YamlConfiguration children = config.getConfigurationSection("children");
        if (children == null) {
            return plainPanel;
        }
        for (StringHolder childIdHolder : children.contents.keySet()) {
            WWidget child = container.parseGUIWidget(children, childIdHolder.str, pathToElement + ".children", context);
            if (child != null) {
                plainPanel.add(child, child.getX(), child.getY(), child.getWidth(), child.getHeight());
            }
        }
        return plainPanel;
    }
}
