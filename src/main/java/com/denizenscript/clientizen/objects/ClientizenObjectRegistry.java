package com.denizenscript.clientizen.objects;

import com.denizenscript.denizencore.objects.ObjectFetcher;
import com.denizenscript.denizencore.objects.ObjectType;

public class ClientizenObjectRegistry {

    public static ObjectType<EntityTag> TYPE_ENTITY;

    public static void registerObjects() {
        TYPE_ENTITY = ObjectFetcher.registerWithObjectFetcher(EntityTag.class, EntityTag.tagProcessor).setAsNOtherCode().generateBaseTag();
    }
}
