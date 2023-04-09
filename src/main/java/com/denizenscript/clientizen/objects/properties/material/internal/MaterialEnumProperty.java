package com.denizenscript.clientizen.objects.properties.material.internal;

import com.denizenscript.clientizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class MaterialEnumProperty extends MaterialMinecraftProperty {

    @Override
    public ElementTag getPropertyValue() {
        return new ElementTag((Enum<?>) ((MaterialTag) object).state.get(internalProperty));
    }

    @Override
    public boolean isDefaultValue(ObjectTag value) {
        return isDefaultValue((ElementTag) value);
    }

    public abstract boolean isDefaultValue(ElementTag value);

    @Override
    public void setPropertyValue(ObjectTag value, Mechanism mechanism) {
        if (mechanism.requireEnum(internalProperty.getType())) {
            ((MaterialTag) object).state = ((MaterialTag) object).state.with(internalProperty, ((ElementTag) value).asEnum(internalProperty.getType()));
        }
    }

    public static void autoRegister(String name, Class<? extends MaterialEnumProperty> propertyClass) {
        autoRegister(name, propertyClass, MaterialTag.class, false);
    }
}
