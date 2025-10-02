package com.denizenscript.clientizen.mixin;

import net.minecraft.client.particle.BillboardParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BillboardParticle.class)
public interface BillboardParticleAccessor {

    @Invoker
    void invokeSetAlpha(float alpha);
}
