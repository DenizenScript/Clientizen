package com.denizenscript.clientizen.scripts.containers.gui.elements;

import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.denizenscript.denizencore.utilities.text.StringHolder;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;

import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.applyInsets;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getSubPath;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedInt;

public class GridPanelElement implements GuiScriptContainer.GuiElementParser {

    @Override
    public WWidget parse(GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context) {
        Integer gridSize = getTaggedInt(config, "grid_size", context);
        if (gridSize == null) {
            Debug.echoError("Must specify a grid size.");
            return null;
        }
        WGridPanel gridPanel = new WGridPanel(gridSize);
        applyInsets(config, gridPanel::setInsets, context);
        Integer horizontalGap = getTaggedInt(config, "horizontal_gap", context);
        Integer verticalGap = getTaggedInt(config, "vertical_gap", context);
        gridPanel.setGaps(horizontalGap != null ? horizontalGap : 0, verticalGap != null ? verticalGap : 0);
        YamlConfiguration children = config.getConfigurationSection("children");
        if (children == null) {
            return gridPanel;
        }
        String childrenPath = getSubPath(pathToElement, "children");
        for (StringHolder childIdHolder : children.contents.keySet()) {
            WWidget child = container.parseGUIWidget(children, childIdHolder.str, childrenPath, context);
            if (child != null) {
                gridPanel.add(child, child.getX(), child.getY(), child.getWidth(), child.getHeight());
            }
        }
        return gridPanel;
    }
}
