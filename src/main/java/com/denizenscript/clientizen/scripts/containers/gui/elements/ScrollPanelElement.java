package com.denizenscript.clientizen.scripts.containers.gui.elements;

import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.fabricmc.fabric.api.util.TriState;

import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.applyInsets;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedEnum;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedInt;

public class ScrollPanelElement implements GuiScriptContainer.GuiElementParser {

    @Override
    public WWidget parse(GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context) {
        WWidget content = container.parseGUIWidget(config, "content", pathToElement, context);
        if (content == null) {
            Debug.echoError("Must have valid content.");
            return null;
        }
        WScrollPanel scrollPanel = new WScrollPanel(content);
        applyInsets(config, scrollPanel::setInsets, context);
        TriState verticalScroll = getTaggedEnum(TriState.class, config, "vertical_scroll", context);
        TriState horizontalScroll = getTaggedEnum(TriState.class, config, "horizontal_scroll", context);
        if (verticalScroll != null) {
            scrollPanel.setScrollingVertically(verticalScroll);
        }
        if (horizontalScroll != null) {
            scrollPanel.setScrollingHorizontally(horizontalScroll);
        }
        Integer verticalScrollSpeed = getTaggedInt(config, "vertical_scroll_speed", context);
        Integer horizontalScrollSpeed = getTaggedInt(config, "horizontal_scroll_speed", context);
        if (verticalScrollSpeed != null) {
            scrollPanel.getVerticalScrollBar().setScrollingSpeed(verticalScrollSpeed);
        }
        if (horizontalScrollSpeed != null) {
            scrollPanel.getHorizontalScrollBar().setScrollingSpeed(horizontalScrollSpeed);
        }
        return scrollPanel;
    }
}
