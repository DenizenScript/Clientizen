package com.denizenscript.clientizen.objects.properties.material;

import com.denizenscript.clientizen.objects.properties.material.internal.MaterialBooleanProperty;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;

public class MaterialSwitched extends MaterialBooleanProperty {

    public static final BooleanProperty[] handledProperties = {Properties.EYE, Properties.POWERED, Properties.ENABLED};

    @Override
    public String getPropertyId() {
        return "switched";
    }

    public static void register() {
        autoRegister("switched", MaterialSwitched.class);
    }
}
