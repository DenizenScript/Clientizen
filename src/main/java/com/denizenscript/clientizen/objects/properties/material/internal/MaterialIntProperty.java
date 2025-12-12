package com.denizenscript.clientizen.objects.properties.material.internal;

import com.denizenscript.clientizen.mixin.IntPropertyAccessor;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.core.ElementTag;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public abstract class MaterialIntProperty extends MaterialMinecraftProperty<IntegerProperty, Integer> {

    public IntPropertyAccessor getAccessor() {
        return (IntPropertyAccessor) (Object) internalProperty;
    }

    @Override
    public Integer parsePropertyValue(ElementTag input, Mechanism mechanism) {
        return mechanism.requireInteger() ? input.asInt() : null;
    }

    @Override
    public void setPropertyValue(ElementTag value, Mechanism mechanism) {
        Integer parsedValue = parsePropertyValue(value, mechanism);
        if (parsedValue == null) {
            return;
        }
        IntPropertyAccessor accessor = getAccessor();
        if (parsedValue < accessor.getMin()) {
            mechanism.echoError("Invalid input number, must be at least " + accessor.getMin() + ".");
            return;
        }
        if (parsedValue > accessor.getMax()) {
            mechanism.echoError("Invalid input number, cannot be more than " + accessor.getMax() + ".");
            return;
        }
        object.state = object.state.setValue(internalProperty, parsedValue);
    }
}
