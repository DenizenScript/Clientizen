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

    // <--[language]
    // @name Grid Panel GUI Element
    // @group GUI System
    // @description
    // Grid panels are a type of panel that aligns the elements it contains along a grid of squares (known as cells); they have a UI type of "grid_panel".
    // The x/y/width/height of every element within it is in grid cells instead of individual pixels,
    // which means that, for example, "x: 2" would mean 2 grid cells off.
    //
    // <code>
    // ui_type: grid_panel
    // # The size of each cell in the grid, required.
    // grid_size: <number>
    // # The grid panel's insets, optional.
    // insets: <GUI Insets>
    // # The horizontal spacing between grid cells, optional.
    // horizontal_spacing: <number>
    // # The vertical spacing between grid cells, optional.
    // vertical_spacing: <number>
    // # The GUI elements in the grid panel, optional.
    // content:
    //     <key>: <GUI Element>
    // </code>
    // -->

    @Override
    public WWidget parse(GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context) {
        Integer gridSize = getTaggedInt(config, "grid_size", context);
        if (gridSize == null) {
            Debug.echoError("Must specify a grid size.");
            return null;
        }
        WGridPanel gridPanel = new WGridPanel(gridSize);
        applyInsets(config, gridPanel::setInsets, context);
        Integer horizontalSpacing = getTaggedInt(config, "horizontal_spacing", context);
        Integer verticalSpacing = getTaggedInt(config, "vertical_spacing", context);
        gridPanel.setGaps(horizontalSpacing != null ? horizontalSpacing : 0, verticalSpacing != null ? verticalSpacing : 0);
        YamlConfiguration content = config.getConfigurationSection("content");
        if (content == null) {
            return gridPanel;
        }
        String contentPath = getSubPath(pathToElement, "content");
        for (StringHolder contentIdHolder : content.contents.keySet()) {
            WWidget child = container.parseGUIWidget(content, contentIdHolder.str, contentPath, context);
            if (child != null) {
                gridPanel.add(child, child.getX(), child.getY(), child.getWidth(), child.getHeight());
            }
        }
        return gridPanel;
    }
}
