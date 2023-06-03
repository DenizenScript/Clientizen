package com.denizenscript.clientizen.objects;

import com.denizenscript.clientizen.util.Utilities;
import com.denizenscript.denizencore.objects.*;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ItemTag implements ObjectTag, Adjustable {

    final ItemStack itemStack;

    public ItemTag(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemTag(ItemConvertible convertible) {
        this(convertible.asItem().getDefaultStack());
    }

    @Fetchable("i")
    public static ItemTag valueOf(String string, TagContext context) {
        if (string.startsWith("i@")) {
            string = string.substring("i@".length());
        }
        if (ObjectFetcher.isObjectWithProperties(string)) {
            return ObjectFetcher.getObjectFromWithProperties(ClientizenObjectRegistry.TYPE_ITEM, string, context);
        }
        MaterialTag material = MaterialTag.valueOf(string, context);
        if (material == null) {
            if (context == null || context.showErrors()) {
                Debug.echoError("valueOf ItemTag returning null: invalid item type '" + string + "' specified.");
            }
            return null;
        }
        return new ItemTag(material.state != null ? material.state.getBlock() : material.item);
    }

    public static boolean matches(String string) {
        if (string.startsWith("i@")) {
            return true;
        }
        Identifier identifier = Identifier.tryParse(string);
        return identifier != null && (Registries.ITEM.containsId(identifier) || Registries.BLOCK.containsId(identifier));
    }

    public static void register() {
        PropertyParser.registerPropertyTagHandlers(ItemTag.class, tagProcessor);
    }

    public static final ObjectTagProcessor<ItemTag> tagProcessor = new ObjectTagProcessor<>();

    @Override
    public ObjectTag getObjectAttribute(Attribute attribute) {
        return tagProcessor.getObjectAttribute(this, attribute);
    }

    @Override
    public void adjust(Mechanism mechanism) {
        tagProcessor.processMechanism(this, mechanism);
    }

    @Override
    public void applyProperty(Mechanism mechanism) {
        adjust(mechanism);
    }

    public ItemStack getStack() {
        return itemStack;
    }

    public String getKey() {
        return Utilities.idToString(Registries.ITEM.getId(itemStack.getItem()));
    }

    @Override
    public String identify() {
        return "i@" + getKey() + PropertyParser.getPropertiesString(this);
    }

    @Override
    public String identifySimple() {
        return "i@" + getKey();
    }

    @Override
    public String debuggable() {
        return "<LG>i@<Y>" + getKey() + PropertyParser.getPropertiesDebuggable(this);
    }

    @Override
    public String toString() {
        return identify();
    }

    @Override
    public boolean isUnique() {
        return false;
    }

    String prefix = "Item";

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public ObjectTag setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }
}
