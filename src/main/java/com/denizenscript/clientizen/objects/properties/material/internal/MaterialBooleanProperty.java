package com.denizenscript.clientizen.objects.properties.material.internal;

import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.core.ElementTag;
import net.minecraft.state.property.BooleanProperty;

public abstract class MaterialBooleanProperty extends MaterialMinecraftProperty<BooleanProperty, Boolean> {

    @Override
    @SuppressWarnings("deprecation")
    public String getPropertyString() {
        boolean value = object.state.get(internalProperty);
        return isDefaultValue(value) ? null : String.valueOf(value);
    }

    @Override
    public ElementTag getPropertyValue() {
        return new ElementTag(object.state.get(internalProperty));
    }

    public boolean isDefaultValue(boolean value) {
        return value == object.state.getBlock().getDefaultState().get(internalProperty);
    }

    @Override
    public void setPropertyValue(ElementTag value, Mechanism mechanism) {
        if (mechanism.requireBoolean()) {
            object.state = object.state.with(internalProperty, value.asBoolean());
        }
    }
}
