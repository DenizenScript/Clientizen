package com.denizenscript.clientizen.mixin.particle;

import com.denizenscript.clientizen.access.ParticleMixinAccess;
import com.denizenscript.clientizen.objects.ParticleTag;
import com.denizenscript.denizencore.flags.MapTagFlagTracker;
import net.minecraft.client.particle.Particle;
import net.minecraft.particle.ParticleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(Particle.class)
public abstract class ParticleMixin implements ParticleMixinAccess {

    @Unique
    final UUID clientizen$id = UUID.randomUUID();
    @Unique
    ParticleType<?> clientizen$particleType;
    @Unique
    MapTagFlagTracker clientizen$flagMap;

    @Inject(method = "<init>(Lnet/minecraft/client/world/ClientWorld;DDD)V", at = @At("TAIL"))
    private void clientizen$onParticleCreated(CallbackInfo ci) {
        ParticleTag.particles.put(clientizen$id, (Particle) (Object) this);
    }

    @Inject(method = "markDead", at = @At("TAIL"))
    private void clientizen$onParticleRemoved(CallbackInfo ci) {
        ParticleTag.particles.remove(clientizen$id);
    }

    @Override
    public UUID clientizen$getUUID() {
        return clientizen$id;
    }

    @Override
    public ParticleType<?> clientizen$getType() {
        return clientizen$particleType;
    }

    @Override
    public void clientizen$setType(ParticleType<?> type) {
        clientizen$particleType = type;
    }

    @Override
    public MapTagFlagTracker clientizen$getFlagTracker() {
        if (clientizen$flagMap == null) {
            clientizen$flagMap = new MapTagFlagTracker();
        }
        return clientizen$flagMap;
    }
}
