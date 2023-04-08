package com.denizenscript.clientizen.objects.properties.material.internal;

import com.denizenscript.clientizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.ObjectProperty;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import com.denizenscript.denizencore.utilities.debugging.DebugInternals;
import net.minecraft.state.property.Property;

public abstract class MaterialMinecraftProperty<T extends Property<V>, V extends Comparable<V>> extends ObjectProperty<MaterialTag, ElementTag> {

    public static String currentlyRegistering;

    public final T internalProperty;
    public final String name;

    public MaterialMinecraftProperty(String name, MaterialTag material, T internalProperty) {
        this.name = name;
        this.object = material;
        this.internalProperty = internalProperty;
    }

    @Override
    public String getPropertyId() {
        return name;
    }

    public static void register() {
        registerTag(ElementTag.class, currentlyRegistering, (attribute, prop) -> prop.getPropertyValue());
        registerMechanism(ElementTag.class, currentlyRegistering, (prop, mechanism, input) -> prop.setPropertyValue(input, mechanism));
    }

    @SuppressWarnings("unchecked")
    public static <T extends MaterialMinecraftProperty<?, ?>, R extends ObjectTag> void registerTag(Class<R> returnType, String name, PropertyParser.PropertyTagWithReturn<T, R> runnable) {
        final MaterialMinecraftPropertyGetter<?, ?> getter = (MaterialMinecraftPropertyGetter<?, ?>) PropertyParser.currentlyRegisteringProperty;
        final String propertyName = currentlyRegistering;
        MaterialTag.tagProcessor.registerTag(returnType, name, (attribute, object) -> {
            ObjectProperty<MaterialTag, ElementTag> prop = getter.get(object);
            if (prop == null) {
                attribute.echoError("Property 'MaterialTag." + propertyName + "' does not describe the input object.");
                return null;
            }
            return runnable.run(attribute, (T) prop);
        });
    }

    @SuppressWarnings("unchecked")
    public static <T extends MaterialMinecraftProperty<?, ?>, P extends ObjectTag> void registerMechanism(Class<P> paramType, String name, PropertyParser.PropertyMechanismWithParam<T, P> runner) {
        final MaterialMinecraftPropertyGetter<?, ?> getter = (MaterialMinecraftPropertyGetter<?, ?>) PropertyParser.currentlyRegisteringProperty;
        final String propertyName = currentlyRegistering;
        MaterialTag.tagProcessor.registerMechanism(name, true, (object, mechanism) -> {
            ObjectProperty<MaterialTag, ElementTag> prop = getter.get(object);
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
    public static <T extends Property<V>, V extends Comparable<V>> void registerProperty(String name, MaterialMinecraftPropertySupplier<T> supplier, Class<? extends com.denizenscript.denizencore.objects.properties.Property> propertyClass, T... properties) {
        currentlyRegistering = name;
        PropertyParser.registerPropertyGetter(new MaterialMinecraftPropertyGetter<>(name, supplier, properties), MaterialTag.class, null, null, propertyClass);
        currentlyRegistering = null;
    }

    public record MaterialMinecraftPropertyGetter<T extends Property<V>, V extends Comparable<V>>
            (String name, MaterialMinecraftPropertySupplier<T> supplier, T[] internalProperties) implements PropertyParser.PropertyGetter<MaterialTag> {

        @Override
        public ObjectProperty<MaterialTag, ElementTag> get(MaterialTag material) {
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
    public interface MaterialMinecraftPropertySupplier<T extends Property<?>> {
        MaterialMinecraftProperty<?, ?> create(String name, MaterialTag material, T internalProperty);
    }
}
