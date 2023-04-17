package com.denizenscript.clientizen.objects.properties.material;

import com.denizenscript.clientizen.objects.properties.material.internal.MaterialBooleanProperty;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;

public class MaterialWaterlogged extends MaterialBooleanProperty {

    public static final BooleanProperty[] handledProperties = {Properties.WATERLOGGED};

    @Override
    public String getPropertyId() {
        return "waterlogged";
    }

    public static void register() {
        autoRegister("waterlogged", MaterialWaterlogged.class);
    }
}
