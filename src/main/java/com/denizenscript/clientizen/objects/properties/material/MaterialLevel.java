package com.denizenscript.clientizen.objects.properties.material;

import com.denizenscript.clientizen.mixin.IntPropertyAccessor;
import com.denizenscript.clientizen.objects.properties.material.internal.MaterialIntProperty;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;

public class MaterialLevel extends MaterialIntProperty {

    public static final IntProperty[] handledProperties = {
            Properties.CANDLES, Properties.BITES, Properties.LEVEL_3, Properties.LEVEL_8, Properties.LEVEL_1_8, Properties.LEVEL_15
    };

    @Override
    public String getPropertyId() {
        return "level";
    }

    public static void register() {
        PropertyParser.registerTag(MaterialLevel.class, ElementTag.class, "minimum_level", (attribute, prop) -> {
            return new ElementTag(((IntPropertyAccessor) prop.internalProperty).getMin());
        });
        PropertyParser.registerTag(MaterialLevel.class, ElementTag.class, "maximum_level", (attribute, prop) -> {
            return new ElementTag(((IntPropertyAccessor) prop.internalProperty).getMax());
        });
        autoRegister("level", MaterialLevel.class);
    }
}
