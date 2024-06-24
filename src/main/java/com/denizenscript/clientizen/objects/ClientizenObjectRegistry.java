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

        // <--[tag]
        // @attribute <entity[<entity>]>
        // @returns EntityTag
        // @description
        // Returns an entity object constructed from the input value.
        // Refer to <@link ObjectType EntityTag>.
        // -->
        TYPE_ENTITY = ObjectFetcher.registerWithObjectFetcher(EntityTag.class, EntityTag.tagProcessor).setAsNOtherCode().generateBaseTag();

        // <--[tag]
        // @attribute <location[<location>]>
        // @returns LocationTag
        // @description
        // Returns a location object constructed from the input value.
        // Refer to <@link ObjectType LocationTag>.
        // -->
        TYPE_LOCATION = ObjectFetcher.registerWithObjectFetcher(LocationTag.class, LocationTag.tagProcessor).setAsNOtherCode().setCanConvertStatic().generateBaseTag();

        // <--[tag]
        // @attribute <material[<material>]>
        // @returns MaterialTag
        // @description
        // Returns a material object constructed from the input value.
        // Refer to <@link ObjectType MaterialTag>.
        // -->
        TYPE_MATERIAL = ObjectFetcher.registerWithObjectFetcher(MaterialTag.class, MaterialTag.tagProcessor).generateBaseTag();

        // <--[tag]
        // @attribute <item[<item>]>
        // @returns ItemTag
        // @description
        // Returns an item object constructed from the input value.
        // Refer to <@link ObjectType ItemTag>.
        // -->
        TYPE_ITEM = ObjectFetcher.registerWithObjectFetcher(ItemTag.class, ItemTag.tagProcessor).setAsNOtherCode().generateBaseTag();

        // <--[tag]
        // @attribute <mod[<mod>]>
        // @returns ModTag
        // @description
        // Returns a mod object constructed from the input value.
        // Refer to <@link ObjectType ModTag>.
        // -->
        TYPE_MOD = ObjectFetcher.registerWithObjectFetcher(ModTag.class, ModTag.tagProcessor).setAsNOtherCode().setCanConvertStatic().generateBaseTag();

        // <--[tag]
        // @attribute <particle[<particle>]>
        // @returns ParticleTag
        // @description
        // Returns a particle object constructed from the input value.
        // Refer to <@link ObjectType ParticleTag>.
        // -->
        TYPE_PARTICLE = ObjectFetcher.registerWithObjectFetcher(ParticleTag.class, ParticleTag.tagProcessor).setAsNOtherCode().generateBaseTag();
    }
}
