package com.denizenscript.clientizen.objects.properties.material.internal;

import com.denizenscript.clientizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.core.ElementTag;
import net.minecraft.state.property.BooleanProperty;

public class MaterialBooleanProperty extends MaterialMinecraftProperty<BooleanProperty, Boolean> {

    public MaterialBooleanProperty(String name, MaterialTag material, BooleanProperty internalProperty) {
        super(name, material, internalProperty);
    }

    @Override
    public ElementTag getPropertyValue() {
        return new ElementTag(object.state.get(internalProperty));
    }

    @Override
    public void setPropertyValue(ElementTag value, Mechanism mechanism) {
        if (mechanism.requireBoolean()) {
            object.state = object.state.with(internalProperty, value.asBoolean());
        }
    }

    public static void register() {
        MaterialMinecraftProperty.register();
    }
}
