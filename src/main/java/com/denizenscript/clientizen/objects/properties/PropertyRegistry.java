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
        registerProperty("switched", MaterialBooleanProperty::new, MaterialBooleanProperty.class, Properties.EYE, Properties.POWERED, Properties.ENABLED);
        registerProperty("waterlogged", MaterialBooleanProperty::new, MaterialBooleanProperty.class, Properties.WATERLOGGED);
        registerProperty("hanging", MaterialBooleanProperty::new, MaterialBooleanProperty.class, Properties.HANGING);
        registerProperty("bed_part", MaterialEnumProperty::new, MaterialEnumProperty.class, Properties.BED_PART);
        registerProperty("instrument", MaterialEnumProperty::new, MaterialEnumProperty.class, Properties.INSTRUMENT);
        registerProperty("level", MaterialLevel::new, MaterialLevel.class, MaterialLevel.handledProperties);
    }
    @SafeVarargs
    public static <T extends Property<V>, V extends Comparable<V>> void registerProperty(String name, MaterialMinecraftPropertySupplier<T> supplier, Class<? extends com.denizenscript.denizencore.objects.properties.Property> propertyClass, T... properties) {
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
