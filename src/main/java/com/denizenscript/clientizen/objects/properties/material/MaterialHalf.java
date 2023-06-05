package com.denizenscript.clientizen.objects.properties.material;

import com.denizenscript.clientizen.objects.properties.material.internal.MaterialEnumProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;

public class MaterialHalf extends MaterialEnumProperty {

	// <--[property]
	// @object MaterialTag
	// @name half
	// @input ElementTag
	// @description
	// The current half for a bisected material (like a door, double-plant, chest, or a bed).
	// Output for "Bisected" blocks (doors/double plants/...) is "BOTTOM" or "TOP".
	// Output for beds is "HEAD" or "FOOT".
	// Output for chests is "LEFT" or "RIGHT".
	// -->

	// TODO: material properties - bukkit conversion, verify all relevant properties are included, Denizen compact ('SINGLE' chests should be null)
    public static final EnumProperty<?>[] handledProperties = {Properties.BED_PART, Properties.CHEST_TYPE};

    public static void register() {
        autoRegister("half", MaterialHalf.class);
    }
}
