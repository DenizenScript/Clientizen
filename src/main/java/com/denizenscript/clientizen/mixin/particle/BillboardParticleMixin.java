package com.denizenscript.clientizen.mixin.particle;

import com.denizenscript.clientizen.access.BillboardParticleMixinAccess;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.particle.BillboardParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BillboardParticle.class)
public abstract class BillboardParticleMixin implements BillboardParticleMixinAccess {

    @Shadow
    protected float scale;
    @Unique
    Float clientizen$scale;

    @Override
    public float clientizen$getScale() {
        return clientizen$scale != null ? clientizen$scale : scale;
    }

    @Override
    public void clientizen$setScale(Float scale) {
        clientizen$scale = scale;
    }

    @WrapOperation(
            method = "buildGeometry",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/BillboardParticle;getSize(F)F")
    )
    private float clientizen$overrideSize(BillboardParticle particle, float tickDelta, Operation<Float> original) {
        return clientizen$scale != null ? clientizen$scale : original.call(particle, tickDelta);
    }
}
