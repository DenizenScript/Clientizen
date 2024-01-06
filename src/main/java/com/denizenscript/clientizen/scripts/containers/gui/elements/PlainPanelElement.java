package com.denizenscript.clientizen.scripts.containers.gui.elements;

import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.text.StringHolder;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;

import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.applyBackgroundPainter;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.applyInsets;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getSubPath;

public class PlainPanelElement implements GuiScriptContainer.GuiElementParser {

    // <--[language]
    // @name Plain Panel GUI Element
    // @group GUI System
    // @description
    // Plain panels are the most basic type of panels, allowing their contained elements full control; they have a UI type of "plain_panel".
    // Unlike other panel types, plain panels do not organize their contained elements in any way,
    // giving them complete freedom to directly set their position/location.
    //
    // <code>
    // ui_type: plain_panel
    // # The plain panel's insets, optional.
    // insets: <GUI Insets>
    // # The plain panel's background, optional.
    // background: <GUI Background>
    // # The plain panel's contained elements, optional.
    // content:
    //     <key>: <GUI Element>
    // </code>
    // -->

    @Override
    public WWidget parse(GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context) {
        WPlainPanel plainPanel = new WPlainPanel();
        applyInsets(config, plainPanel::setInsets, context);
        applyBackgroundPainter(plainPanel, config, context);
        YamlConfiguration content = config.getConfigurationSection("content");
        if (content == null) {
            return plainPanel;
        }
        String contentPath = getSubPath(pathToElement, "content");
        for (StringHolder contentIdHolder : content.contents.keySet()) {
            WWidget child = container.parseGUIWidget(content, contentIdHolder.str, contentPath, context);
            if (child != null) {
                plainPanel.add(child, child.getX(), child.getY(), child.getWidth(), child.getHeight());
            }
        }
        return plainPanel;
    }
}
