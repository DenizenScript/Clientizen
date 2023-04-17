package com.denizenscript.clientizen.objects.properties.material.internal;

import com.denizenscript.clientizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import net.minecraft.state.property.EnumProperty;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class MaterialEnumProperty extends MaterialMinecraftProperty {


    @Override
    public ElementTag getPropertyValue() {
        return new ElementTag((Enum<?>) ((MaterialTag) object).state.get(internalProperty));
    }

    @Override
    public boolean isDefaultValue(ObjectTag value) {
        return isDefaultValue(((ElementTag) value).asEnum(internalProperty.getType()));
    }

    public boolean isDefaultValue(Enum<?> value) {
        return value == ((MaterialTag) object).state.getBlock().getDefaultState().get(internalProperty);
    }

    @Override
    public void setPropertyValue(ObjectTag value, Mechanism mechanism) {
        if (mechanism.requireEnum(internalProperty.getType())) {
            ((MaterialTag) object).state = ((MaterialTag) object).state.with(internalProperty, ((ElementTag) value).asEnum(internalProperty.getType()));
        }
    }

    public static void autoRegisterEnumProperty(String name, Class<? extends MaterialEnumProperty> propertyClass) {
        autoRegister(name, propertyClass, ElementTag.class, false);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void registerEnumProperty(Class<? extends MaterialEnumProperty> propertyClass, EnumProperty<?>... properties) {
        PropertyParser.registerPropertyGetter(new MaterialMinecraftPropertyGetter(propertyClass, properties), MaterialTag.class, null, null, propertyClass);
    }
}
