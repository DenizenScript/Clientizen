package com.denizenscript.clientizen.objects;

import com.denizenscript.clientizen.mixin.ClientWorldAccessor;
import com.denizenscript.clientizen.util.EntityAttachmentPersister;
import com.denizenscript.clientizen.util.Utilities;
import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.*;
import com.denizenscript.denizencore.objects.core.ColorTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.vehicle.VehicleEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class EntityTag implements ObjectTag, Adjustable {

    // <--[ObjectType]
    // @name EntityTag
    // @prefix e
    // @base ElementTag
    // @implements PropertyHolderObject
    // @ExampleTagBase client.self_entity
    // @ExampleValues <client.self_entity>
    // @ExampleForReturns
    // - narrate "The entity is %VALUE%!"
    // @format
    // The identity format for entities is either a spawned entity's UUID, or an entity type.
    // For example, 'e@1d7b97ac-e5dc-45f6-8bb2-3eb8a5ef190e' or 'e@zombie'.
    //
    // @description
    // An EntityTag represents a spawned entity or a generic entity type.
    //
    // Note that a spawned entity can be a living entity (a player, animal, monster, etc.) or a non-living entity (a painting, item frame, etc).
    //
    // @Matchable
    // EntityTag matchers, sometimes identified as "<entity>":
    // "entity" plaintext, always matches.
    // "vehicle" plaintext: matches for any vehicle type (minecarts, boats, horses, etc).
    // "fish" plaintext: matches for any fish type (cod, pufferfish, etc).
    // "projectile" plaintext: matches for any projectile type (arrow, trident, fish hook, snowball, etc).
    // "hanging" plaintext: matches for any hanging type (painting, item_frame, etc).
    // "monster" plaintext: matches for any monster type (creepers, zombies, etc).
    // "animal" plaintext: matches for any animal type (pigs, cows, etc).
    // "mob" plaintext: matches for any mob type (creepers, pigs, etc).
    // "living" plaintext: matches for any living type (players, pigs, creepers, etc).
    // Any entity type name: matches if the entity is of the given type, using advanced matchers.
    //
    // -->

    private static final Set<UUID> renderedEntities = new HashSet<>();

    public static final AttachmentType<Integer> GLOW_COLOR_OVERRIDE = EntityAttachmentPersister.createAttachment("glow_color_override");

    public final UUID uuid;
    public Entity entity;
    public final boolean isFake;

    public EntityTag(Entity entity, boolean isFake) {
        this.entity = entity;
        this.uuid = entity.getUUID();
        this.isFake = isFake;
    }

    public EntityTag(Entity entity) {
        this(entity, false);
    }

    public EntityTag(EntityType<?> entityType) {
        this.entity = entityType.create(Minecraft.getInstance().level, EntitySpawnReason.COMMAND);
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
            EntityType<?> entityType = EntityType.byString(string).orElse(null);
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
        EntityTag entityByType = EntityType.byString(typeString).map(EntityTag::new).orElse(null);
        if (entityByType == null) {
            Utilities.echoErrorByContext(context, "valueOf EntityTag returning null: invalid entity type '" + typeString + "'.");
            return null;
        }
        return entityByType;
    }

    public static Entity getEntityByUUID(UUID uuid) {
        return ((ClientWorldAccessor) Minecraft.getInstance().level).invokeGetEntities().get(uuid);
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
        return getEntity().getType().toShortString();
    }

    public float getSpigotYaw() {
        Entity entity = getEntity();
        if (entity instanceof LivingEntity && !(entity instanceof ArmorStand)) {
            return entity.getYHeadRot();
        }
        return entity.getYRot();
    }

    public boolean isSpawned() {
        return uuid != null && getEntity() != null && entity.isAlive();
    }

    public boolean is(EntityType<?> type) {
        return getEntity().getType() == type;
    }

    public static Set<UUID> getRenderedEntities() {
        return renderedEntities;
    }

    public static void register() {
        PropertyParser.registerPropertyTagHandlers(EntityTag.class, tagProcessor);

        // <--[tag]
        // @attribute <EntityTag.entity_type>
        // @returns ElementTag
        // @group data
        // @description
        // Returns an entity's type.
        // -->
        tagProcessor.registerTag(ElementTag.class, "entity_type", (attribute, object) -> {
            return new ElementTag(object.getTypeName(), true);
        });

        // <--[tag]
        // @attribute <EntityTag.location>
        // @returns LocationTag
        // @group location
        // @description
        // Returns an entity's location.
        // -->
        tagProcessor.registerTag(LocationTag.class, "location", (attribute, object) -> {
            return new LocationTag(object.getEntity().position(), object.getSpigotYaw(), object.getEntity().getXRot());
        });

        // <--[tag]
        // @attribute <EntityTag.eye_location>
        // @returns LocationTag
        // @group location
        // @description
        // Returns the entity's eye location.
        // -->
        tagProcessor.registerTag(LocationTag.class, "eye_location", (attribute, object) -> {
            return new LocationTag(object.getEntity().getEyePosition(), object.getSpigotYaw(), object.getEntity().getXRot());
        });

        // <--[tag]
        // @attribute <EntityTag.is_rendering>
        // @returns ElementTag(Boolean)
        // @description
        // Returns whether an entity is being rendered by the client.
        // This does not mean the entity will always be visible, but within the camera's viewing frustum.
        // -->
        tagProcessor.registerTag(ElementTag.class, "is_rendering", (attribute, object) -> {
            return new ElementTag(renderedEntities.contains(object.getEntity().getUUID()));
        });

        // <--[tag]
        // @attribute <EntityTag.glow_color_override>
        // @returns ColorTag
        // @mechanism EntityTag.glow_color_override
        // @description
        // Returns the entity's glow color override, if any.
        // Note that this is Clientizen data, see <@link language Client-side entity data> for more information.
        // -->
        tagProcessor.registerTag(ColorTag.class, "glow_color_override", (attribute, object) -> {
            Integer glowColor = object.getEntity().getAttached(GLOW_COLOR_OVERRIDE);
            return glowColor != null ? ColorTag.fromRGB(glowColor) : null;
        });

        // <--[mechanism]
        // @object EntityTag
        // @name glow_color_override
        // @input ColorTag
        // @description
        // Sets the entity's glow color override.
        // Provide no input to unset.
        // Note that this is Clientizen data, see <@link language Client-side entity data> for more information.
        // @tags
        // <EntityTag.glow_color_override>
        // -->
        tagProcessor.registerMechanism("glow_color_override", false, (object, mechanism) -> {
            if (!mechanism.hasValue()) {
                object.getEntity().removeAttached(GLOW_COLOR_OVERRIDE);
            }
            else if (mechanism.requireObject(ColorTag.class)) {
                object.getEntity().setAttached(GLOW_COLOR_OVERRIDE, mechanism.valueAsType(ColorTag.class).asRGB());
            }
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
            Component displayName = entity.getCustomName();
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

    @Override
    public boolean advancedMatches(String matcher, TagContext context) {
        return ScriptEvent.createMatcher(matcher).doesMatch(getTypeName(), text ->
                switch (text) {
                    case "entity" -> true;
                    case "vehicle" -> getEntity() instanceof VehicleEntity;
                    case "fish" -> getEntity() instanceof AbstractFish;
                    case "projectile" -> getEntity() instanceof Projectile;
                    case "hanging" -> getEntity() instanceof HangingEntity;
                    case "monster" -> getEntity() instanceof Enemy;
                    case "animal" -> getEntity() instanceof Animal;
                    case "mob" -> getEntity() instanceof Mob;
                    case "living" -> getEntity() instanceof LivingEntity;
                    default -> false;
                }
        );
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
