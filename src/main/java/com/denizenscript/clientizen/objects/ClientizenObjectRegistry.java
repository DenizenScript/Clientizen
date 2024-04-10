package com.denizenscript.clientizen.objects;

import com.denizenscript.denizencore.objects.ObjectFetcher;
import com.denizenscript.denizencore.objects.ObjectType;

public class ClientizenObjectRegistry {

    public static ObjectType<EntityTag> TYPE_ENTITY;
    public static ObjectType<LocationTag> TYPE_LOCATION;
    public static ObjectType<MaterialTag> TYPE_MATERIAL;
    public static ObjectType<ItemTag> TYPE_ITEM;
    public static ObjectType<ModTag> TYPE_MOD;
    public static ObjectType<ParticleTag> TYPE_PARTICLE;

    public static void registerObjects() {
        TYPE_ENTITY = ObjectFetcher.registerWithObjectFetcher(EntityTag.class, EntityTag.tagProcessor).setAsNOtherCode().generateBaseTag();
        TYPE_LOCATION = ObjectFetcher.registerWithObjectFetcher(LocationTag.class, LocationTag.tagProcessor).setAsNOtherCode().setCanConvertStatic().generateBaseTag();
        TYPE_MATERIAL = ObjectFetcher.registerWithObjectFetcher(MaterialTag.class, MaterialTag.tagProcessor).generateBaseTag();
        TYPE_ITEM = ObjectFetcher.registerWithObjectFetcher(ItemTag.class, ItemTag.tagProcessor).setAsNOtherCode().generateBaseTag();
        TYPE_MOD = ObjectFetcher.registerWithObjectFetcher(ModTag.class, ModTag.tagProcessor).setAsNOtherCode().setCanConvertStatic().generateBaseTag();
        TYPE_PARTICLE = ObjectFetcher.registerWithObjectFetcher(ParticleTag.class, ParticleTag.tagProcessor).setAsNOtherCode().generateBaseTag();
    }
}
