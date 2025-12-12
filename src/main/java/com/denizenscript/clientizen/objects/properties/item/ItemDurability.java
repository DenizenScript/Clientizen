package com.denizenscript.clientizen.objects.properties.item;

import com.denizenscript.clientizen.objects.ItemTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.core.ElementTag;

public class ItemDurability extends ItemProperty<ElementTag> {

    // <--[property]
    // @object ItemTag
    // @name durability
    // @input ElementTag(Number)
    // @description
    // The amount of durability an item lost.
    // -->

    public static boolean describes(ItemTag item) {
        return item.getStack().isDamageableItem();
    }

    @Override
    public ElementTag getPropertyValue() {
        return new ElementTag(getStack().getDamageValue());
    }

    @Override
    public boolean isDefaultValue(ElementTag value) {
        return value.asInt() <= 0;
    }

    @Override
    public String getPropertyId() {
        return "durability";
    }

    @Override
    public void setPropertyValue(ElementTag value, Mechanism mechanism) {
        if (mechanism.requireInteger()) {
            getStack().setDamageValue(value.asInt());
        }
    }

    public static void register() {
        autoRegister("durability", ItemDurability.class, ElementTag.class, false);
    }
}
