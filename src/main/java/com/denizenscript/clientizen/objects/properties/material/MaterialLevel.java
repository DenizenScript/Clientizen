package com.denizenscript.clientizen.objects.properties.material;

import com.denizenscript.clientizen.mixin.IntPropertyAccessor;
import com.denizenscript.clientizen.objects.MaterialTag;
import com.denizenscript.clientizen.objects.properties.material.internal.MaterialIntProperty;
import com.denizenscript.denizencore.objects.core.ElementTag;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;

public class MaterialLevel extends MaterialIntProperty {
    public MaterialLevel(String name, MaterialTag material, IntProperty internalProperty) {
        super(name, material, internalProperty);
    }

    public static final IntProperty[] handledProperties = {
            Properties.CANDLES, Properties.BITES, Properties.LEVEL_3, Properties.LEVEL_8, Properties.LEVEL_1_8, Properties.LEVEL_15
    };

    public static void register() {
        registerTag(ElementTag.class, "minimum_level", (attribute, prop) -> {
            return new ElementTag(((IntPropertyAccessor) prop.internalProperty).getMin());
        });
        registerTag(ElementTag.class, "maximum_level", (attribute, prop) -> {
            return new ElementTag(((IntPropertyAccessor) prop.internalProperty).getMax());
        });
        MaterialIntProperty.register();
    }
}
