package com.denizenscript.clientizen.objects.properties;

import com.denizenscript.clientizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.tags.Attribute;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;

public class EnumMaterialProperty<T extends Enum<T> & StringIdentifiable> extends MinecraftMaterialProperty<EnumProperty<T>, T> {

    public EnumMaterialProperty(String name, MaterialTag material, EnumProperty<T> internalProperty) {
        super(name, material, internalProperty);
    }

    public static <T extends Enum<T> & StringIdentifiable> void register() {
        registerTag(ElementTag.class, currentlyRegistering, (Attribute attribute, EnumMaterialProperty<T> object) -> {
            return new ElementTag(object.material.state.get(object.internalProperty));
        });
        registerMechanism(ElementTag.class, currentlyRegistering, (EnumMaterialProperty<T> object, Mechanism mechanism, ElementTag input) -> {
            if (mechanism.requireEnum(object.internalProperty.getType())) {
                object.material.state = object.material.state.with(object.internalProperty, input.asEnum(object.internalProperty.getType()));
            }
        });
    }
}
