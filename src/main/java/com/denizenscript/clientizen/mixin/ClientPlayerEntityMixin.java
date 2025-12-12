package com.denizenscript.clientizen.mixin;

import com.denizenscript.clientizen.events.PlayerSprintScriptEvent;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayer {

    // <editor-fold defaultstate="collapsed" desc="Dummy constructor">
    private ClientPlayerEntityMixin(ClientLevel world, GameProfile profile) {
        super(world, profile);
    }
    // </editor-fold>

    @Unique
    boolean clientizen$prevSprintState;

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;canStartSprinting()Z"))
    private void clientizen$beforeSprintHandling(CallbackInfo ci) {
        clientizen$prevSprintState = isSprinting();
    }

    @Inject(method = "aiStep", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Abilities;mayfly:Z"))
    private void clientizen$afterSprintHandling(CallbackInfo ci) {
        boolean isSprinting = isSprinting();
        if (isSprinting != clientizen$prevSprintState && PlayerSprintScriptEvent.instance.handleSprintingToggle(this, isSprinting)) {
            setSprinting(clientizen$prevSprintState);
        }
    }
}
