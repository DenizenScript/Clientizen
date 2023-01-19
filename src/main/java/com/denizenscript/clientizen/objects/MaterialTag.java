package com.denizenscript.clientizen.objects;

import com.denizenscript.clientizen.util.Utilities;
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
import net.minecraft.state.State;
import net.minecraft.util.Identifier;

public class MaterialTag implements ObjectTag, Adjustable {

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
            return ObjectFetcher.getObjectFromWithProperties(MaterialTag.class, string, context);
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
                .orElse(Registries.ITEM.getOrEmpty(identifier).map(MaterialTag::new).orElse(null));
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
        return Utilities.stringifyIdentifier(state != null ? Registries.BLOCK.getId(state.getBlock()) : Registries.ITEM.getId(item));
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

    private String prefix = "Entity";

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
