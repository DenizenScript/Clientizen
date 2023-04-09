package com.denizenscript.clientizen.objects.properties.material.internal;

import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.core.ElementTag;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;

public abstract class MaterialEnumProperty<T extends Enum<T> & StringIdentifiable> extends MaterialMinecraftProperty<EnumProperty<T>, T> {

    @Override
    public ElementTag getPropertyValue() {
        return new ElementTag(object.state.get(internalProperty));
    }

    @Override
    public void setPropertyValue(ElementTag value, Mechanism mechanism) {
        if (mechanism.requireEnum(internalProperty.getType())) {
            object.state = object.state.with(internalProperty, value.asEnum(internalProperty.getType()));
        }
    }

    public abstract boolean isDefaultValue(ElementTag value);
}
