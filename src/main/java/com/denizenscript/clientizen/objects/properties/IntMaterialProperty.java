package com.denizenscript.clientizen.objects.properties;

import com.denizenscript.clientizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.tags.Attribute;
import net.minecraft.state.State;
import net.minecraft.state.property.IntProperty;

public class IntMaterialProperty extends MojangMaterialProperty<IntProperty, Integer> {
    public IntMaterialProperty(String name, MaterialTag material, IntProperty internalProperty) {
        super(name, material, internalProperty);
    }

    public static void register() {
        registerTag(ElementTag.class, currentlyRegistering, (Attribute attribute, IntMaterialProperty object) -> {
            return new ElementTag(object.material.state.get(object.internalProperty));
        });
        registerMechanism(ElementTag.class, currentlyRegistering, (IntMaterialProperty object, Mechanism mechanism, ElementTag input) -> {
            if (mechanism.requireInteger()) {
                object.material.state = (State<?, ?>) object.material.state.with(object.internalProperty, input.asInt());
            }
        });
    }
}
