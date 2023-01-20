package com.denizenscript.clientizen.objects.properties;

import com.denizenscript.clientizen.objects.MaterialTag;
import com.denizenscript.clientizen.objects.properties.material.MaterialLevel;
import com.denizenscript.clientizen.objects.properties.material.internal.MaterialBooleanProperty;
import com.denizenscript.clientizen.objects.properties.material.internal.MaterialEnumProperty;
import com.denizenscript.clientizen.objects.properties.material.internal.MaterialIntProperty;
import com.denizenscript.clientizen.objects.properties.material.internal.MaterialMinecraftProperty;
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
        registerPropertyGetter(name, MaterialBooleanProperty::new, properties, MaterialBooleanProperty.class);
    }

    public static void registerIntProperty(String name, IntProperty... properties) {
        registerPropertyGetter(name, MaterialIntProperty::new, properties, MaterialIntProperty.class);
    }

    @SafeVarargs
    public static <T extends Enum<T> & StringIdentifiable> void registerEnumProperty(String name, EnumProperty<T>... properties) {
        registerPropertyGetter(name, MaterialEnumProperty::new, properties, MaterialEnumProperty.class);
    }

    public static void registerBooleanProperty(String name, Class<? extends MaterialBooleanProperty> propertyClass) {
        registerPropertyGetter(name, MaterialBooleanProperty::new, getHandledProperties(propertyClass), propertyClass);
    }

    public static void registerIntProperty(String name, Class<? extends MaterialIntProperty> propertyClass) {
        registerPropertyGetter(name, MaterialIntProperty::new, getHandledProperties(propertyClass), propertyClass);
    }

    public static <T extends Enum<T> & StringIdentifiable, P extends MaterialEnumProperty<T>> void registerEnumProperty(String name, Class<P> propertyClass) {
        registerPropertyGetter(name, MaterialEnumProperty::new, getHandledProperties(propertyClass), propertyClass);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Property<?>> T[] getHandledProperties(Class<? extends MaterialMinecraftProperty<T, ?>> propertyClass) {
        try {
            Field handledField = propertyClass.getDeclaredField("handledProperties");
            return (T[]) handledField.get(null);
        }
        catch (IllegalAccessException | NoSuchFieldException e) {
            Debug.echoError("Invalid handled properties array for property class '" + DebugInternals.getClassNameOpti(propertyClass) + "': " + e.getMessage() + "!");
        }
        return null;
    }

    public static <T extends Property<V>, V extends Comparable<V>> void registerPropertyGetter(String name, MaterialMinecraftPropertySupplier<T> supplier, T[] properties, Class<? extends com.denizenscript.denizencore.objects.properties.Property> propertyClass) {
        MaterialMinecraftProperty.currentlyRegistering = name;
        PropertyParser.registerPropertyGetter(new MaterialMinecraftPropertyGetter<>(name, supplier, properties), MaterialTag.class, null, null, propertyClass);
        MaterialMinecraftProperty.currentlyRegistering = null;
    }

    @SuppressWarnings("unchecked")
    public record MaterialMinecraftPropertyGetter<T extends Property<V>, V extends Comparable<V>>
            (String name, MaterialMinecraftPropertySupplier<T> supplier, T... internalProperties) implements PropertyParser.PropertyGetter {

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
    public interface MaterialMinecraftPropertySupplier<T extends Property<?>> {

        MaterialMinecraftProperty<?, ?> create(String name, MaterialTag material, T internalProperty);
    }
}
