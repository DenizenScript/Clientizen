package com.denizenscript.clientizen.mixin;

import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Particle.class)
public interface ParticleAccessor {

    @Invoker
    void invokeSetAlpha(float alpha);

    // MCDev plugin bug, need to specify the name in the annotation to avoid IDE errors
    @Accessor("x")
    double getX();

    @Accessor("y")
    double getY();

    @Accessor("z")
    double getZ();

    @Accessor
    double getVelocityX();

    @Accessor
    double getVelocityY();

    @Accessor
    double getVelocityZ();
}
