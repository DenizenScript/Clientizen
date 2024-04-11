package com.denizenscript.clientizen.access;

import net.minecraft.particle.ParticleType;

import java.util.UUID;

public interface ParticleMixinAccess {

    UUID clientizen$getUUID();

    ParticleType<?> clientizen$getType();

    void clientizen$setType(ParticleType<?> type);
}
