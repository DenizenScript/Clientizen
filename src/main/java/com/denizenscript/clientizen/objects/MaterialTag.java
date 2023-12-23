package com.denizenscript.clientizen.objects;

import com.denizenscript.clientizen.util.Utilities;
import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.*;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class MaterialTag implements ObjectTag, Adjustable {

    // <--[ObjectType]
    // @name MaterialTag
    // @prefix m
    // @base ElementTag
    // @implements PropertyHolderObject
    // @ExampleTagBase material[stone]
    // @ExampleValues stone,dirt,stick,iron_sword
    // @ExampleForReturns
    // - narrate "The material is %VALUE%!"
    // @format
    // The identity format for materials is the material type name.
    // For example, 'm@stick'.
    //
    // @description
    // A MaterialTag represents a type of block or item.
    //
    // Block materials may sometimes also contain property data
    // for specific values on the block material such as the growth stage of a plant or the orientation of a stair block.
    //
    // Material types: <@link url https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html>.
    //
    // @Matchable
    // MaterialTag matchers, sometimes identified as "<material>":
    // "material" plaintext: always matches.
    // "block" plaintext: matches if the material is a block-type material.
    // "item" plaintext: matches if the material is an item-type material.
    // Any block/item name: matches if the material is of the given type, using advanced matchers.
    //
    // -->

    // Needs to match the server-side impl, which uses bukkit's all-in-one Material enum
    public BlockState state;
    public Item item;

    public MaterialTag(BlockState state) {
        this.state = state;
    }

    public MaterialTag(Block block) {
        this.state = block.getDefaultState();
    }

    public MaterialTag(Item item) {
        this.item = item;
    }

    @Fetchable("m")
    public static MaterialTag valueOf(String string, TagContext context) {
        if (string == null) {
            return null;
        }
        if (ObjectFetcher.isObjectWithProperties(string)) {
            return ObjectFetcher.getObjectFromWithProperties(ClientizenObjectRegistry.TYPE_MATERIAL, string, context);
        }
        if (string.startsWith("m@")) {
            string = string.substring("m@".length());
        }
        Identifier identifier = Identifier.tryParse(string);
        if (identifier == null) {
            if (context == null || context.showErrors()) {
                Debug.echoError("valueOf MaterialTag returning null, invalid material/item name or identifier specified. For input: " + string);
            }
            return null;
        }
        MaterialTag material = Registries.BLOCK.getOrEmpty(identifier).map(MaterialTag::new)
                .orElseGet(() -> Registries.ITEM.getOrEmpty(identifier).map(MaterialTag::new).orElse(null));
        if (material != null) {
            return material;
        }
        if (context == null || context.showErrors()) {
            Debug.echoError("valueOf MaterialTag returning null, invalid material/item specified. For input: " + string);
        }
        return null;
    }

    public static boolean matches(String string) {
        if (string.startsWith("m@")) {
            return true;
        }
        return valueOf(string, CoreUtilities.noDebugContext) != null;
    }

    public String getName() {
        return Utilities.idToString(state != null ? Registries.BLOCK.getId(state.getBlock()) : Registries.ITEM.getId(item));
    }

    public boolean isBlock() {
        return state != null || Registries.BLOCK.containsId(Registries.ITEM.getId(item));
    }

    public boolean isItem() {
        return item != null || Registries.ITEM.containsId(Registries.BLOCK.getId(state.getBlock()));
    }

    public static void register() {
        PropertyParser.registerPropertyTagHandlers(MaterialTag.class, tagProcessor);

        tagProcessor.registerTag(ElementTag.class, "name", (attribute, object) -> {
            return new ElementTag(object.getName());
        });
    }

    public static final ObjectTagProcessor<MaterialTag> tagProcessor = new ObjectTagProcessor<>();

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

    @Override
    public String identify() {
        return "m@" + getName() + PropertyParser.getPropertiesString(this);
    }

    @Override
    public String identifySimple() {
        return "m@" + getName();
    }

    @Override
    public String debuggable() {
        return "<LG>m@<Y>" + getName() + PropertyParser.getPropertiesDebuggable(this);
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
    public ObjectTag duplicate() {
        return state != null ? new MaterialTag(state) : new MaterialTag(item);
    }

    @Override
    public boolean advancedMatches(String matcher) {
        return ScriptEvent.createMatcher(matcher).doesMatch(getName(), text ->
                switch (text) {
                    case "material" -> true;
                    case "block" -> isBlock();
                    case "item" -> isItem();
                    default -> false;
                }
        );
    }

    private String prefix = "Material";

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
