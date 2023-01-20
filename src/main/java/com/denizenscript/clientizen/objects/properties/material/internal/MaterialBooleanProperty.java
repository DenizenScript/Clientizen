package com.denizenscript.clientizen.objects.properties.material.internal;

import com.denizenscript.clientizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.tags.Attribute;
import net.minecraft.state.property.BooleanProperty;

public class MaterialBooleanProperty extends MaterialMinecraftProperty<BooleanProperty, Boolean> {

    public MaterialBooleanProperty(String name, MaterialTag material, BooleanProperty internalProperty) {
        super(name, material, internalProperty);
    }

    public static void register() {
        registerTag(ElementTag.class, currentlyRegistering, (Attribute attribute, MaterialBooleanProperty object) -> {
            return new ElementTag(object.material.state.get(object.internalProperty));
        });
        registerMechanism(ElementTag.class, currentlyRegistering, (MaterialBooleanProperty object, Mechanism mechanism, ElementTag input) -> {
            if (mechanism.requireBoolean()) {
                object.material.state = object.material.state.with(object.internalProperty, input.asBoolean());
            }
        });
    }
}
