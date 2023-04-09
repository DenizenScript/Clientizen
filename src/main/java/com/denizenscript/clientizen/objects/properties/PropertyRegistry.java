package com.denizenscript.clientizen.objects.properties;

import com.denizenscript.clientizen.objects.properties.material.MaterialHalf;
import com.denizenscript.clientizen.objects.properties.material.MaterialHanging;
import com.denizenscript.clientizen.objects.properties.material.MaterialSwitched;
import com.denizenscript.clientizen.objects.properties.material.MaterialWaterlogged;

import static com.denizenscript.clientizen.objects.properties.material.internal.MaterialMinecraftProperty.registerProperty;
import static com.denizenscript.clientizen.objects.properties.material.internal.MaterialMinecraftProperty.registerEnumProperty;

public class PropertyRegistry {

    public static void register() {
        registerProperty(MaterialSwitched.class, MaterialSwitched.handledProperties);
        registerProperty(MaterialWaterlogged.class, MaterialWaterlogged.handledProperties);
        registerProperty(MaterialHanging.class, MaterialHanging.handledProperties);
        registerEnumProperty(MaterialHalf.class, MaterialHalf.handledProperties);
//        registerProperty("instrument", MaterialEnumProperty::new, MaterialEnumProperty.class, Properties.INSTRUMENT);
//        registerProperty("level", MaterialLevel::new, MaterialLevel.class, MaterialLevel.handledProperties);
    }
}
