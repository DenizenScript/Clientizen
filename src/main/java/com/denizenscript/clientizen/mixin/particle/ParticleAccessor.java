package com.denizenscript.clientizen.mixin.particle;

import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Particle.class)
public interface ParticleAccessor {

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

    @Accessor
    float getRed();

    @Accessor
    float getGreen();

    @Accessor
    float getBlue();

    @Accessor
    float getAlpha();

    @Invoker
    void invokeSetAlpha(float alpha);

    @Accessor
    boolean isOnGround();

    @Accessor("collidesWithWorld")
    boolean collidesWithWorld();

    @Accessor
    void setCollidesWithWorld(boolean collidesWithWorld);

    @Accessor
    int getAge();

    @Accessor
    void setAge(int age);
}
