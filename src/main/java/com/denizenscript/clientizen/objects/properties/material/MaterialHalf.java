package com.denizenscript.clientizen.objects.properties.material;

import com.denizenscript.clientizen.objects.properties.material.internal.MaterialEnumProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;

public class MaterialHalf extends MaterialEnumProperty {

    public static final EnumProperty<?>[] handledProperties = {Properties.BED_PART, Properties.CHEST_TYPE};

    public static void register() {
        autoRegisterEnumProperty("half", MaterialHalf.class);
    }
}
