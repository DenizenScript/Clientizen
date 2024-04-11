package com.denizenscript.clientizen.mixin.particle;

import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SpriteBillboardParticle.class)
public interface SpriteBillboardParticleAccessor {

    @Accessor
    Sprite getSprite();

    @Invoker
    void invokeSetSprite(Sprite sprite);
}
