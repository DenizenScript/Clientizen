package com.denizenscript.clientizen.objects.properties.entity;

import com.denizenscript.clientizen.access.KeyBindingMixinAccess;
import com.denizenscript.clientizen.objects.EntityTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.core.ElementTag;
import net.minecraft.client.Minecraft;

public class EntitySneaking extends EntityProperty<ElementTag> {

    // <--[property]
    // @object EntityTag
    // @name is_sneaking
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
        return new ElementTag(getEntity().isShiftKeyDown());
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
        getEntity().setShiftKeyDown(sneaking);
        if (getEntity() == Minecraft.getInstance().player) {
            KeyBindingMixinAccess keyBindingMixinAccess = (KeyBindingMixinAccess) Minecraft.getInstance().options.keyShift;
            keyBindingMixinAccess.clientizen$forceSetPressed(sneaking);
            keyBindingMixinAccess.clientizen$disableUntilPress();
        }
    }

    @Override
    public String getPropertyId() {
        return "is_sneaking";
    }

    public static void register() {
        autoRegister("is_sneaking", EntitySneaking.class, ElementTag.class, false);
    }
}
