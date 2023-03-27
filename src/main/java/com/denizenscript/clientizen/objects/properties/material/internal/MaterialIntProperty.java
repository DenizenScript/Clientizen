package com.denizenscript.clientizen.objects.properties.material.internal;

import com.denizenscript.clientizen.mixin.IntPropertyAccessor;
import com.denizenscript.clientizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.tags.Attribute;
import net.minecraft.state.property.IntProperty;

public class MaterialIntProperty extends MaterialMinecraftProperty<IntProperty, Integer> {
    public MaterialIntProperty(String name, MaterialTag material, IntProperty internalProperty) {
        super(name, material, internalProperty);
    }

    public static void register() {
        registerTag(ElementTag.class, currentlyRegistering, (Attribute attribute, MaterialIntProperty object) -> {
            return new ElementTag(object.object.state.get(object.internalProperty));
        });
        registerMechanism(ElementTag.class, currentlyRegistering, (MaterialIntProperty object, Mechanism mechanism, ElementTag input) -> {
            if (!mechanism.requireInteger()) {
                return;
            }
            int newValue = input.asInt();
            IntPropertyAccessor accessor = (IntPropertyAccessor) object.internalProperty;
            if (newValue < accessor.getMin()) {
                mechanism.echoError("Invalid input number, must be at least " + accessor.getMin() + ".");
                return;
            }
            if (newValue > accessor.getMax()) {
                mechanism.echoError("Invalid input number, cannot be more than " + accessor.getMax() + ".");
                return;
            }
            object.object.state = object.object.state.with(object.internalProperty, newValue);
        });
    }
}
