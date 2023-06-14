package com.denizenscript.clientizen.objects.properties.material;

import com.denizenscript.clientizen.objects.properties.material.internal.MaterialEnumProperty;
import net.minecraft.block.enums.ChestType;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;

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

    public static final EnumProperty<?>[] handledProperties = {Properties.BED_PART, Properties.CHEST_TYPE, Properties.DOUBLE_BLOCK_HALF};

    enum BukkitDoubleBlockHalf implements EnumStringIdentifiable { TOP, BOTTOM }

    static {
        convertEnum(Properties.DOUBLE_BLOCK_HALF, BukkitDoubleBlockHalf.class);
        removeSingleInstance(Properties.CHEST_TYPE, ChestType.SINGLE);
    }

    public static void register() {
        autoRegister("half", MaterialHalf.class);
    }
}
