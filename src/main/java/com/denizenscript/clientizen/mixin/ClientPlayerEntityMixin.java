package com.denizenscript.clientizen.mixin;

import com.denizenscript.clientizen.events.PlayerSprintScriptEvent;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    // <editor-fold defaultstate="collapsed" desc="Dummy constructor">
    private ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }
    // </editor-fold>

    @Unique
    boolean clientizen$prevSprintState;

    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;canStartSprinting()Z"))
    private void clientizen$beforeSprintHandling(CallbackInfo ci) {
        clientizen$prevSprintState = isSprinting();
    }

    @Inject(method = "tickMovement", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerAbilities;allowFlying:Z"))
    private void clientizen$afterSprintHandling(CallbackInfo ci) {
        boolean isSprinting = isSprinting();
        if (isSprinting != clientizen$prevSprintState && PlayerSprintScriptEvent.instance.handleSprintingToggle(this, isSprinting)) {
            setSprinting(clientizen$prevSprintState);
        }
    }
}
