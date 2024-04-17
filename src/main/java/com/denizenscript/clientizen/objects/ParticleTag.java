package com.denizenscript.clientizen.objects;

import com.denizenscript.clientizen.access.BillboardParticleMixinAccess;
import com.denizenscript.clientizen.access.ParticleMixinAccess;
import com.denizenscript.clientizen.mixin.particle.ParticleAccessor;
import com.denizenscript.clientizen.mixin.particle.ParticleManagerAccessor;
import com.denizenscript.clientizen.mixin.particle.SpriteBillboardParticleAccessor;
import com.denizenscript.clientizen.scripts.containers.ParticleScriptContainer;
import com.denizenscript.clientizen.util.Utilities;
import com.denizenscript.denizencore.objects.Adjustable;
import com.denizenscript.denizencore.objects.Fetchable;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ColorTag;
import com.denizenscript.denizencore.objects.core.DurationTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ScriptTag;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ParticleTag implements Adjustable {

    // <--[ObjectType]
    // @name ParticleTag
    // @prefix particle
    // @ExampleTagBase [particle]
    // @base ElementTag
    // @format
    // The identity format for particles is the particle's UUID.
    // For example, 'particle@14ade8b1-746c-4952-881f-2844432aa277'.
    //
    // @description
    // A ParticleTag represents a particle that currently exists in the world.
    // Either a normal vanilla particle, one from a <@link language Particle Script Container>, or one from another mod.
    //
    // -->

    public static final Map<UUID, Particle> particles = new HashMap<>();

    public static SpriteAtlasTexture getParticleAtlas() {
        return (SpriteAtlasTexture) MinecraftClient.getInstance().getTextureManager().getTexture(SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE);
    }

    public static Map<Identifier, ParticleManager.SimpleSpriteProvider> getSpriteProviders() {
        return ((ParticleManagerAccessor) MinecraftClient.getInstance().particleManager).getSpriteProviderMap();
    }

    @Fetchable("particle")
    public static ParticleTag valueOf(String text, TagContext context) {
        if (text.startsWith("particle@")) {
            text = text.substring("particle@".length());
        }
        UUID uuid = Utilities.uuidFromString(text);
        if (uuid == null) {
            Utilities.echoErrorByContext(context, "valueOf ParticleTag returning null: '" + text + "' isn't a valid UUID.");
            return null;
        }
        Particle particle = particles.get(uuid);
        if (particle == null) {
            Utilities.echoErrorByContext(context, "valueOf ParticleTag returning null: UUID '" + uuid + "' is valid, but isn't matched to any particle.");
            return null;
        }
        return new ParticleTag(particle);
    }

    public static boolean matches(String text) {
        if (text.startsWith("particle@")) {
            return true;
        }
        return valueOf(text, CoreUtilities.noDebugContext) != null;
    }

    public final Particle particle;

    public ParticleTag(Particle particle) {
        this.particle = particle;
    }

    public ParticleAccessor getAccessor() {
        return (ParticleAccessor) particle;
    }

    public ParticleMixinAccess getMixinAccess() {
        return (ParticleMixinAccess) particle;
    }

    public Identifier getTypeId() {
        return Registries.PARTICLE_TYPE.getId(getMixinAccess().clientizen$getType());
    }

    public String getTypeString() {
        return Utilities.idToString(getTypeId());
    }

    public static void register() {

        // <--[tag]
        // @attribute <ParticleTag.type>
        // @returns ElementTag
        // @description
        // Returns the particle's particle type.
        // For vanilla particles, this is their base vanilla type - see <@link url https://minecraft.wiki/w/Particles_(Java_Edition)#Types_of_particles>.
        // For clientizen particles, this is their custom particle type ("clientizen:<particle script name>") - should generally prefer <@link tag ParticleTag.script>.
        // For particles added by other mods, this is a custom particle type in namespaced key format ("<mod_id>:<particle type>").
        // @example
        // # Use to check if a particle is a flame particle.
        // - if <[particle].type> == flame:
        //   - narrate "It's a flame particle!"
        // -->
        tagProcessor.registerTag(ElementTag.class, "type", (attribute, object) -> {
            return new ElementTag(object.getTypeString(), true);
        });

        // <--[tag]
        // @attribute <ParticleTag.script>
        // @returns ScriptTag
        // @description
        // Returns the particle script a particle was created from, if any.
        // -->
        tagProcessor.registerTag(ScriptTag.class, "script", (attribute, object) -> {
            if (object.particle instanceof ParticleScriptContainer.ClientizenParticle clientizenParticle) {
                return new ScriptTag(clientizenParticle.particleScript);
            }
            return null;
        });

        // <--[tag]
        // @attribute <ParticleTag.location>
        // @returns LocationTag
        // @mechanism ParticleTag.location
        // @description
        // Returns the particle's location.
        // @example
        // # Use to move the particle 5 blocks up.
        // - adjust <[particle]> location:<[particle].location.above[5]>
        // -->
        tagProcessor.registerTag(LocationTag.class, "location", (attribute, object) -> {
            ParticleAccessor particle = object.getAccessor();
            return new LocationTag(particle.getX(), particle.getY(), particle.getZ());
        });

        // <--[mechanism]
        // @object ParticleTag
        // @name location
        // @input LocationTag
        // @description
        // Sets the particle's location.
        // @tags
        // <ParticleTag.location>
        // @example
        // # Use to move the particle 5 blocks up.
        // - adjust <[particle]> location:<[particle].location.above[5]>
        // -->
        tagProcessor.registerMechanism("location", false, LocationTag.class, (object, mechanism, input) -> {
            object.particle.setPos(input.getX(), input.getY(), input.getZ());
        });

        // <--[tag]
        // @attribute <ParticleTag.velocity>
        // @returns LocationTag
        // @mechanism ParticleTag.velocity
        // @description
        // Returns the particle's velocity as a LocationTag vector.
        // @example
        // # Use to check whether the particle is going upwards.
        // - if <[particle].velocity.y> > 0:
        //   - narrate "The particle is heading upwards."
        // -->
        tagProcessor.registerTag(LocationTag.class, "velocity", (attribute, object) -> {
            ParticleAccessor particle = object.getAccessor();
            return new LocationTag(particle.getVelocityX(), particle.getVelocityY(), particle.getVelocityZ());
        });

        // <--[mechanism]
        // @object ParticleTag
        // @name velocity
        // @input LocationTag
        // @description
        // Sets the particle's velocity to the given LocationTag vector.
        // @tags
        // <ParticleTag.velocity>
        // @example
        // # Use to make the particle move upwards.
        // - adjust <[particle]> velocity:0,1,0
        // -->
        tagProcessor.registerMechanism("velocity", false, LocationTag.class, (object, mechanism, input) -> {
            object.particle.setVelocity(input.getX(), input.getY(), input.getZ());
        });

        // <--[tag]
        // @attribute <ParticleTag.texture>
        // @returns ElementTag
        // @mechanism ParticleTag.texture
        // @description
        // Returns the particle's current texture as a namespaced key, if it's of a type that has textures.
        // Note that the texture id is within the particle texture atlas,
        // "assets/<namespace>/textures/particle/<name>.png" within the resource pack, and referenced as "<namespace>:<name>".
        // -->
        tagProcessor.registerTag(ElementTag.class, "texture", (attribute, object) -> {
            if (object.particle instanceof SpriteBillboardParticleAccessor spriteParticle) {
                return new ElementTag(Utilities.idToString(spriteParticle.getSprite().getContents().getId()), true);
            }
            return null;
        });

        // <--[mechanism]
        // @object ParticleTag
        // @name texture
        // @input ElementTag
        // @description
        // Sets the particle's texture, if it's of a type that allows them.
        // The input is a namespaced key of the texture, within the particle texture atlas -
        // "assets/<namespace>/textures/particle/<name>.png" within the resource pack, and referenced as "<namespace>:<name>".
        // @tags
        // <ParticleTag.texture>
        // -->
        tagProcessor.registerMechanism("texture", false, ElementTag.class, (object, mechanism, input) -> {
            if (!(object.particle instanceof SpriteBillboardParticleAccessor spriteParticle)) {
                mechanism.echoError("Cannot set texture: particles of type '" + object.getTypeString() + "' don't support textures.");
                return;
            }
            Identifier texture = Identifier.tryParse(input.asString());
            if (texture == null) {
                mechanism.echoError("Invalid texture id specified: " + input + '.');
                return;
            }
            Sprite sprite = getParticleAtlas().getSprite(texture);
            if (sprite == null) {
                mechanism.echoError("Texture id '" + input + "' is valid, but doesn't match any texture.");
                return;
            }
            spriteParticle.invokeSetSprite(sprite);
        });

        // <--[tag]
        // @attribute <ParticleTag.color>
        // @returns ColorTag
        // @mechanism ParticleTag.color
        // @description
        // Returns the particle's color.
        // Usually applied on top of a particle's existing texture, either coloring it or overriding it (while keeping the shape).
        // -->
        tagProcessor.registerTag(ColorTag.class, "color", (attribute, object) -> {
            ParticleAccessor particle = object.getAccessor();
            return new ColorTag((int) (particle.getRed() * 255f), (int) (particle.getGreen() * 255f), (int) (particle.getBlue() * 255f), (int) (particle.getAlpha() * 255f));
        });

        // <--[mechanism]
        // @object ParticleTag
        // @name color
        // @input ColorTag
        // @description
        // Sets the particle's color.
        // Usually applied on top of a particle's existing texture, either coloring it or overriding it (while keeping the shape).
        // Note that alpha values can be set, but only visually apply to some particles.
        // @tags
        // <ParticleTag.color>
        // @example
        // # Use to set a particle's color to a random color.
        // - adjust <[particle]> color:random
        // -->
        tagProcessor.registerMechanism("color", false, ColorTag.class, (object, mechanism, input) -> {
            object.particle.setColor(input.red / 255f, input.green / 255f, input.blue / 255f);
            object.getAccessor().invokeSetAlpha(input.alpha / 255f);
        });

        // <--[tag]
        // @attribute <ParticleTag.world_collision>
        // @returns ElementTag(Boolean)
        // @mechanism ParticleTag.world_collision
        // @description
        // Returns whether the particle will collide with the world.
        // -->
        tagProcessor.registerTag(ElementTag.class, "world_collision", (attribute, object) -> {
            return new ElementTag(object.getAccessor().collidesWithWorld());
        });

        // <--[mechanism]
        // @object ParticleTag
        // @name world_collision
        // @input ElementTag(Boolean)
        // @description
        // Sets whether the particle will collide the with the world.
        // @tags
        // <ParticleTag.world_collision>
        // -->
        tagProcessor.registerMechanism("world_collision", false, ElementTag.class, (object, mechanism, input) -> {
            if (mechanism.requireBoolean()) {
                object.getAccessor().setCollidesWithWorld(input.asBoolean());
            }
        });

        // <--[tag]
        // @attribute <ParticleTag.time_lived>
        // @returns DurationTag
        // @mechanism ParticleTag.time_lived
        // @description
        // Returns how long the particle's existed for.
        // @example
        // # Use to check if the particle's existed for at least 10 seconds.
        // - if <[particle].time_lived.is_more_than[10s]>:
        //   - narrate "The particle's existed for more than 10 seconds."
        // -->
        tagProcessor.registerTag(DurationTag.class, "time_lived", (attribute, object) -> {
            return new DurationTag((long) object.getAccessor().getAge());
        });

        // <--[mechanism]
        // @object ParticleTag
        // @name time_lived
        // @input DurationTag
        // @description
        // Sets the amount of time the particle's existed for.
        // Generally shouldn't be needed, but may be useful in some specific cases.
        // See <@link mechanism ParticleTag.time_to_live> for setting the amount of time the particle should exist for.
        // @tags
        // <ParticleTag.time_lived>
        // @example
        // # Use to make it as if the particle just spawned in.
        // - adjust <[particle]> time_lived:0
        // -->
        tagProcessor.registerMechanism("time_lived", false, DurationTag.class, (object, mechanism, input) -> {
            object.getAccessor().setAge(input.getTicksAsInt());
        });

        // <--[tag]
        // @attribute <ParticleTag.time_to_live>
        // @returns DurationTag
        // @mechanism ParticleTag.time_to_live
        // @description
        // Returns the amount of time the particle should exist for.
        // See <@link tag ParticleTag.time_lived> for the amount of time the particle's existed.
        // @example
        // # Use to check how much time is left before the particle despawns.
        // - narrate "The particle will despawn in <[particle].time_to_live.sub[<[particle].time_lived>].formatted>."
        // -->
        tagProcessor.registerTag(DurationTag.class, "time_to_live", (attribute, object) -> {
            return new DurationTag((long) object.particle.getMaxAge());
        });

        // <--[mechanism]
        // @object ParticleTag
        // @name time_to_live
        // @input DurationTag
        // @description
        // Sets the amount of time the particle should exist for.
        // @tags
        // <ParticleTag.time_to_live>
        // @example
        // # Use to make it so the particle will exist for 10 seconds (assuming it just spawned).
        // - adjust <[particle]> time_to_live:10s
        // -->
        tagProcessor.registerMechanism("time_to_live", false, DurationTag.class, (object, mechanism, input) -> {
            object.particle.setMaxAge(input.getTicksAsInt());
        });

        // <--[tag]
        // @attribute <ParticleTag.scale>
        // @returns ElementTag(Decimal)
        // @mechanism ParticleTag.scale
        // @description
        // Returns the particle's scale, if it's of a type that supports scaling.
        // Note that some particles may do additional processing on their base scale, which can be overriden using the mechanism.
        // @example
        // # Use to make a particle twice as large.
        // - adjust <[particle]> scale:<[particle].scale.mul[2]>
        // -->
        tagProcessor.registerTag(ElementTag.class, "scale", (attribute, object) -> {
            if (object.particle instanceof BillboardParticleMixinAccess billboardParticle) {
                return new ElementTag(billboardParticle.clientizen$getScale());
            }
            return null;
        });

        // <--[mechanism]
        // @object ParticleTag
        // @name scale
        // @input ElementTag(Decimal)
        // @description
        // Sets the particle's scale, if it's of a type that supports scaling.
        // Note that this overrides the particle's normal scale calculations (some particles rescale based on their time lived, for example) with a hard-coded scale,
        // see <@link mechanism ParticleTag.multiply_scale> to modify the vanilla scaling instead of overriding it.
        // And see <@link mechanism ParticleTag.reset_scale> to go back to the default vanilla scaling.
        // @tags
        // <ParticleTag.scale>
        // @example
        // # Use to make a particle twice as large.
        // - adjust <[particle]> scale:<[particle].scale.mul[2]>
        // -->
        tagProcessor.registerMechanism("scale", false, ElementTag.class, (object, mechanism, input) -> {
            if (!(object.particle instanceof BillboardParticleMixinAccess billboardParticle)) {
                mechanism.echoError("Cannot set scale: particles of type '" + object.getTypeString() + "' don't support scaling.");
                return;
            }
            if (mechanism.requireFloat()) {
                billboardParticle.clientizen$setScale(mechanism.getValue().asFloat());
            }
        });

        // <--[mechanism]
        // @object ParticleTag
        // @name reset_scale
        // @input None
        // @description
        // Resets a scale override from <@link mechanism ParticleTag.scale> back to the particle's original vanilla scaling.
        // -->
        tagProcessor.registerMechanism("reset_scale", false, (object, mechanism) -> {
            if (!(object.particle instanceof BillboardParticleMixinAccess billboardParticle)) {
                mechanism.echoError("Cannot reset scale: particles of type '" + object.getTypeString() + "' don't support scaling.");
                return;
            }
            billboardParticle.clientizen$setScale(null);
        });

        // <--[tag]
        // @attribute <ParticleTag.on_ground>
        // @returns ElementTag(Boolean)
        // @description
        // Returns whether the particle's on the ground or in the air.
        // See <@link tag ParticleTag.world_collision> for whether the particle collides with the ground.
        // @example
        // # Use to make the particle blue if it's on the ground.
        // - if <[particle].on_ground>:
        //   - adjust <[particle]> color:blue
        // -->
        tagProcessor.registerTag(ElementTag.class, "on_ground", (attribute, object) -> {
            return new ElementTag(object.getAccessor().isOnGround());
        });

        // <--[mechanism]
        // @object ParticleTag
        // @name randomize_texture
        // @input None
        // @description
        // Applies a random texture from the particle's texture list, if it's of a type that supports textures.
        // See <@link mechanism ParticleTag.texture> for setting a specific texture.
        // -->
        tagProcessor.registerMechanism("randomize_texture", false, (object, mechanism) -> {
            if (!(object.particle instanceof SpriteBillboardParticle spriteParticle)) {
                mechanism.echoError("Cannot randomize texture: particles of type '" + object.getTypeString() + "' don't have textures.");
                return;
            }
            ParticleManager.SimpleSpriteProvider spriteProvider = getSpriteProviders().get(object.getTypeId());
            spriteParticle.setSprite(spriteProvider);
        });

        // <--[mechanism]
        // @object ParticleTag
        // @name update_age_texture
        // @input None
        // @description
        // Applies a texture from the particle's texture list based on the time it's lived.
        // For example: a particle with 4 textures that exists for 20 seconds will apply a different texture every 5 seconds, when this mechanism is used.
        // See <@link mechanism ParticleTag.texture> for setting a specific texture.
        // -->
        tagProcessor.registerMechanism("update_age_texture", false, (object, mechanism) -> {
            if (!(object.particle instanceof SpriteBillboardParticle spriteParticle)) {
                mechanism.echoError("Cannot update texture for age: particles of type '" + object.getTypeString() + "' don't have textures.");
                return;
            }
            ParticleManager.SimpleSpriteProvider spriteProvider = getSpriteProviders().get(object.getTypeId());
            spriteParticle.setSpriteForAge(spriteProvider);
        });

        // <--[mechanism]
        // @object ParticleTag
        // @name multiply_scale
        // @input ElementTag(Decimal)
        // @description
        // Multiplies the particle's current vanilla scale by the input amount, without overriding it.
        // See <@link mechanism ParticleTag.scale> for overriding the vanilla scaling with a specific scale.
        // @example
        // # Use to make a particle twice as big without overriding its built-in scaling logic.
        // - adjust <[particle]> multiply_scale:2
        // -->
        tagProcessor.registerMechanism("multiply_scale", false, ElementTag.class, (object, mechanism, input) -> {
            if (mechanism.requireFloat()) {
                object.particle.scale(input.asFloat());
            }
        });

        // <--[mechanism]
        // @object ParticleTag
        // @name remove
        // @input None
        // @description
        // Removes a particle from the world.
        // -->
        tagProcessor.registerMechanism("remove", false, (object, mechanism) -> {
            object.particle.markDead();
        });
    }

    public static final ObjectTagProcessor<ParticleTag> tagProcessor = new ObjectTagProcessor<>();

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
        mechanism.echoError("Cannot apply properties to a ParticleTag.");
    }

    @Override
    public boolean isUnique() {
        return true;
    }

    @Override
    public String identify() {
        return "particle@" + getMixinAccess().clientizen$getUUID();
    }

    @Override
    public String identifySimple() {
        return identify();
    }

    @Override
    public String debuggable() {
        return "<LG>particle@<Y>" + getMixinAccess().clientizen$getUUID() + " <GR>(" + getTypeString() + ")";
    }

    @Override
    public String toString() {
        return identify();
    }

    String prefix;

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
