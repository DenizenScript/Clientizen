package com.denizenscript.clientizen.objects;

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
        tagProcessor.registerTag(ElementTag.class, "type", (attribute, object) -> {
            return new ElementTag(object.getTypeString(), true);
        });

        tagProcessor.registerTag(ScriptTag.class, "script", (attribute, object) -> {
            if (object.particle instanceof ParticleScriptContainer.ClientizenParticle clientizenParticle) {
                return new ScriptTag(clientizenParticle.particleScript);
            }
            return null;
        });

        tagProcessor.registerTag(LocationTag.class, "location", (attribute, object) -> {
            ParticleAccessor particle = object.getAccessor();
            return new LocationTag(particle.getX(), particle.getY(), particle.getZ());
        });

        tagProcessor.registerMechanism("location", false, LocationTag.class, (object, mechanism, input) -> {
            object.particle.setPos(input.getX(), input.getY(), input.getZ());
        });

        tagProcessor.registerTag(LocationTag.class, "velocity", (attribute, object) -> {
            ParticleAccessor particle = object.getAccessor();
            return new LocationTag(particle.getVelocityX(), particle.getVelocityY(), particle.getVelocityZ());
        });

        tagProcessor.registerMechanism("velocity", false, LocationTag.class, (object, mechanism, input) -> {
            object.particle.setVelocity(input.getX(), input.getY(), input.getZ());
        });

        tagProcessor.registerTag(ElementTag.class, "texture", (attribute, object) -> {
            if (object.particle instanceof SpriteBillboardParticleAccessor spriteParticle) {
                return new ElementTag(Utilities.idToString(spriteParticle.getSprite().getContents().getId()), true);
            }
            return null;
        });

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

        tagProcessor.registerTag(ColorTag.class, "color", (attribute, object) -> {
            ParticleAccessor particle = object.getAccessor();
            return new ColorTag((int) (particle.getRed() * 255f), (int) (particle.getGreen() * 255f), (int) (particle.getBlue() * 255f), (int) (particle.getAlpha() * 255f));
        });

        tagProcessor.registerMechanism("color", false, ColorTag.class, (object, mechanism, input) -> {
            object.particle.setColor(input.red / 255f, input.green / 255f, input.blue / 255f);
            object.getAccessor().invokeSetAlpha(input.alpha / 255f);
        });

        tagProcessor.registerTag(ElementTag.class, "world_collision", (attribute, object) -> {
            return new ElementTag(object.getAccessor().collidesWithWorld());
        });

        tagProcessor.registerMechanism("world_collision", false, ElementTag.class, (object, mechanism, input) -> {
            if (mechanism.requireBoolean()) {
                object.getAccessor().setCollidesWithWorld(input.asBoolean());
            }
        });

        tagProcessor.registerTag(DurationTag.class, "time_lived", (attribute, object) -> {
            return new DurationTag((long) object.getAccessor().getAge());
        });

        tagProcessor.registerMechanism("time_lived", false, DurationTag.class, (object, mechanism, input) -> {
            object.getAccessor().setAge(input.getTicksAsInt());
        });

        tagProcessor.registerTag(DurationTag.class, "time_to_live", (attribute, object) -> {
            return new DurationTag((long) object.particle.getMaxAge());
        });

        tagProcessor.registerMechanism("time_to_live", false, DurationTag.class, (object, mechanism, input) -> {
            object.particle.setMaxAge(input.getTicksAsInt());
        });

        tagProcessor.registerTag(ElementTag.class, "on_ground", (attribute, object) -> {
            return new ElementTag(object.getAccessor().isOnGround());
        });

        tagProcessor.registerMechanism("randomize_texture", false, (object, mechanism) -> {
            if (!(object.particle instanceof SpriteBillboardParticle spriteParticle)) {
                mechanism.echoError("Cannot randomize texture: particles of type '" + object.getTypeString() + "' don't have textures.");
                return;
            }
            ParticleManager.SimpleSpriteProvider spriteProvider = getSpriteProviders().get(object.getTypeId());
            spriteParticle.setSprite(spriteProvider);
        });

        tagProcessor.registerMechanism("update_age_texture", false, (object, mechanism) -> {
            if (!(object.particle instanceof SpriteBillboardParticle spriteParticle)) {
                mechanism.echoError("Cannot update texture for age: particles of type '" + object.getTypeString() + "' don't have textures.");
                return;
            }
            ParticleManager.SimpleSpriteProvider spriteProvider = getSpriteProviders().get(object.getTypeId());
            spriteParticle.setSpriteForAge(spriteProvider);
        });

        tagProcessor.registerMechanism("multiply_scale", false, ElementTag.class, (object, mechanism, input) -> {
            if (mechanism.requireFloat()) {
                object.particle.scale(input.asFloat());
            }
        });

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
