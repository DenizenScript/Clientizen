package com.denizenscript.clientizen.mixin;

import com.denizenscript.clientizen.access.LivingEntityMixinAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.stat.StatHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

    // TODO: proper persistence system
    @Inject(method = "<init>", at = @At("TAIL"))
    private void clientizen$persistClimbingSpeed(MinecraftClient client, ClientWorld world, ClientPlayNetworkHandler networkHandler, StatHandler stats, ClientRecipeBook recipeBook, boolean lastSneaking, boolean lastSprinting, CallbackInfo ci) {
        if (client.player != null) {
            ((LivingEntityMixinAccess) this).clientizen$setClimbingSpeed(((LivingEntityMixinAccess) client.player).clientizen$getClimbingSpeed());
        }
    }
}
