package com.denizenscript.clientizen.mixin;

import com.denizenscript.clientizen.network.NetworkManager;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "handleLogin", at = @At("TAIL"))
    private void clientizen$onGameJoin(CallbackInfo ci) {
        NetworkManager.onConnect();
    }
}
