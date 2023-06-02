package com.denizenscript.clientizen.objects.properties.material.internal;

import com.denizenscript.clientizen.mixin.IntPropertyAccessor;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.core.ElementTag;
import net.minecraft.state.property.IntProperty;

public abstract class MaterialIntProperty extends MaterialMinecraftProperty<IntProperty, Integer> {

    public IntPropertyAccessor getAccessor() {
        return (IntPropertyAccessor) internalProperty;
    }

    @Override
    public void setPropertyValue(ElementTag value, Mechanism mechanism) {
        if (!mechanism.requireInteger()) {
            return;
        }
        int newValue = value.asInt();
        IntPropertyAccessor accessor = getAccessor();
        if (newValue < accessor.getMin()) {
            mechanism.echoError("Invalid input number, must be at least " + accessor.getMin() + ".");
            return;
        }
        if (newValue > accessor.getMax()) {
            mechanism.echoError("Invalid input number, cannot be more than " + accessor.getMax() + ".");
            return;
        }
        object.state = object.state.with(internalProperty, newValue);
    }
}
