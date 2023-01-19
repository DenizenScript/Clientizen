package com.denizenscript.clientizen.objects.properties;

import com.denizenscript.clientizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;

public class PropertyRegistry {

    public static void register() {
        registerBooleanProperty(Properties.EYE);
        registerBooleanProperty(Properties.WATERLOGGED);
        registerBooleanProperty(Properties.HANGING);
        registerEnumProperty(Properties.BED_PART);
        registerEnumProperty(Properties.INSTRUMENT);
        registerIntProperty(Properties.CANDLES);
        registerIntProperty(Properties.BITES);
    }

    public static void registerBooleanProperty(BooleanProperty property) {
        registerBooleanProperty(property, property.getName());
    }

    public static void registerBooleanProperty(BooleanProperty property, String name) {
        PropertyParser.PropertyGetter getter = object -> {
            if (!(object instanceof MaterialTag material) || material.state == null || !material.state.contains(property)) {
                return null;
            }
            return new BooleanMaterialProperty(name, material, property);
        };
        BooleanMaterialProperty.currentlyRegistering = name;
        PropertyParser.registerPropertyGetter(getter, MaterialTag.class, null, null, BooleanMaterialProperty.class);
        BooleanMaterialProperty.currentlyRegistering = null;
    }

    public static void registerEnumProperty(EnumProperty<?> property) {
        registerEnumProperty(property, property.getName());
    }

    public static void registerEnumProperty(EnumProperty<?> property, String name) {
        PropertyParser.PropertyGetter getter = object -> {
            if (!(object instanceof MaterialTag material) || material.state == null || !material.state.contains(property)) {
                return null;
            }
            return new EnumMaterialProperty<>(name, material, property);
        };
        EnumMaterialProperty.currentlyRegistering = name;
        PropertyParser.registerPropertyGetter(getter, MaterialTag.class, null, null, EnumMaterialProperty.class);
        EnumMaterialProperty.currentlyRegistering = null;
    }

    public static void registerIntProperty(IntProperty property) {
        registerIntProperty(property, property.getName());
    }

    public static void registerIntProperty(IntProperty property, String name) {
        PropertyParser.PropertyGetter getter = object -> {
            if (!(object instanceof MaterialTag material) || material.state == null || !material.state.contains(property)) {
                return null;
            }
            return new IntMaterialProperty(name, material, property);
        };
        IntMaterialProperty.currentlyRegistering = name;
        PropertyParser.registerPropertyGetter(getter, MaterialTag.class, null, null, IntMaterialProperty.class);
        IntMaterialProperty.currentlyRegistering = null;
    }
}
