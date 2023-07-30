package com.denizenscript.clientizen.objects.properties.entity;

import com.denizenscript.clientizen.access.KeyBindingMixinAccess;
import com.denizenscript.clientizen.objects.EntityTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.core.ElementTag;
import net.minecraft.client.MinecraftClient;

public class EntitySneaking extends EntityProperty<ElementTag> {

    // <--[property]
    // @object EntityTag
    // @name sneaking
    // @input ElementTag(Boolean)
    // @description
    // Whether an entity is sneaking.
    // For most entities this just makes the name tag less visible, and doesn't actually update the pose.
    // -->

    public static boolean describes(EntityTag entity) {
        return true;
    }

    @Override
    public ElementTag getPropertyValue() {
        return new ElementTag(getEntity().isSneaking());
    }

    @Override
    public boolean isDefaultValue(ElementTag value) {
        return !value.asBoolean();
    }

    @Override
    public void setPropertyValue(ElementTag value, Mechanism mechanism) {
        if (!mechanism.requireBoolean()) {
            return;
        }
        boolean sneaking = value.asBoolean();
        getEntity().setSneaking(sneaking);
        if (getEntity() == MinecraftClient.getInstance().player) {
            KeyBindingMixinAccess keyBindingMixinAccess = (KeyBindingMixinAccess) MinecraftClient.getInstance().options.sneakKey;
            keyBindingMixinAccess.clientizen$forceSetPressed(sneaking);
            keyBindingMixinAccess.clientizen$disableUntilPress();
        }
    }

    @Override
    public String getPropertyId() {
        return "sneaking";
    }

    public static void register() {
        autoRegister("sneaking", EntitySneaking.class, ElementTag.class, false);
    }
}
