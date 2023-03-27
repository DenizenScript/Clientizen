package com.denizenscript.clientizen.objects.properties.material.internal;

import com.denizenscript.clientizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import com.denizenscript.denizencore.utilities.debugging.DebugInternals;

public abstract class MaterialMinecraftProperty<T extends net.minecraft.state.property.Property<V>, V extends Comparable<V>> implements Property {

    public static String currentlyRegistering;

    public T internalProperty;
    public MaterialTag material;
    public String name;

    public MaterialMinecraftProperty(String name, MaterialTag material, T internalProperty) {
        this.name = name;
        this.material = material;
        this.internalProperty = internalProperty;
    }

    @Override
    public ElementTag getPropertyValue() {
        return new ElementTag(material.state.get(internalProperty).toString());
    }

    @Override
    public String getPropertyId() {
        return name;
    }

    @SuppressWarnings("unchecked")
    public static <T extends MaterialMinecraftProperty<?, ?>, R extends ObjectTag> void registerTag(Class<R> returnType, String name, PropertyParser.PropertyTagWithReturn<T, R> runnable) {
        final PropertyParser.PropertyGetter<MaterialTag> getter = (PropertyParser.PropertyGetter<MaterialTag>) PropertyParser.currentlyRegisteringProperty;
        final String propertyName = currentlyRegistering;
        MaterialTag.tagProcessor.registerTag(returnType, name, (attribute, object) -> {
            Property prop = getter.get(object);
            if (prop == null) {
                attribute.echoError("Property 'MaterialTag." + propertyName + "' does not describe the input object.");
                return null;
            }
            return runnable.run(attribute, (T) prop);
        });
    }

    @SuppressWarnings("unchecked")
    public static <T extends MaterialMinecraftProperty<?, ?>, P extends ObjectTag> void registerMechanism(Class<P> paramType, String name, PropertyParser.PropertyMechanismWithParam<T, P> runner) {
        final PropertyParser.PropertyGetter<MaterialTag> getter = (PropertyParser.PropertyGetter<MaterialTag>) PropertyParser.currentlyRegisteringProperty;
        final String propertyName = currentlyRegistering;
        MaterialTag.tagProcessor.registerMechanism(name, true, (object, mechanism) -> {
            Property prop = getter.get(object);
            if (prop == null) {
                mechanism.echoError("Property 'MaterialTag." + propertyName + "' does not describe the input object.");
                return;
            }
            if (mechanism.value == null) {
                mechanism.echoError("Error: mechanism '" + name + "' must have input of type '" + DebugInternals.getClassNameOpti(paramType) + "', but none was given.");
                return;
            }
            P input = mechanism.value.asType(paramType, mechanism.context);
            if (input == null) {
                mechanism.echoError("Error: mechanism '" + name + "' must have input of type '" + DebugInternals.getClassNameOpti(paramType) + "', but value '" + mechanism.value + "' cannot be converted to the required type.");
                return;
            }
            runner.run((T) prop, mechanism, input);
        });
    }

    @SafeVarargs
    public static <T extends net.minecraft.state.property.Property<V>, V extends Comparable<V>> void registerProperty(String name, MaterialMinecraftPropertySupplier<T> supplier, Class<? extends Property> propertyClass, T... properties) {
        currentlyRegistering = name;
        PropertyParser.registerPropertyGetter(new MaterialMinecraftPropertyGetter<>(name, supplier, properties), MaterialTag.class, null, null, propertyClass);
        currentlyRegistering = null;
    }

    @SuppressWarnings("unchecked")
    public record MaterialMinecraftPropertyGetter<T extends net.minecraft.state.property.Property<V>, V extends Comparable<V>>
            (String name, MaterialMinecraftPropertySupplier<T> supplier, T... internalProperties) implements PropertyParser.PropertyGetter<MaterialTag> {

        @Override
        public com.denizenscript.denizencore.objects.properties.Property get(MaterialTag material) {
            if (material.state == null) {
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
    public interface MaterialMinecraftPropertySupplier<T extends net.minecraft.state.property.Property<?>> {

        MaterialMinecraftProperty<?, ?> create(String name, MaterialTag material, T internalProperty);
    }
}
