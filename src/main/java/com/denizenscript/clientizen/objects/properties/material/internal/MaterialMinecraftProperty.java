package com.denizenscript.clientizen.objects.properties.material.internal;

import com.denizenscript.clientizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.ObjectProperty;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.denizenscript.denizencore.utilities.debugging.DebugInternals;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public abstract class MaterialMinecraftProperty<T extends Property<V>, V extends Comparable<V>> extends ObjectProperty<MaterialTag, ElementTag> {

    public T internalProperty;

    protected MaterialMinecraftProperty() {}

    @SafeVarargs
    public static <T extends Property<V>, V extends Comparable<V>> void registerProperty(Class<? extends MaterialMinecraftProperty<T, V>> propertyClass, T... properties) {
        PropertyParser.registerPropertyGetter(new MaterialMinecraftPropertyGetter<>(propertyClass, properties), MaterialTag.class, null, null, propertyClass);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void registerEnumProperty(Class<? extends MaterialEnumProperty> propertyClass, EnumProperty<?>... properties) {
        PropertyParser.registerPropertyGetter(new MaterialMinecraftPropertyGetter(propertyClass, properties), MaterialTag.class, null, null, propertyClass);
    }

    public record MaterialMinecraftPropertyGetter<T extends Property<V>, V extends Comparable<V>>
            (MethodHandle constructor, T[] internalProperties) implements PropertyParser.PropertyGetter<MaterialTag> {

        private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
        private static final MethodType PROPERTY_CONSTRUCTOR = MethodType.methodType(void.class);

        private static MethodHandle getConstructor(Class<? extends MaterialMinecraftProperty<?, ?>> propertyClass) {
            try {
                return LOOKUP.findConstructor(propertyClass, PROPERTY_CONSTRUCTOR);
            }
            catch (NoSuchMethodException noSuchMethodException) {
                Debug.echoError("Invalid material minecraft property class '" + DebugInternals.getClassNameOpti(propertyClass) + "' is missing a no-args constructor!");
                throw new IllegalArgumentException(noSuchMethodException);
            }
            catch (IllegalAccessException illegalAccessException) {
                Debug.echoError("Unable to get constructor from material minecraft property class '" + DebugInternals.getClassNameOpti(propertyClass) + "'!");
                throw new IllegalArgumentException(illegalAccessException);
            }
        }

        public MaterialMinecraftPropertyGetter(Class<? extends MaterialMinecraftProperty<T, V>> propertyClass, T[] internalProperties) {
            this(getConstructor(propertyClass), internalProperties);
        }


        @Override
        public ObjectProperty<MaterialTag, ElementTag> get(MaterialTag material) {
            if (material.state == null) {
                return null;
            }
            for (T internalProperty : internalProperties) {
                if (material.state.contains(internalProperty)) {
                    try {
                        //noinspection unchecked
                        MaterialMinecraftProperty<T, ?> property = (MaterialMinecraftProperty<T, ?>) constructor.invoke();
                        property.object = material;
                        property.internalProperty = internalProperty;
                        return property;
                    }
                    catch (Throwable e) {
                        Debug.echoError("Exception while constructing property '" + DebugInternals.getClassNameOpti(constructor.type().returnType()) + "':");
                        Debug.echoError(e);
                        return null;
                    }
                }
            }
            return null;
        }
    }
}
