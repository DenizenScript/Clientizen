package com.denizenscript.clientizen.objects.properties;

import com.denizenscript.clientizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import net.minecraft.state.property.*;

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

    public static void registerEnumProperty(EnumProperty<?> property) {
        registerEnumProperty(property, property.getName());
    }

    public static void registerIntProperty(IntProperty property) {
        registerIntProperty(property, property.getName());
    }

    public static void registerBooleanProperty(BooleanProperty property, String name) {
        registerPropertyGetter(new MaterialInternalPropertyGetter<>(name, property, BooleanMaterialProperty::new), BooleanMaterialProperty.class);
    }

    public static void registerEnumProperty(EnumProperty<?> property, String name) {
        registerPropertyGetter(new MaterialInternalPropertyGetter<>(name, property, EnumMaterialProperty::new), EnumMaterialProperty.class);
    }

    public static void registerIntProperty(IntProperty property, String name) {
        registerPropertyGetter(new MaterialInternalPropertyGetter<>(name, property, IntMaterialProperty::new), IntMaterialProperty.class);
    }

    public static void registerPropertyGetter(MaterialInternalPropertyGetter<?, ?> getter, Class<? extends com.denizenscript.denizencore.objects.properties.Property> propertyClass) {
        MinecraftMaterialProperty.currentlyRegistering = getter.name();
        PropertyParser.registerPropertyGetter(getter, MaterialTag.class, null, null, propertyClass);
        MinecraftMaterialProperty.currentlyRegistering = null;
    }

    public record MaterialInternalPropertyGetter<T extends Property<V>, V extends Comparable<V>>
            (String name, T internalProperty, MinecraftMaterialPropertySupplier<T> supplier) implements PropertyParser.PropertyGetter {

        @Override
        public com.denizenscript.denizencore.objects.properties.Property get(ObjectTag object) {
            if (object instanceof MaterialTag material && material.state != null && material.state.contains(internalProperty)) {
                return supplier.create(name, material, internalProperty);
            }
            return null;
        }
    }

    @FunctionalInterface
    public interface MinecraftMaterialPropertySupplier<T extends net.minecraft.state.property.Property<?>> {

        MinecraftMaterialProperty<?, ?> create(String name, MaterialTag material, T internalProperty);
    }
}
