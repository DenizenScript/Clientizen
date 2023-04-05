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
        registerTag(ElementTag.class, currentlyRegistering, (Attribute attribute, MaterialBooleanProperty prop) -> {
            return new ElementTag(prop.object.state.get(prop.internalProperty));
        });
        registerMechanism(ElementTag.class, currentlyRegistering, (MaterialBooleanProperty prop, Mechanism mechanism, ElementTag input) -> {
            if (mechanism.requireBoolean()) {
                prop.object.state = prop.object.state.with(prop.internalProperty, input.asBoolean());
            }
        });
    }
}
