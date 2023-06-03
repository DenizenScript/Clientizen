package com.denizenscript.clientizen.objects;

import com.denizenscript.clientizen.util.Utilities;
import com.denizenscript.denizencore.objects.*;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;

import java.util.UUID;

public class EntityTag implements ObjectTag, Adjustable {

    public final UUID uuid;
    public Entity entity;

    public EntityTag(Entity entity) {
        this.entity = entity;
        this.uuid = entity.getUuid();
    }

    public EntityTag(EntityType<?> entityType) {
        entity = entityType.create(MinecraftClient.getInstance().world);
        uuid = null;
    }

    @Fetchable("e")
    public static EntityTag valueOf(String string, TagContext context) {
        if (string == null) {
            return null;
        }
//        if (string.startsWith("e@fake:")) {
//            String uuidString = string.substring("e@fake:".length());
//            UUID uuid = Utilities.uuidFromString(uuidString);
//            if (uuid == null) {
//                Utilities.echoErrorByContext(context, "valueOf EntityTag returning null: UUID '" + uuidString + "' is invalid.");
//                return null;
//            }
//            Entity entity = getEntityForID(uuid);
//            if (entity == null) {
//                Utilities.echoErrorByContext(context, "valueOf EntityTag returning null: UUID '" + uuidString + "' is valid but isn't matched to any entity.");
//                return null;
//            }
//            return new EntityTag(entity);
//        }
        if (ObjectFetcher.isObjectWithProperties(string)) {
            return ObjectFetcher.getObjectFromWithProperties(ClientizenObjectRegistry.TYPE_ENTITY, string, context);
        }
        boolean strictUUID = false;
        // e@fake:<UUID> - treat as a normal entity since fake entities are just entities to the client
        // TODO: store the fact that the entity is a fake one?
        if (string.startsWith("e@fake:")) {
            string = string.substring("e@fake:".length());
            strictUUID = true;
        }
        else if (string.startsWith("e@")) {
            string = string.substring("e@".length());
        }
        // e@<UUID>/<Entity Type/Script>
        int slashIndex = string.indexOf('/');
        if (slashIndex != -1) {
            String uuidString = string.substring(0, slashIndex);
            // If the UUID isn't a valid entity anymore, the type (after "/") will be used
            string = string.substring(slashIndex + 1);
            UUID uuid = Utilities.uuidFromString(uuidString);
            if (uuid == null) {
                Utilities.echoErrorByContext(context, "valueOf EntityTag returning null: UUID '" + uuidString + "' is invalid.");
                return null;
            }
            Entity entity = getEntityForID(uuid);
            if (entity != null) {
                EntityType<?> entityType = EntityType.get(string).orElse(null);
                // If the value isn't a valid entity type then just let it through, as we can't verify entity scripts
                if (entityType != null && entity.getType() != entityType) {
                    Utilities.echoErrorByContext(context, "valueOf EntityTag returning null: UUID '" + uuidString + "' is valid, but doesn't match the provided entity type.");
                    return null;
                }
                return new EntityTag(entity);
            }
        }
        // e@(fake:)<UUID>
        UUID uuid = Utilities.uuidFromString(string);
        if (uuid != null) {
            Entity entity = getEntityForID(uuid);
            if (entity == null) {
                Utilities.echoErrorByContext(context, "valueOf EntityTag returning null: UUID '" + string + "' is valid but isn't matched to any entity.");
                return null;
            }
            return new EntityTag(entity);
        }
        else if (strictUUID) {
            Utilities.echoErrorByContext(context, "valueOf EntityTag returning null: UUID '" + string + "' is invalid.");
            return null;
        }
        // e@<Entity Type>
        EntityTag entityByType = EntityType.get(string).map(EntityTag::new).orElse(null);
        if (entityByType == null) {
            Utilities.echoErrorByContext(context, "valueOf EntityTag returning null: invalid entity type '" + string + "'.");
            return null;
        }
        return entityByType;
    }

    public static Entity getEntityForID(UUID uuid) {
        return MinecraftClient.getInstance().world.getEntityLookup().get(uuid);
    }

    public static boolean matches(String string) {
        if (string.startsWith("e@")) {
            return true;
        }
        return valueOf(string, CoreUtilities.noDebugContext) != null;
    }

    public Entity getEntity() {
        if (entity == null || entity.isRemoved()) {
            Entity found = getEntityForID(uuid);
            if (found != null) {
                entity = found;
            }
        }
        return entity;
    }

    public boolean isSpawned() {
        return uuid != null && getEntity() != null && entity.isAlive();
    }

    public boolean is(EntityType<?> type) {
        return getEntity().getType() == type;
    }

    public static void register() {
        tagProcessor.registerTag(ElementTag.class, "entity_type", (attribute, object) -> {
            return new ElementTag(object.getEntity().getType().getUntranslatedName(), true);
        });
        tagProcessor.registerTag(ElementTag.class, "health", (attribute, object) -> {
            if (object.getEntity() instanceof LivingEntity livingEntity) {
                return new ElementTag(livingEntity.getHealth());
            }
            return null;
        });
    }

    public static final ObjectTagProcessor<EntityTag> tagProcessor = new ObjectTagProcessor<>();

    @Override
    public ObjectTag getObjectAttribute(Attribute attribute) {
        return tagProcessor.getObjectAttribute(this, attribute);
    }

    @Override
    public void adjust(Mechanism mechanism) {
        tagProcessor.processMechanism(this, mechanism);
    }

    @Override
    public void applyProperty(Mechanism mechanism) {
        if (!isUnique()) {
            mechanism.echoError("Cannot apply properties to already-spawned entities.");
            return;
        }
        adjust(mechanism);
    }

    @Override
    public String identify() {
        if (uuid != null) {
            return "e@" + uuid + '/' + getEntity().getType().getUntranslatedName();
        }
        return "e@" + entity.getType().getUntranslatedName() + PropertyParser.getPropertiesString(this);
    }

    @Override
    public String identifySimple() {
        return "e@" + (uuid != null ? uuid : entity.getType().getUntranslatedName());
    }

    @Override
    public String debuggable() {
        if (uuid != null) {
            return "<LG>e@<Y>" + uuid + " <GR>(" + getEntity().getType().getUntranslatedName() +
                    (entity.hasCustomName() ? "<LG>/<GR>" + entity.getCustomName().getString() : "") + ")";
        }
        return "<LG>e@<Y>" + entity.getType().getUntranslatedName() + PropertyParser.getPropertiesDebuggable(this);
    }

    @Override
    public String toString() {
        return identify();
    }

    @Override
    public boolean isUnique() {
        return uuid != null || isSpawned();
    }

    private String prefix = "Entity";

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public ObjectTag setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }
}
