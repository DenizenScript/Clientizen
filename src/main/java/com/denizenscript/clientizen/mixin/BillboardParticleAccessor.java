package com.denizenscript.clientizen.mixin;

import net.minecraft.client.particle.SingleQuadParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SingleQuadParticle.class)
public interface BillboardParticleAccessor {

    @Invoker
    void invokeSetAlpha(float alpha);
}
