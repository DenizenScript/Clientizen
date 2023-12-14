package com.denizenscript.clientizen.scripts.containers.gui.elements;

import com.denizenscript.clientizen.objects.ItemTag;
import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer;
import com.denizenscript.denizencore.objects.core.DurationTag;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import io.github.cottonmc.cotton.gui.widget.WItem;
import io.github.cottonmc.cotton.gui.widget.WWidget;

import java.util.List;

import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedObject;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedObjectList;

public class ItemElement implements GuiScriptContainer.GuiElementParser {

    @Override
    public WWidget parse(GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context) {
        List<ItemTag> items = getTaggedObjectList(ItemTag.class, config, "items", context);
        if (items == null) {
            Debug.echoError("Must specify a list of items.");
            return null;
        }
        WItem item = new WItem(items.stream().map(ItemTag::getStack).toList());
        DurationTag duration = getTaggedObject(DurationTag.class, config, "duration", context);
        if (duration != null) {
            item.setDuration(duration.getTicksAsInt());
        }
        return item;
    }
}