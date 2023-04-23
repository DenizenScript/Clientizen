package com.denizenscript.clientizen.objects.properties.material.internal;

import com.denizenscript.clientizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.ObjectProperty;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.denizenscript.denizencore.utilities.debugging.DebugInternals;
import net.minecraft.state.property.Property;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.stream.Collectors;

public abstract class MaterialMinecraftProperty<T extends Property<V>, V extends Comparable<V>> extends ObjectProperty<MaterialTag, ElementTag> {

    public T internalProperty;
    private String propertyID;

    protected MaterialMinecraftProperty() {}

    @Override
    public String getPropertyId() {
        return propertyID;
    }

    @Override
    @SuppressWarnings("deprecation")
    public String getPropertyString() {
        V value = object.state.get(internalProperty);
        return isDefaultValue(value) ? null : internalProperty.name(value);
    }

    public boolean isDefaultValue(V value) {
        return value == object.state.getBlock().getDefaultState().get(internalProperty);
    }

    @Override
    public ElementTag getPropertyValue() {
        return new ElementTag(internalProperty.name(object.state.get(internalProperty)));
    }

    @Override
    public void setPropertyValue(ElementTag value, Mechanism mechanism) {
        internalProperty.parse(value.asLowerString()).ifPresentOrElse(
                newValue -> object.state = object.state.with(internalProperty, newValue),
                () -> mechanism.echoError("Invalid " + DebugInternals.getClassNameOpti(internalProperty.getType()) + " specified, must be one of: "
                        + internalProperty.getValues().stream().map(internalProperty::name).collect(Collectors.joining(", ")) + '.'));
    }

    @SafeVarargs
    public static <T extends Property<V>, V extends Comparable<V>> void registerProperty(Class<? extends MaterialMinecraftProperty<T, V>> propertyClass, T... properties) {
        PropertyParser.registerPropertyGetter(new MaterialMinecraftPropertyGetter<>(propertyClass, properties), MaterialTag.class, null, null, propertyClass);
    }

    @SuppressWarnings({"rawtypes", "unchecked"}) // Erase types to allow enum properties
    public static void autoRegister(String name, Class<? extends MaterialMinecraftProperty> propertyClass) {
        ((MaterialMinecraftPropertyGetter<?, ?>) PropertyParser.currentlyRegisteringProperty).propertyID = name;
        autoRegister(name, propertyClass, ElementTag.class, false);
    }

    public static class MaterialMinecraftPropertyGetter<T extends Property<V>, V extends Comparable<V>> implements PropertyParser.PropertyGetter<MaterialTag> {

        private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
        private static final MethodType NO_ARGS_CONSTRUCTOR = MethodType.methodType(void.class);

        private final MethodHandle constructor;
        private final T[] internalProperties;
        private String propertyID;

        public MaterialMinecraftPropertyGetter(Class<? extends MaterialMinecraftProperty<T, V>> propertyClass, T[] internalProperties) {
            try {
                this.constructor = LOOKUP.findConstructor(propertyClass, NO_ARGS_CONSTRUCTOR);
            }
            catch (Exception e) {
                Debug.echoError("Unable to get constructor from material minecraft property class '" + DebugInternals.getClassNameOpti(propertyClass) + "'!");
                throw new IllegalArgumentException(e);
            }
            this.internalProperties = internalProperties;
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
                        property.propertyID = propertyID;
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
