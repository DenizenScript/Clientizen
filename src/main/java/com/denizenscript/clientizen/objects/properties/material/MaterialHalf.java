package com.denizenscript.clientizen.objects.properties.material;

import com.denizenscript.clientizen.objects.properties.material.internal.MaterialEnumProperty;
import com.denizenscript.denizencore.objects.core.ElementTag;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.ChestType;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;

public class MaterialHalf extends MaterialEnumProperty {

    public static final EnumProperty<?>[] handledProperties = {Properties.BED_PART, Properties.CHEST_TYPE};

    @Override
    public boolean isDefaultValue(ElementTag value) {
        return internalProperty == Properties.BED_PART ? value.asEnum(BedPart.class) == BedPart.FOOT : value.asEnum(ChestType.class) == ChestType.SINGLE;
    }

    @Override
    public String getPropertyId() {
        return "half";
    }

    public static void register() {
        autoRegister("half", MaterialHalf.class, ElementTag.class, false);
    }
}
