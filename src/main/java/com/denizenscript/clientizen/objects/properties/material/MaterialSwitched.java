package com.denizenscript.clientizen.objects.properties.material;

import com.denizenscript.clientizen.objects.properties.material.internal.MaterialBooleanProperty;
import com.denizenscript.denizencore.objects.core.ElementTag;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;

public class MaterialSwitched extends MaterialBooleanProperty {

    public static final BooleanProperty[] handledProperties = {Properties.EYE, Properties.POWERED, Properties.ENABLED};

    @Override
    public String getPropertyId() {
        return "switched";
    }

    @Override
    public boolean isDefaultValue(ElementTag value) {
        if (internalProperty == Properties.EYE || internalProperty == Properties.POWERED) {
            return !value.asBoolean();
        }
        return value.asBoolean();
    }

    public static void register() {
        autoRegister("switched", MaterialSwitched.class, ElementTag.class, false);
    }
}
