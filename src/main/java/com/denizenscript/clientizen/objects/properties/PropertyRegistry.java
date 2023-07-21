package com.denizenscript.clientizen.objects.properties;

import com.denizenscript.clientizen.objects.EntityTag;
import com.denizenscript.clientizen.objects.ItemTag;
import com.denizenscript.clientizen.objects.properties.entity.EntitySheared;
import com.denizenscript.clientizen.objects.properties.item.ItemDurability;
import com.denizenscript.clientizen.objects.properties.item.ItemServerScript;
import com.denizenscript.clientizen.objects.properties.material.*;
import com.denizenscript.denizencore.objects.properties.PropertyParser;

import static com.denizenscript.clientizen.objects.properties.material.internal.MaterialEnumProperty.registerProperty;

public class PropertyRegistry {

    public static void register() {
        // Material properties
        registerProperty(MaterialSwitched.class, MaterialSwitched.handledProperties);
        registerProperty(MaterialWaterlogged.class, MaterialWaterlogged.handledProperties);
        registerProperty(MaterialHalf.class, MaterialHalf.handledProperties);
        registerProperty(MaterialInstrument.class, MaterialInstrument.handledProperties);
        registerProperty(MaterialLevel.class, MaterialLevel.handledProperties);

        // Item properties
        PropertyParser.registerProperty(ItemDurability.class, ItemTag.class);
        PropertyParser.registerProperty(ItemServerScript.class, ItemTag.class);

        // Entity properties
        PropertyParser.registerProperty(EntitySheared.class, EntityTag.class);
    }
}
