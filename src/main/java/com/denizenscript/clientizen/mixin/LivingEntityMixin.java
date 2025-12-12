package com.denizenscript.clientizen.mixin;

import com.denizenscript.clientizen.tags.ClientTagBase;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    public abstract boolean onClimbable();

    @ModifyArg(method = "handleRelativeFrictionAndCalculateMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;<init>(DDD)V"), index = 1)
    private double clientizen$modifyClimbingSpeed(double y) {
        return onClimbable() ? ClientTagBase.climbingSpeed : y;
    }
}
