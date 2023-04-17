package com.denizenscript.clientizen.objects.properties;

import com.denizenscript.clientizen.objects.properties.material.*;

import static com.denizenscript.clientizen.objects.properties.material.internal.MaterialEnumProperty.registerEnumProperty;
import static com.denizenscript.clientizen.objects.properties.material.internal.MaterialMinecraftProperty.registerProperty;

public class PropertyRegistry {

    public static void register() {
        registerProperty(MaterialSwitched.class, MaterialSwitched.handledProperties);
        registerProperty(MaterialWaterlogged.class, MaterialWaterlogged.handledProperties);
        registerProperty(MaterialHanging.class, MaterialHanging.handledProperties);
        registerEnumProperty(MaterialHalf.class, MaterialHalf.handledProperties);
        registerEnumProperty(MaterialInstrument.class, MaterialInstrument.handledProperties);
        registerProperty(MaterialLevel.class, MaterialLevel.handledProperties);
    }
}
