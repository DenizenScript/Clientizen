package com.denizenscript.clientizen.mixin;

import com.denizenscript.clientizen.access.LivingEntityMixinAccess;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements LivingEntityMixinAccess {

    double clientizen$climbingSpeed = 0.2; // Vanilla default

    @Override
    public double clientizen$getClimbingSpeed() {
        return clientizen$climbingSpeed;
    }

    @Override
    public void clientizen$setClimbingSpeed(double climbingSpeed) {
        clientizen$climbingSpeed = climbingSpeed;
    }

    @Shadow
    public abstract boolean isClimbing();

    @ModifyArg(method = "applyMovementInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;<init>(DDD)V"), index = 1)
    private double clientizen$modifyClimbingSpeed(double y) {
        return isClimbing() ? clientizen$climbingSpeed : y;
    }
}
