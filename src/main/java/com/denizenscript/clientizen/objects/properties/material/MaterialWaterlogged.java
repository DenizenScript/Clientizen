package com.denizenscript.clientizen.objects.properties.material;

import com.denizenscript.clientizen.objects.properties.material.internal.MaterialBooleanProperty;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;

public class MaterialWaterlogged extends MaterialBooleanProperty {

    // <--[property]
    // @object MaterialTag
    // @name waterlogged
    // @input ElementTag(Boolean)
    // @description
    // Whether a block is waterlogged.
    // -->

    public static final BooleanProperty[] handledProperties = {Properties.WATERLOGGED};

    public static void register() {
        autoRegister("waterlogged", MaterialWaterlogged.class);
    }
}
