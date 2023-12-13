package com.denizenscript.clientizen.scripts.containers.gui.elements;

import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.text.StringHolder;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;

import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.applyInsets;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getSubPath;

public class PlainPanelElement implements GuiScriptContainer.GuiElementParser {

    @Override
    public WWidget parse(GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context) {
        WPlainPanel plainPanel = new WPlainPanel();
        applyInsets(config, plainPanel::setInsets, context);
        YamlConfiguration children = config.getConfigurationSection("children");
        if (children == null) {
            return plainPanel;
        }
        String childrenPath = getSubPath(pathToElement, "children");
        for (StringHolder childIdHolder : children.contents.keySet()) {
            WWidget child = container.parseGUIWidget(children, childIdHolder.str, childrenPath, context);
            if (child != null) {
                plainPanel.add(child, child.getX(), child.getY(), child.getWidth(), child.getHeight());
            }
        }
        return plainPanel;
    }
}
