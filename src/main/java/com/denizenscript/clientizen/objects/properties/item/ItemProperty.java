package com.denizenscript.clientizen.objects.properties.item;

import com.denizenscript.clientizen.objects.ItemTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.properties.ObjectProperty;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class ItemProperty<TData extends ObjectTag> extends ObjectProperty<ItemTag, TData> {

    public ItemStack getStack() {
        return object.getStack();
    }

    public boolean is(Item item) {
        return getStack().isOf(item);
    }
}
