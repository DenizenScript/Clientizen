package com.denizenscript.clientizen.objects.properties.entity;

import com.denizenscript.clientizen.objects.EntityTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.properties.ObjectProperty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

public abstract class EntityProperty<TData extends ObjectTag> extends ObjectProperty<EntityTag, TData> {

    public Entity getEntity() {
        return object.getEntity();
    }

    public <T extends Entity> T as(EntityType<T> type) {
        return type.downcast(getEntity());
    }

    public boolean is(EntityType<?> type) {
        return getEntity().getType() == type;
    }
}
