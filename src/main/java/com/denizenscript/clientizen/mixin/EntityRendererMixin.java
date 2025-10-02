package com.denizenscript.clientizen.mixin;

import com.denizenscript.clientizen.objects.EntityTag;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {

    @WrapOperation(method = "updateRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getTeamColorValue()I"))
    private int clientizen$modifyGlowColor(Entity instance, Operation<Integer> original) {
        Integer glowColor = instance.getAttached(EntityTag.GLOW_COLOR_OVERRIDE);
        return glowColor != null ? glowColor : original.call(instance);
    }
}
