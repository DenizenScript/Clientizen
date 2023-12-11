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
            Debug.echoError("must have valid content.");
            return null;
        }
        WScrollPanel scrollPanel = new WScrollPanel(content);
        GuiScriptContainer.applyInsets(config, scrollPanel::setInsets, context);
        TriState verticalScroll = GuiScriptContainer.getTaggedEnum(TriState.class, config, "vertical_scroll", context);
        TriState horizontalScroll = GuiScriptContainer.getTaggedEnum(TriState.class, config, "horizontal_scroll", context);
        if (verticalScroll != null) {
            scrollPanel.setScrollingVertically(verticalScroll);
        }
        if (horizontalScroll != null) {
            scrollPanel.setScrollingHorizontally(horizontalScroll);
        }
        Integer verticalScrollSpeed = GuiScriptContainer.getTaggedInt(config, "vertical_scroll_speed", context);
        Integer horizontalScrollSpeed = GuiScriptContainer.getTaggedInt(config, "horizontal_scroll_speed", context);
        if (verticalScrollSpeed != null) {
            scrollPanel.getVerticalScrollBar().setScrollingSpeed(verticalScrollSpeed);
        }
        if (horizontalScrollSpeed != null) {
            scrollPanel.getHorizontalScrollBar().setScrollingSpeed(horizontalScrollSpeed);
        }
        return scrollPanel;
    }
}
