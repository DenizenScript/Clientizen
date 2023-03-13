package com.denizenscript.clientizen.objects.properties;

import com.denizenscript.clientizen.objects.EntityTag;
import com.denizenscript.clientizen.objects.properties.entity.EntitySheared;
import com.denizenscript.clientizen.objects.properties.material.MaterialLevel;
import com.denizenscript.clientizen.objects.properties.material.internal.MaterialBooleanProperty;
import com.denizenscript.clientizen.objects.properties.material.internal.MaterialEnumProperty;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import net.minecraft.state.property.Properties;

import static com.denizenscript.clientizen.objects.properties.material.internal.MaterialMinecraftProperty.registerProperty;

public class PropertyRegistry {

    public static void register() {
        registerProperty("switched", MaterialBooleanProperty::new, MaterialBooleanProperty.class, Properties.EYE, Properties.POWERED, Properties.ENABLED);
        registerProperty("waterlogged", MaterialBooleanProperty::new, MaterialBooleanProperty.class, Properties.WATERLOGGED);
        registerProperty("hanging", MaterialBooleanProperty::new, MaterialBooleanProperty.class, Properties.HANGING);
        registerProperty("bed_part", MaterialEnumProperty::new, MaterialEnumProperty.class, Properties.BED_PART);
        registerProperty("instrument", MaterialEnumProperty::new, MaterialEnumProperty.class, Properties.INSTRUMENT);
        registerProperty("level", MaterialLevel::new, MaterialLevel.class, MaterialLevel.handledProperties);
        PropertyParser.registerProperty(EntitySheared.class, EntityTag.class);
    }
}
