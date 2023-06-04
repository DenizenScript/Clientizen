package com.denizenscript.clientizen.objects;

import com.denizenscript.clientizen.mixin.ClientWorldAccessor;
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
import net.minecraft.text.Text;

import java.util.UUID;

public class EntityTag implements ObjectTag, Adjustable {

    public final UUID uuid;
    public Entity entity;
    public final boolean isFake;

    public EntityTag(Entity entity, boolean isFake) {
        this.entity = entity;
        this.uuid = entity.getUuid();
        this.isFake = isFake;
    }

    public EntityTag(Entity entity) {
        this(entity, false);
    }

    public EntityTag(EntityType<?> entityType) {
        this.entity = entityType.create(MinecraftClient.getInstance().world);
        this.uuid = null;
        this.isFake = false;
    }

    @Fetchable("e")
    public static EntityTag valueOf(String string, TagContext context) {
        if (string == null) {
            return null;
        }
        if (ObjectFetcher.isObjectWithProperties(string)) {
            return ObjectFetcher.getObjectFromWithProperties(ClientizenObjectRegistry.TYPE_ENTITY, string, context);
        }
        // e@fake:<UUID> - treat as a normal entity since fake entities are just entities to the client
        boolean isFake = false;
        if (string.startsWith("e@fake:")) {
            string = string.substring("e@fake:".length());
            isFake = true;
        }
        else if (string.startsWith("e@")) {
            string = string.substring("e@".length());
        }
        // e@<UUID>/<Entity Type/Script>
        int slashIndex = string.indexOf('/');
        if (slashIndex != -1) {
            String uuidString = string.substring(0, slashIndex);
            UUID uuid = Utilities.uuidFromString(uuidString);
            if (uuid == null) {
                Utilities.echoErrorByContext(context, "valueOf EntityTag returning null: UUID '" + uuidString + "' is invalid.");
                return null;
            }
            Entity entity = getEntityByUUID(uuid);
            if (entity == null) {
                // If the UUID isn't a valid entity anymore, the type (after "/") will be used
                return valueOfByType(string.substring(slashIndex + 1), context);
            }
            EntityType<?> entityType = EntityType.get(string).orElse(null);
            // If the value isn't a valid entity type then just let it through, as we can't verify entity scripts
            if (entityType != null && entity.getType() != entityType) {
                Utilities.echoErrorByContext(context, "valueOf EntityTag returning null: UUID '" + uuidString + "' is valid, but doesn't match the provided entity type.");
                return null;
            }
            return new EntityTag(entity);
        }
        // e@(fake:)<UUID>
        UUID uuid = Utilities.uuidFromString(string);
        if (uuid != null) {
            Entity entity = getEntityByUUID(uuid);
            if (entity == null) {
                Utilities.echoErrorByContext(context, "valueOf EntityTag returning null: UUID '" + string + "' is valid but isn't matched to any entity.");
                return null;
            }
            return new EntityTag(entity, isFake);
        }
        // Fake entities are always "e@fake:<UUID>", so error if there's no valid UUID
        else if (isFake) {
            Utilities.echoErrorByContext(context, "valueOf EntityTag returning null: UUID '" + string + "' is invalid.");
            return null;
        }
        // e@<Entity Type>
        return valueOfByType(string, context);
    }

    private static EntityTag valueOfByType(String typeString, TagContext context) {
        EntityTag entityByType = EntityType.get(typeString).map(EntityTag::new).orElse(null);
        if (entityByType == null) {
            Utilities.echoErrorByContext(context, "valueOf EntityTag returning null: invalid entity type '" + typeString + "'.");
            return null;
        }
        return entityByType;
    }

    public static Entity getEntityByUUID(UUID uuid) {
        return ((ClientWorldAccessor) MinecraftClient.getInstance().world).invokeGetEntityLookup().get(uuid);
    }

    public static boolean matches(String string) {
        if (string.startsWith("e@")) {
            return true;
        }
        return valueOf(string, CoreUtilities.noDebugContext) != null;
    }

    public Entity getEntity() {
        if (entity == null || entity.isRemoved()) {
            Entity found = getEntityByUUID(uuid);
            if (found != null) {
                entity = found;
            }
        }
        return entity;
    }

    public String getTypeName() {
        return getEntity().getType().getUntranslatedName();
    }

    public boolean isSpawned() {
        return uuid != null && getEntity() != null && entity.isAlive();
    }

    public boolean is(EntityType<?> type) {
        return getEntity().getType() == type;
    }

    public static void register() {
        PropertyParser.registerPropertyTagHandlers(EntityTag.class, tagProcessor);

        tagProcessor.registerTag(ElementTag.class, "entity_type", (attribute, object) -> {
            return new ElementTag(object.getTypeName(), true);
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
        if (isUnique()) {
            mechanism.echoError("Cannot apply properties to already-spawned entities.");
            return;
        }
        adjust(mechanism);
    }

    @Override
    public String identify() {
        if (uuid != null) {
            return isFake ? "e@fake:" + uuid
                    : "e@" + uuid + '/' + getTypeName();
        }
        return "e@" + getTypeName() + PropertyParser.getPropertiesString(this);
    }

    @Override
    public String identifySimple() {
        return isFake ? "e@fake:" + uuid
                : "e@" + (uuid != null ? uuid : getTypeName());
    }

    @Override
    public String debuggable() {
        if (uuid != null) {
            String debuggable = "<LG>e@";
            if (isFake) {
                debuggable += "FAKE:";
            }
            debuggable += "<Y>" + uuid + "<GR>(" + getTypeName();
            Text displayName = entity.getCustomName();
            if (displayName != null) {
                debuggable += "<LG>/<GR>" + displayName.getString();
            }
            return debuggable + ')';
        }
        return "<LG>e@<Y>" + getTypeName() + PropertyParser.getPropertiesDebuggable(this);
    }

    @Override
    public String toString() {
        return identify();
    }

    @Override
    public boolean isUnique() {
        return uuid != null;
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
