package com.denizenscript.clientizen.mixin.particle;

import net.minecraft.client.particle.ParticleManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ParticleManager.class)
public interface ParticleManagerAccessor {

    @Accessor("spriteAwareFactories")
    Map<Identifier, ParticleManager.SimpleSpriteProvider> getSpriteProviderMap();
}
