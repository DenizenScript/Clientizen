package com.denizenscript.clientizen.access;

import net.minecraft.util.Identifier;

public interface RegistryMixinAccess {

    void clientizen$unfreeze();

    void clientizen$remove(Identifier toRemove);
}
