package com.denizenscript.clientizen.objects.properties.material.internal;

import com.denizenscript.clientizen.mixin.IntPropertyAccessor;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.core.ElementTag;
import net.minecraft.state.property.IntProperty;

public abstract class MaterialIntProperty extends MaterialMinecraftProperty<IntProperty, Integer> {

    @Override
    @SuppressWarnings("deprecation")
    public String getPropertyString() {
        int value = object.state.get(internalProperty);
        return isDefaultValue(value) ? null : String.valueOf(value);
    }

    @Override
    public ElementTag getPropertyValue() {
        return new ElementTag(object.state.get(internalProperty));
    }

    public boolean isDefaultValue(int value) {
        return value == object.state.getBlock().getDefaultState().get(internalProperty);
    }

    @Override
    public void setPropertyValue(ElementTag value, Mechanism mechanism) {
        if (!mechanism.requireInteger()) {
            return;
        }
        int newValue = value.asInt();
        IntPropertyAccessor accessor = (IntPropertyAccessor) internalProperty;
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
