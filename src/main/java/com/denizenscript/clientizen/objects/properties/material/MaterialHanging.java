package com.denizenscript.clientizen.objects.properties.material;

import com.denizenscript.clientizen.objects.properties.material.internal.MaterialBooleanProperty;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;

public class MaterialHanging extends MaterialBooleanProperty {

    public static final BooleanProperty[] handledProperties = {Properties.HANGING};

    @Override
    public boolean isDefaultValue(boolean value) {
        return !value;
    }

    @Override
    public String getPropertyId() {
        return "hanging";
    }

    public static void register() {
        autoRegister("hanging", MaterialHanging.class);
    }
}
