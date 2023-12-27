package com.denizenscript.clientizen.scripts.containers.gui.elements;

import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.denizenscript.denizencore.utilities.text.StringHolder;
import io.github.cottonmc.cotton.gui.widget.WBox;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment;

import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.applyInsets;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getSubPath;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedEnum;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedInt;

public class BoxPanelElement implements GuiScriptContainer.GuiElementParser {

    // <--[language]
    // @name Box Panel GUI Element
    // @group GUI System
    // @description
    // Boxes are a type of panel that that sort the elements they contain evenly along an axis; they have a UI type of "box_panel".
    //
    // <code>
    // ui_type: box_panel
    // # The axis to sort the elements in the box panel along, required.
    // axis: HORIZONTAL/VERTICAL
    // # The box panel's insets, optional.
    // insets: <GUI Insets>
    // # The spacing between elements in the box panel, optional.
    // spacing: <number>
    // # The vertical alignment for elements in the box panel, optional.
    // vertical_alignment: TOP/CENTER/BOTTOM
    // # The horizontal alignment for elements in the box panel, optional.
    // horizontal_alignment: LEFT/CENTER/RIGHT
    // # The elements in the box panel, optional.
    // content:
    //     <key>: <GUI Element>
    // </code>
    // -->

    @Override
    public WWidget parse(GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context) {
        Axis axis = getTaggedEnum(Axis.class, config, "axis", context);
        if (axis == null) {
            Debug.echoError("Must specify an axis.");
            return null;
        }
        WBox box = new WBox(axis);
        applyInsets(config, box::setInsets, context);
        Integer spacing = getTaggedInt(config, "spacing", context);
        if (spacing != null) {
            box.setSpacing(spacing);
        }
        VerticalAlignment verticalAlignment = getTaggedEnum(VerticalAlignment.class, config, "vertical_alignment", context);
        if (verticalAlignment != null) {
            box.setVerticalAlignment(verticalAlignment);
        }
        HorizontalAlignment horizontalAlignment = getTaggedEnum(HorizontalAlignment.class, config, "horizontal_alignment", context);
        if (horizontalAlignment != null) {
            box.setHorizontalAlignment(horizontalAlignment);
        }
        YamlConfiguration content = config.getConfigurationSection("content");
        if (content == null) {
            return box;
        }
        String contentPath = getSubPath(pathToElement, "content");
        for (StringHolder contentIdHolder : content.contents.keySet()) {
            WWidget child = container.parseGUIWidget(content, contentIdHolder.str, contentPath, context);
            if (child != null) {
                box.add(child, child.getWidth(), child.getHeight());
            }
        }
        return box;
    }
}
