package com.denizenscript.clientizen.scripts.containers.gui.elements;

import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.fabricmc.fabric.api.util.TriState;

import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.applyBackgroundPainter;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.applyInsets;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedEnum;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedInt;

public class ScrollPanelElement implements GuiScriptContainer.GuiElementParser {

    // <--[language]
    // @name Scroll Panel GUI Element
    // @group GUI System
    // @description
    // Scroll panels are a type of panel that lets you scroll through its content; they have a UI type of "scroll_panel".
    //
    // <code>
    // # The scroll panel's contained GUI element, required.
    // content: <GUI Element>
    // # The scroll panel's insets, optional.
    // insets: <GUI Insets>
    // # The scroll panel's background, optional.
    // background: <GUI Background>
    // # Whether the scroll panel should have a vertical scroll bar, optional (defaults to DEFAULT).
    // vertical_scroll: FALSE/DEFAULT (automatically add/remove based on the content's size)/TRUE
    // # Whether the scroll panel should have a horizontal scroll bar, optional (defaults to DEFAULT).
    // horizontal_scroll: FALSE/DEFAULT (automatically add/remove based on the content's size)/TRUE
    // # The scroll panel's vertical scroll speed, optional.
    // vertical_scroll_speed: <number>
    // # The scroll panel's horizontal scroll speed, optional.
    // horizontal_scroll_speed: <number>
    // </code>
    // -->

    @Override
    public WWidget parse(GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context) {
        WWidget content = container.parseGUIWidget(config, "content", pathToElement, context);
        if (content == null) {
            Debug.echoError("Must have valid content.");
            return null;
        }
        WScrollPanel scrollPanel = new WScrollPanel(content);
        applyInsets(config, scrollPanel::setInsets, context);
        applyBackgroundPainter(scrollPanel, config, context);
        TriState verticalScroll = getTaggedEnum(TriState.class, config, "vertical_scroll", context);
        if (verticalScroll != null) {
            scrollPanel.setScrollingVertically(verticalScroll);
        }
        TriState horizontalScroll = getTaggedEnum(TriState.class, config, "horizontal_scroll", context);
        if (horizontalScroll != null) {
            scrollPanel.setScrollingHorizontally(horizontalScroll);
        }
        Integer verticalScrollSpeed = getTaggedInt(config, "vertical_scroll_speed", context);
        if (verticalScrollSpeed != null) {
            scrollPanel.getVerticalScrollBar().setScrollingSpeed(verticalScrollSpeed);
        }
        Integer horizontalScrollSpeed = getTaggedInt(config, "horizontal_scroll_speed", context);
        if (horizontalScrollSpeed != null) {
            scrollPanel.getHorizontalScrollBar().setScrollingSpeed(horizontalScrollSpeed);
        }
        return scrollPanel;
    }
}
