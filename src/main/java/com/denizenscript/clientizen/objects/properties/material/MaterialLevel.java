package com.denizenscript.clientizen.objects.properties.material;

import com.denizenscript.clientizen.objects.properties.material.internal.MaterialIntProperty;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class MaterialLevel extends MaterialIntProperty {

    // <--[property]
    // @object MaterialTag
    // @name level
    // @input ElementTag(Number)
    // @description
    // The current level for a "levelled" material:
    // For light blocks, this is the brightness of the light.
    // For water/lava this is the height of the liquid block.
    // For cauldrons, this is the amount of liquid contained.
    // For cake, this is the number of bites left.
    // For beehives/bee nests, this is the amount of honey contained.
    // For snow, this is the number of partial layers, or the height, of a snow block.
    // For farmland, this is the moisture level.
    // For composters, this is the amount of compost.
    // -->

    // <--[tag]
    // @attribute <MaterialTag.minimum_level>
    // @returns ElementTag(Number)
    // @group properties
    // @description
    // Returns the minimum level for a "levelled" material (see <@link property MaterialTag.level>).
    // -->

    // <--[tag]
    // @attribute <MaterialTag.maximum_level>
    // @returns ElementTag(Number)
    // @group properties
    // @description
    // Returns the maximum level for a "levelled" material (see <@link property MaterialTag.level>).
    // -->
    public static final IntegerProperty[] handledProperties = {
            BlockStateProperties.LEVEL, BlockStateProperties.LEVEL_CAULDRON, BlockStateProperties.BITES, BlockStateProperties.LEVEL_HONEY, BlockStateProperties.LAYERS, BlockStateProperties.MOISTURE, BlockStateProperties.LEVEL_COMPOSTER, BlockStateProperties.LEVEL_FLOWING
    };

    public static void register() {
        PropertyParser.registerTag(MaterialLevel.class, ElementTag.class, "minimum_level", (attribute, prop) -> {
            return new ElementTag(prop.getAccessor().getMin());
        });
        PropertyParser.registerTag(MaterialLevel.class, ElementTag.class, "maximum_level", (attribute, prop) -> {
            return new ElementTag(prop.getAccessor().getMax());
        });
        autoRegister("level", MaterialLevel.class);
    }
}
