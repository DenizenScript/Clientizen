package com.denizenscript.clientizen.objects.properties.entity;

import com.denizenscript.clientizen.objects.EntityTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import net.minecraft.entity.EntityType;

public record EntitySheared(EntityTag entity) implements Property {

    public static EntitySheared getFrom(ObjectTag object) {
        return object instanceof EntityTag entity && entity.is(EntityType.SHEEP) ? new EntitySheared(entity) : null;
    }

    @Override
    public String getPropertyString() {
        return entity.as(EntityType.SHEEP).isSheared() ? "true" : null;
    }

    @Override
    public String getPropertyId() {
        return "sheared";
    }

    public static void register() {

        // <--[tag]
        // @attribute <EntityTag.sheared>
        // @returns ElementTag(Boolean)
        // @mechanism EntityTag.sheared
        // @group properties
        // @description
        // Returns whether a sheep is sheared.
        // -->
        PropertyParser.registerTag(EntitySheared.class, ElementTag.class, "sheared", (attribute, object) -> {
            return new ElementTag(object.entity.as(EntityType.SHEEP).isSheared());
        });

        // <--[mechanism]
        // @object EntityTag
        // @name sheared
        // @input ElementTag(Boolean)
        // @description
        // Sets whether a sheep is sheared.
        // @tags
        // <EntityTag.sheared>
        // -->
        PropertyParser.registerMechanism(EntitySheared.class, ElementTag.class, "sheared", (object, mechanism, input) -> {
            if (mechanism.requireBoolean()) {
                object.entity.as(EntityType.SHEEP).setSheared(input.asBoolean());
            }
        });
    }
}
