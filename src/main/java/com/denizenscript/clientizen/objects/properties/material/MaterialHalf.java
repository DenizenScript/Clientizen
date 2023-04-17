package com.denizenscript.clientizen.objects.properties.material;

import com.denizenscript.clientizen.objects.properties.material.internal.MaterialEnumProperty;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.ChestType;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;

public class MaterialHalf extends MaterialEnumProperty {

    public static final EnumProperty<?>[] handledProperties = {Properties.BED_PART, Properties.CHEST_TYPE};

    @Override
    public String getPropertyId() {
        return "half";
    }

    public static void register() {
        autoRegisterEnumProperty("half", MaterialHalf.class);
    }
}
