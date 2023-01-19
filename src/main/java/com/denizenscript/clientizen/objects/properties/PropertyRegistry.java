package com.denizenscript.clientizen.objects.properties;

import com.denizenscript.clientizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import net.minecraft.state.property.*;
import net.minecraft.util.StringIdentifiable;

public class PropertyRegistry {

    public static void register() {
        registerBooleanProperty("switched", Properties.EYE, Properties.POWERED, Properties.ENABLED);
        registerBooleanProperty("waterlogged", Properties.WATERLOGGED);
        registerBooleanProperty("hanging", Properties.HANGING);
        registerEnumProperty("bed_part", Properties.BED_PART);
        registerEnumProperty("instrument", Properties.INSTRUMENT);
        registerIntProperty("level", Properties.CANDLES, Properties.BITES, Properties.LEVEL_3, Properties.LEVEL_8, Properties.LEVEL_1_8, Properties.LEVEL_15);
    }

    public static void registerBooleanProperty(String name, BooleanProperty... properties) {
        registerPropertyGetter(new MinecraftMaterialPropertyGetter<>(name, BooleanMaterialProperty::new, properties), BooleanMaterialProperty.class);
    }

    @SafeVarargs
    public static <T extends Enum<T> & StringIdentifiable> void registerEnumProperty(String name, EnumProperty<T>... properties) {
        registerPropertyGetter(new MinecraftMaterialPropertyGetter<>(name, EnumMaterialProperty::new, properties), EnumMaterialProperty.class);
    }

    public static void registerIntProperty(String name, IntProperty... properties) {
        registerPropertyGetter(new MinecraftMaterialPropertyGetter<>(name, IntMaterialProperty::new, properties), IntMaterialProperty.class);
    }

    public static void registerPropertyGetter(MinecraftMaterialPropertyGetter<?, ?> getter, Class<? extends com.denizenscript.denizencore.objects.properties.Property> propertyClass) {
        MinecraftMaterialProperty.currentlyRegistering = getter.name();
        PropertyParser.registerPropertyGetter(getter, MaterialTag.class, null, null, propertyClass);
        MinecraftMaterialProperty.currentlyRegistering = null;
    }

    @SuppressWarnings("unchecked")
    public record MinecraftMaterialPropertyGetter<T extends Property<V>, V extends Comparable<V>>
            (String name, MinecraftMaterialPropertySupplier<T> supplier, T... internalProperties) implements PropertyParser.PropertyGetter {

        @Override
        public com.denizenscript.denizencore.objects.properties.Property get(ObjectTag object) {
            if (!(object instanceof MaterialTag material) || material.state == null) {
                return null;
            }
            for (T internalProperty : internalProperties) {
                if (material.state.contains(internalProperty)) {
                    return supplier.create(name, material, internalProperty);
                }
            }
            return null;
        }
    }

    @FunctionalInterface
    public interface MinecraftMaterialPropertySupplier<T extends net.minecraft.state.property.Property<?>> {

        MinecraftMaterialProperty<?, ?> create(String name, MaterialTag material, T internalProperty);
    }
}
