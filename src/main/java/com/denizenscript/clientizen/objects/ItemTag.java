package com.denizenscript.clientizen.objects;

import com.denizenscript.clientizen.util.Utilities;
import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.*;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ItemTag implements ObjectTag, Adjustable {

    // <--[ObjectType]
    // @name ItemTag
    // @prefix i
    // @base ElementTag
    // @implements PropertyHolderObject
    // @ExampleTagBase client.self_entity.item_in_hand
    // @ExampleValues <client.self_entity.item_in_hand>,stick,iron_sword
    // @ExampleForReturns
    // - narrate "The item is %VALUE%"
    // @format
    // The identity format for items is the basic material type name. Other data is specified in properties.
    // For example, 'i@stick'.
    //
    // @description
    // An ItemTag represents a holdable item generically.
    //
    // ItemTags do NOT remember where they came from. If you read an item from somewhere, changing it
    // does not change the original item. You must set it back in.
    //
    // Find a list of valid materials at <@link url https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html>
    // Note that some materials on that list are exclusively for use with blocks, and cannot be held as items.
    //
    // @Matchable
    // ItemTag matchers, sometimes identified as "<item>":
    // "item" plaintext: always matches.
    // "item_enchanted:<enchantment>": matches if the item is enchanted with the given enchantment (excluding enchantment books), allows advanced matchers.
    // "server_script:<name>": matches if the item is from the given server-side item script, allows advanced matchers.
    // Any item name: matches if the item is of the given type, using advanced matchers.
    //
    // -->

    final ItemStack itemStack;
    final Identifier identifier;
    public String script; // Compact with server-side item scripts

    public ItemTag(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.identifier = Registries.ITEM.getId(itemStack.getItem());
    }

    public ItemTag(ItemConvertible convertible) {
        this(convertible.asItem().getDefaultStack());
    }

    public ItemTag(MaterialTag material) {
        this(material.item != null ? material.item : material.state.getBlock());
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
        return new ItemTag(material);
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

    public String getName() {
        return Utilities.idToString(identifier);
    }

    @Override
    public String identify() {
        return "i@" + getName() + PropertyParser.getPropertiesString(this);
    }

    @Override
    public String identifySimple() {
        return "i@" + getName();
    }

    @Override
    public String debuggable() {
        return "<LG>i@<Y>" + getName() + PropertyParser.getPropertiesDebuggable(this);
    }

    @Override
    public String toString() {
        return identify();
    }

    @Override
    public boolean isUnique() {
        return false;
    }

    @Override
    public boolean advancedMatches(String matcher) {
        return ScriptEvent.createMatcher(matcher).doesMatch(getName(), text -> {
            if (text.equals("item")) {
                return true;
            }
            int colonIndex = text.indexOf(':');
            if (colonIndex != -1) {
                String prefix = text.substring(0, colonIndex);
                String value = text.substring(colonIndex + 1);
                switch (prefix) {
                    case "item_enchanted" -> {
                        NbtList enchantments = getStack().getEnchantments();
                        if (enchantments.isEmpty()) {
                            return false;
                        }
                        ScriptEvent.MatchHelper matchHelper = ScriptEvent.createMatcher(value);
                        for (Enchantment enchantment : EnchantmentHelper.fromNbt(enchantments).keySet()) {
                            if (matchHelper.doesMatch(Utilities.idToString(Registries.ENCHANTMENT.getId(enchantment)))) {
                                return true;
                            }
                        }
                        return false;
                    }
                    case "server_script" -> {
                        return script != null && ScriptEvent.runGenericCheck(value, script);
                    }
                }
            }
            return false;
        });
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
