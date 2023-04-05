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
        registerTag(ElementTag.class, currentlyRegistering, (Attribute attribute, MaterialIntProperty prop) -> {
            return new ElementTag(prop.object.state.get(prop.internalProperty));
        });
        registerMechanism(ElementTag.class, currentlyRegistering, (MaterialIntProperty prop, Mechanism mechanism, ElementTag input) -> {
            if (!mechanism.requireInteger()) {
                return;
            }
            int newValue = input.asInt();
            IntPropertyAccessor accessor = (IntPropertyAccessor) prop.internalProperty;
            if (newValue < accessor.getMin()) {
                mechanism.echoError("Invalid input number, must be at least " + accessor.getMin() + ".");
                return;
            }
            if (newValue > accessor.getMax()) {
                mechanism.echoError("Invalid input number, cannot be more than " + accessor.getMax() + ".");
                return;
            }
            prop.object.state = prop.object.state.with(prop.internalProperty, newValue);
        });
    }
}
