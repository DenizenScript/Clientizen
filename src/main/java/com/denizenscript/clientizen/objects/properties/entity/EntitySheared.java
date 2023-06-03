package com.denizenscript.clientizen.objects.properties.entity;

import com.denizenscript.clientizen.objects.EntityTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.core.ElementTag;
import net.minecraft.entity.EntityType;

public class EntitySheared extends EntityProperty<ElementTag> {

    // <--[property]
    // @object EntityTag
    // @name sheared
    // @input ElementTag(Boolean)
    // @description
    // Controls whether a sheep is sheared.
    // -->
    public static boolean describes(EntityTag entity) {
        return entity.is(EntityType.SHEEP);
    }

    @Override
    public boolean isDefaultValue(ElementTag data) {
        return !data.asBoolean();
    }

    @Override
    public ElementTag getPropertyValue() {
        return new ElementTag(as(EntityType.SHEEP).isSheared());
    }

    @Override
    public void setPropertyValue(ElementTag value, Mechanism mechanism) {
        if (mechanism.requireBoolean()) {
            as(EntityType.SHEEP).setSheared(value.asBoolean());
        }
    }

    @Override
    public String getPropertyId() {
        return "sheared";
    }

    public static void register() {
        autoRegister("sheared", EntitySheared.class, ElementTag.class, false);
    }
}
