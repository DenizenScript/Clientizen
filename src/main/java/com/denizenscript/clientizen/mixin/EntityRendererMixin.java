package com.denizenscript.clientizen.mixin;

import com.denizenscript.clientizen.objects.EntityTag;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {

    @WrapOperation(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getTeamColor()I"))
    private int clientizen$modifyGlowColor(Entity instance, Operation<Integer> original) {
        Integer glowColor = instance.getAttached(EntityTag.GLOW_COLOR_OVERRIDE);
        return glowColor != null ? glowColor : original.call(instance);
    }
}
