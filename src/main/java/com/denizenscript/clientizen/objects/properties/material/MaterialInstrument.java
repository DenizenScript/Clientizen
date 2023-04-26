package com.denizenscript.clientizen.objects.properties.material;

import com.denizenscript.clientizen.objects.properties.material.internal.MaterialEnumProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;

public class MaterialInstrument extends MaterialEnumProperty {

    public static EnumProperty<?>[] handledProperties = {Properties.INSTRUMENT};

    public static void register() {
        autoRegister("instrument", MaterialInstrument.class);
    }
}
