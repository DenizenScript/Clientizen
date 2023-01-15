package com.denizenscript.clientizen.objects;

import com.denizenscript.denizencore.objects.Fetchable;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import java.util.UUID;

public class EntityTag implements ObjectTag {

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

    public static ObjectTagProcessor<EntityTag> tagProcessor = new ObjectTagProcessor<>();

    @Override
    public ObjectTag getObjectAttribute(Attribute attribute) {
        return tagProcessor.getObjectAttribute(this, attribute);
    }

    @Override
    public String identify() {
        return "e@" + uuid;
    }

    @Override
    public String identifySimple() {
        return identify();
    }

    @Override
    public String debuggable() {
        String debuggable = "<LG>e@<Y>" + uuid;
        if (getEntity() != null) {
            debuggable += " <GR>(" + entity.getType().getUntranslatedName();
            if (entity.hasCustomName()) {
                debuggable += "<Y>/<GR>" + entity.getCustomName().getString();
            }
            debuggable += ")";
        }
        return debuggable;
    }

    @Override
    public String toString() {
        return identify();
    }

    private String prefix = "Entity";

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public boolean isUnique() {
        return true;
    }

    @Override
    public ObjectTag setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }
}
