package com.denizenscript.clientizen.objects.properties.entity;

import com.denizenscript.clientizen.access.KeyBindingMixinAccess;
import com.denizenscript.clientizen.objects.EntityTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.core.ElementTag;
import net.minecraft.client.MinecraftClient;

public class EntitySprinting extends EntityProperty<ElementTag> {

    // <--[property]
    // @object EntityTag
    // @name is_sprinting
    // @input ElementTag(Boolean)
    // @description
    // Controls whether an entity is sprinting.
    // @mechanism
    // While this can be set for all entities, it only has an effect on the client's player.
    // -->

    public static boolean describes(EntityTag entity) {
        return true;
    }

    @Override
    public ElementTag getPropertyValue() {
        return new ElementTag(getEntity().isSprinting());
    }

    @Override
    public void setPropertyValue(ElementTag value, Mechanism mechanism) {
        if (!mechanism.requireBoolean()) {
            return;
        }
        boolean sprinting = value.asBoolean();
        getEntity().setSprinting(sprinting);
        if (getEntity() == MinecraftClient.getInstance().player) {
            KeyBindingMixinAccess keyBindingMixin = (KeyBindingMixinAccess) MinecraftClient.getInstance().options.sprintKey;
            keyBindingMixin.clientizen$forceSetPressed(sprinting);
            keyBindingMixin.clientizen$disableUntilPress();
        }
    }

    @Override
    public String getPropertyId() {
        return "is_sprinting";
    }

    public static void register() {
        autoRegister("is_sprinting", EntitySprinting.class, ElementTag.class, false);
    }
}
