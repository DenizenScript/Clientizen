package com.denizenscript.clientizen.objects.properties.material;

import com.denizenscript.clientizen.objects.properties.material.internal.MaterialBooleanProperty;
import com.denizenscript.denizencore.objects.core.ElementTag;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;

public class MaterialWaterlogged extends MaterialBooleanProperty {

    public static final BooleanProperty[] handledProperties = {Properties.WATERLOGGED};

    @Override
    public String getPropertyId() {
        return "waterlogged";
    }

    @Override
    public boolean isDefaultValue(ElementTag value) {
        return !value.asBoolean();
    }

    public static void register() {
        autoRegister("waterlogged", MaterialWaterlogged.class, ElementTag.class, false);
    }
}
