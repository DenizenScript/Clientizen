package com.denizenscript.clientizen.objects.properties.material;

import com.denizenscript.clientizen.objects.properties.material.internal.MaterialEnumProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class MaterialHalf extends MaterialEnumProperty {

    // <--[property]
    // @object MaterialTag
    // @name half
    // @input ElementTag
    // @description
    // The current half for a bisected material (like a door, double-plant, chest, or a bed).
    // Output for "double" (2 block tall) blocks (doors/double plants/...) is "BOTTOM" or "TOP".
    // Output for beds is "HEAD" or "FOOT".
    // Output for chests is "LEFT" or "RIGHT".
    // -->

    public static final EnumProperty<?>[] handledProperties = {BlockStateProperties.BED_PART, BlockStateProperties.CHEST_TYPE, BlockStateProperties.DOUBLE_BLOCK_HALF};

    enum BukkitDoubleBlockHalf implements EnumStringIdentifiable {TOP, BOTTOM}

    public static void register() {
        convertEnum(BlockStateProperties.DOUBLE_BLOCK_HALF, BukkitDoubleBlockHalf.class);
        removeValues(BlockStateProperties.CHEST_TYPE, ChestType.SINGLE);
        autoRegister("half", MaterialHalf.class);
    }
}
