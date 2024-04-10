package com.denizenscript.clientizen.objects;

import com.denizenscript.clientizen.access.ParticleMixinAccess;
import com.denizenscript.clientizen.mixin.ParticleAccessor;
import com.denizenscript.clientizen.util.Utilities;
import com.denizenscript.denizencore.objects.Adjustable;
import com.denizenscript.denizencore.objects.Fetchable;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import net.minecraft.client.particle.Particle;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ParticleTag implements Adjustable {

    public static final Map<UUID, Particle> particles = new HashMap<>();

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

    public static void register() {
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
        return "particle@" + ((ParticleMixinAccess) particle).clientizen$getUUID();
    }

    @Override
    public String identifySimple() {
        return identify();
    }

    @Override
    public String debuggable() {
        return "<LG>particle@<Y>" + ((ParticleMixinAccess) particle).clientizen$getUUID();
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
