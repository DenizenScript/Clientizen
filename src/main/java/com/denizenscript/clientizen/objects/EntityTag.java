package com.denizenscript.clientizen.objects;

import com.denizenscript.denizencore.objects.*;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;

import java.util.UUID;

public class EntityTag implements ObjectTag, Adjustable {

    public UUID uuid;
    public Entity entity;

    public EntityTag(Entity entity) {
        this.entity = entity;
        this.uuid = entity.getUuid();
    }

    @Fetchable("e")
    public static EntityTag valueOf(String string, TagContext context) {
        if (string == null) {
            return null;
        }
        if (ObjectFetcher.isObjectWithProperties(string)) {
            return ObjectFetcher.getObjectFromWithProperties(ClientizenObjectRegistry.TYPE_ENTITY, string, context);
        }
        if (string.startsWith("e@")) {
            string = string.substring("e@".length());
        }
        try {
            Entity found = MinecraftClient.getInstance().world.getEntityLookup().get(UUID.fromString(string));
            if (found != null) {
                return new EntityTag(found);
            }
        }
        catch (Exception ignored) {}
        if (context == null || context.showErrors()) {
            Debug.echoError("valueOf EntityTag returning null: " + string);
        }
        return null;
    }

    public static boolean matches(String string) {
        if (string.startsWith("e@")) {
            return true;
        }
        return valueOf(string, CoreUtilities.noDebugContext) != null;
    }

    public Entity getEntity() {
        if (entity == null || entity.isRemoved()) {
            Entity found = MinecraftClient.getInstance().world.getEntityLookup().get(uuid);
            if (found != null) {
                entity = found;
            }
        }
        return entity;
    }

    public <T extends Entity> T as(EntityType<T> type) {
        return type.downcast(getEntity());
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
        Debug.echoError("Cannot apply properties to an EntityTag"); // TODO: support not spawned by-type EntityTags
    }

    @Override
    public String identify() {
        return "e@" + uuid + PropertyParser.getPropertiesString(this);
    }

    @Override
    public String identifySimple() {
        return "e@" + uuid;
    }

    @Override
    public String debuggable() {
        String debuggable = "<LG>e@<Y>" + uuid;
        if (getEntity() != null) {
            debuggable += " <GR>(" + entity.getType().getUntranslatedName();
            if (entity.hasCustomName()) {
                debuggable += "<LG>/<GR>" + entity.getCustomName().getString();
            }
            debuggable += ")";
        }
        return debuggable;
    }

    @Override
    public String toString() {
        return identify();
    }

    @Override
    public boolean isUnique() {
        return true;
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
