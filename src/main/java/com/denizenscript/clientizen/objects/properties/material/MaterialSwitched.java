package com.denizenscript.clientizen.objects.properties.material;

import com.denizenscript.clientizen.objects.properties.material.internal.MaterialBooleanProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class MaterialSwitched extends MaterialBooleanProperty {

    // <--[property]
    // @object MaterialTag
    // @name switched
    // @input ElementTag(Boolean)
    // @description
    // Whether a material is 'switched on', which has different semantic meaning depending on the material type.
    // More specifically, this is whether:
    // - a Powerable material (like pressure plates) is activated
    // - an Openable material (like doors) is open
    // - a dispenser is powered and should dispense its contents
    // - a daylight sensor is inverted (detects darkness instead of light)
    // - a lightable block is lit
    // - a piston block is extended
    // - an end portal frame has an ender eye in it
    // - a hopper is NOT being powered by redstone
    // - a sculk_shrieker can summon a warden
    // -->
    public static final BooleanProperty[] handledProperties = {
            BlockStateProperties.POWERED, BlockStateProperties.OPEN, BlockStateProperties.TRIGGERED, BlockStateProperties.INVERTED, BlockStateProperties.LIT, BlockStateProperties.EXTENDED, BlockStateProperties.EYE, BlockStateProperties.ENABLED, BlockStateProperties.CAN_SUMMON
    };

    public static void register() {
        autoRegister("switched", MaterialSwitched.class);
    }
}
