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

    // <--[language]
    // @name Item GUI Element
    // @group GUI System
    // @description
    // Item GUI elements show an item, potentially looping over a list of items and switching between them; they have a UI type of "item".
    //
    // <code>
    // ui_type: item
    // # The item (or list of items) to display, required.
    // items: <ListTag(ItemTag)>
    // # The duration to show each item for when more than one is specified, optional.
    // duration: <DurationTag>
    // </code>
    // -->

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
