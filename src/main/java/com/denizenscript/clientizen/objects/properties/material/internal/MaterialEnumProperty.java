package com.denizenscript.clientizen.objects.properties.material.internal;

import com.denizenscript.clientizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import net.minecraft.state.property.EnumProperty;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class MaterialEnumProperty extends MaterialMinecraftProperty {

    public static void registerEnumProperty(Class<? extends MaterialEnumProperty> propertyClass, EnumProperty<?>... properties) {
        PropertyParser.registerPropertyGetter(new MaterialMinecraftPropertyGetter(propertyClass, properties), MaterialTag.class, null, null, propertyClass);
    }
}
