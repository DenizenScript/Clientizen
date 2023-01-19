package com.denizenscript.clientizen.objects.properties;

import com.denizenscript.clientizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.tags.Attribute;
import net.minecraft.state.State;
import net.minecraft.state.property.IntProperty;

public class IntMaterialProperty extends MinecraftMaterialProperty<IntProperty, Integer> {
    public IntMaterialProperty(String name, MaterialTag material, IntProperty internalProperty) {
        super(name, material, internalProperty);
    }

    public static void register() {
        registerTag(ElementTag.class, currentlyRegistering, (Attribute attribute, IntMaterialProperty object) -> {
            return new ElementTag(object.material.state.get(object.internalProperty));
        });
        registerMechanism(ElementTag.class, currentlyRegistering, (IntMaterialProperty object, Mechanism mechanism, ElementTag input) -> {
            if (!mechanism.requireInteger()) {
                return;
            }
            int newValue = input.asInt();
            if (newValue < object.internalProperty.min) {
                mechanism.echoError("Invalid number '" + newValue + "' specified: must be at least '" + object.internalProperty.min + "'.");
                return;
            }
            if (newValue > object.internalProperty.max) {
                mechanism.echoError("Invalid number '" + newValue + "' specified: cannot be more than '" + object.internalProperty.max + "'.");
                return;
            }
            object.material.state = (State<?, ?>) object.material.state.with(object.internalProperty, newValue);
        });
    }
}
