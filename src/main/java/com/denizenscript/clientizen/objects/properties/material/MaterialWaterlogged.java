package com.denizenscript.clientizen.objects.properties.material;

import com.denizenscript.clientizen.objects.properties.material.internal.MaterialBooleanProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class MaterialWaterlogged extends MaterialBooleanProperty {

    // <--[property]
    // @object MaterialTag
    // @name waterlogged
    // @input ElementTag(Boolean)
    // @description
    // Whether a block is waterlogged.
    // -->

    public static final BooleanProperty[] handledProperties = {BlockStateProperties.WATERLOGGED};

    public static void register() {
        autoRegister("waterlogged", MaterialWaterlogged.class);
    }
}
