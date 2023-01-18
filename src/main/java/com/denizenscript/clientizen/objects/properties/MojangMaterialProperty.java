package com.denizenscript.clientizen.objects.properties;

import com.denizenscript.clientizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import com.denizenscript.denizencore.utilities.debugging.DebugInternals;

public abstract class MojangMaterialProperty<T extends net.minecraft.state.property.Property<V>, V extends Comparable<V>> implements Property {

    public static String currentlyRegistering;

    public T internalProperty;
    public MaterialTag material;
    public String name;

    public MojangMaterialProperty(String name, MaterialTag material, T internalProperty) {
        this.name = name;
        this.material = material;
        this.internalProperty = internalProperty;
    }

    @Override
    public String getPropertyString() {
        return String.valueOf(material.state.get(internalProperty));
    }

    @Override
    public String getPropertyId() {
        return name;
    }

    @SuppressWarnings("unchecked")
    public static <T extends MojangMaterialProperty<?, ?>, R extends ObjectTag> void registerTag(Class<R> returnType, String name, PropertyParser.PropertyTagWithReturn<T, R> runnable) {
        final PropertyParser.PropertyGetter getter = PropertyParser.currentlyRegisteringProperty;
        MaterialTag.tagProcessor.registerTag(returnType, name, (attribute, object) -> {
            Property prop = getter.get(object);
            if (prop == null) {
                attribute.echoError("Property 'MaterialTag." + name + "' does not describe the input object.");
                return null;
            }
            return runnable.run(attribute, (T) prop);
        });
    }

    @SuppressWarnings("unchecked")
    public static <T extends MojangMaterialProperty<?, ?>, P extends ObjectTag> void registerMechanism(Class<P> paramType, String name, PropertyParser.PropertyMechanismWithParam<T, P> runner) {
        final PropertyParser.PropertyGetter getter = PropertyParser.currentlyRegisteringProperty;
        MaterialTag.tagProcessor.registerMechanism(name, true, (object, mechanism) -> {
            Property prop = getter.get(object);
            if (prop == null) {
                mechanism.echoError("Property 'MaterialTag." + name + "' does not describe the input object.");
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
}
