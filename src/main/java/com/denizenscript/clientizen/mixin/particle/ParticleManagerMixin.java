package com.denizenscript.clientizen.mixin.particle;

import com.denizenscript.clientizen.access.ParticleMixinAccess;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParticleManager.class)
public abstract class ParticleManagerMixin {

    @Inject(method = "createParticle", at = @At("RETURN"))
    private <T extends ParticleEffect> void clientizen$storeParticleType(T parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfoReturnable<Particle> cir) {
        if (cir.getReturnValue() instanceof ParticleMixinAccess particle) {
            particle.clientizen$setType(parameters.getType());
        }
    }
}
