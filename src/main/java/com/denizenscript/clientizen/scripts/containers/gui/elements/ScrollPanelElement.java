package com.denizenscript.clientizen.scripts.containers.gui.elements;

import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.fabricmc.fabric.api.util.TriState;

public class ScrollPanelElement implements GuiScriptContainer.GuiElementParser {

    @Override
    public WWidget parse(GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context) {
        WWidget content = container.parseGUIWidget(config, "content", pathToElement, context);
        if (content == null) {
            Debug.echoError(context, "Invalid scroll panel '" + pathToElement + "': must have valid content.");
            return null;
        }
        WScrollPanel scrollPanel = new WScrollPanel(content);
        TriState verticalScroll = GuiScriptContainer.getEnum(TriState.class, config, pathToElement, "vertical_scroll", context);
        TriState horizontalScroll = GuiScriptContainer.getEnum(TriState.class, config, pathToElement, "horizontal_scroll", context);
        if (verticalScroll != null) {
            scrollPanel.setScrollingVertically(verticalScroll);
        }
        if (horizontalScroll != null) {
            scrollPanel.setScrollingHorizontally(horizontalScroll);
        }
        return scrollPanel;
    }
}
