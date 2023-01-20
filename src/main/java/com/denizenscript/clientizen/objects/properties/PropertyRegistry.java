package com.denizenscript.clientizen.objects.properties;

import com.denizenscript.clientizen.objects.MaterialTag;
import com.denizenscript.clientizen.objects.properties.material.MaterialLevel;
import com.denizenscript.clientizen.objects.properties.material.internal.BooleanMaterialProperty;
import com.denizenscript.clientizen.objects.properties.material.internal.EnumMaterialProperty;
import com.denizenscript.clientizen.objects.properties.material.internal.IntMaterialProperty;
import com.denizenscript.clientizen.objects.properties.material.internal.MinecraftMaterialProperty;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.denizenscript.denizencore.utilities.debugging.DebugInternals;
import net.minecraft.state.property.*;
import net.minecraft.util.StringIdentifiable;

import java.lang.reflect.Field;

public class PropertyRegistry {

    public static void register() {
        registerBooleanProperty("switched", Properties.EYE, Properties.POWERED, Properties.ENABLED);
        registerBooleanProperty("waterlogged", Properties.WATERLOGGED);
        registerBooleanProperty("hanging", Properties.HANGING);
        registerEnumProperty("bed_part", Properties.BED_PART);
        registerEnumProperty("instrument", Properties.INSTRUMENT);
        registerIntProperty("level", MaterialLevel.class);
    }

    public static void registerBooleanProperty(String name, BooleanProperty... properties) {
        registerPropertyGetter(name, BooleanMaterialProperty::new, properties, BooleanMaterialProperty.class);
    }

    public static void registerIntProperty(String name, IntProperty... properties) {
        registerPropertyGetter(name, IntMaterialProperty::new, properties, IntMaterialProperty.class);
    }

    @SafeVarargs
    public static <T extends Enum<T> & StringIdentifiable> void registerEnumProperty(String name, EnumProperty<T>... properties) {
        registerPropertyGetter(name, EnumMaterialProperty::new, properties, EnumMaterialProperty.class);
    }

    public static void registerBooleanProperty(String name, Class<? extends BooleanMaterialProperty> propertyClass) {
        registerPropertyGetter(name, BooleanMaterialProperty::new, getHandledProperties(propertyClass), propertyClass);
    }

    public static void registerIntProperty(String name, Class<? extends IntMaterialProperty> propertyClass) {
        registerPropertyGetter(name, IntMaterialProperty::new, getHandledProperties(propertyClass), propertyClass);
    }

    public static <T extends Enum<T> & StringIdentifiable, P extends EnumMaterialProperty<T>> void registerEnumProperty(String name, Class<P> propertyClass) {
        registerPropertyGetter(name, EnumMaterialProperty::new, getHandledProperties(propertyClass), propertyClass);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Property<?>> T[] getHandledProperties(Class<? extends MinecraftMaterialProperty<T, ?>> propertyClass) {
        try {
            Field handledField = propertyClass.getDeclaredField("handledProperties");
            return (T[]) handledField.get(null);
        }
        catch (IllegalAccessException | NoSuchFieldException e) {
            Debug.echoError("Invalid handled properties array for property class '" + DebugInternals.getClassNameOpti(propertyClass) + "': " + e.getMessage() + "!");
        }
        return null;
    }

    public static <T extends Property<V>, V extends Comparable<V>> void registerPropertyGetter(String name, MinecraftMaterialPropertySupplier<T> supplier, T[] properties, Class<? extends com.denizenscript.denizencore.objects.properties.Property> propertyClass) {
        MinecraftMaterialProperty.currentlyRegistering = name;
        PropertyParser.registerPropertyGetter(new MinecraftMaterialPropertyGetter<>(name, supplier, properties), MaterialTag.class, null, null, propertyClass);
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
    public interface MinecraftMaterialPropertySupplier<T extends Property<?>> {

        MinecraftMaterialProperty<?, ?> create(String name, MaterialTag material, T internalProperty);
    }
}
