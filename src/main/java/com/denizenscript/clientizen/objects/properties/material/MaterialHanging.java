package com.denizenscript.clientizen.objects.properties.material;

import com.denizenscript.clientizen.objects.properties.material.internal.MaterialBooleanProperty;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;

public class MaterialHanging extends MaterialBooleanProperty {

    public static final BooleanProperty[] handledProperties = {Properties.HANGING};

    public static void register() {
        autoRegister("hanging", MaterialHanging.class);
    }
}
