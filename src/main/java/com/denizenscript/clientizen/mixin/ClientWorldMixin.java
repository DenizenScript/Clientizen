package com.denizenscript.clientizen.mixin;

import com.denizenscript.clientizen.Clientizen;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {

    @Inject(method = "disconnect", at = @At("HEAD"))
    private void clientizen$onDisconnect(CallbackInfo ci) {
        Clientizen.SYNC_DISCONNECT.invoker().run();
    }
}
