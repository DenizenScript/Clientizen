package com.denizenscript.clientizen.objects.properties;

import com.denizenscript.clientizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.tags.Attribute;
import net.minecraft.state.property.BooleanProperty;

public class BooleanMaterialProperty extends MinecraftMaterialProperty<BooleanProperty, Boolean> {

    public BooleanMaterialProperty(String name, MaterialTag material, BooleanProperty internalProperty) {
        super(name, material, internalProperty);
    }

    public static void register() {
        registerTag(ElementTag.class, currentlyRegistering, (Attribute attribute, BooleanMaterialProperty object) -> {
            return new ElementTag(object.material.state.get(object.internalProperty));
        });
        registerMechanism(ElementTag.class, currentlyRegistering, (BooleanMaterialProperty object, Mechanism mechanism, ElementTag input) -> {
            if (mechanism.requireBoolean()) {
                object.material.state = object.material.state.with(object.internalProperty, input.asBoolean());
            }
        });
    }
}
