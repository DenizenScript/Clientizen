package com.denizenscript.clientizen.objects.properties.material;

import com.denizenscript.clientizen.objects.properties.material.internal.MaterialEnumProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;

public class MaterialInstrument extends MaterialEnumProperty {

    public static EnumProperty<?>[] handledProperties = {Properties.INSTRUMENT};

    @Override
    public String getPropertyId() {
        return "instrument";
    }

    public static void register() {
        autoRegisterEnumProperty("instrument", MaterialInstrument.class);
    }
}
