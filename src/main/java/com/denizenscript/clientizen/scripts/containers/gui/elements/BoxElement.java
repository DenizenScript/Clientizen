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

import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.*;

public class BoxElement implements GuiScriptContainer.GuiElementParser {

    @Override
    public WWidget parse(GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context) {
        Axis axis = getTaggedEnum(Axis.class, config, "axis", context);
        if (axis == null) {
            Debug.echoError("must specify an axis.");
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
        for (StringHolder contentIdHolder : content.contents.keySet()) {
            WWidget child = container.parseGUIWidget(content, contentIdHolder.str, pathToElement + ".content", context);
            if (child != null) {
                box.add(child, child.getWidth(), child.getHeight());
            }
        }
        return box;
    }
}
