package com.denizenscript.clientizen.mixin.render;

import com.denizenscript.clientizen.scripts.commands.AttachCommand;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {

	private Entity clientizen$renderingEntity;

	@Inject(method = "setupTransforms", cancellable = true, at = @At("HEAD"))
	private void clientizen$cancelAttachAnimation(T entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta, CallbackInfo ci) {
		AttachCommand.AttachData data = AttachCommand.attachedEntities.get(entity.getUuid());
		if (data != null && data.noAnimation()) {
			ci.cancel();
		}
	}

	@Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
	private void clientizen$captureRenderingEntity(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
		clientizen$renderingEntity = livingEntity;
	}

	// TODO fix this (ordinal wrong maybe)
	@ModifyVariable(
			method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
			at = @At(value = "STORE"), ordinal = 8)
	private float clientizen$cancelAttachAngles(float animationProgress) {
		if (clientizen$renderingEntity != null) {
			AttachCommand.AttachData data = AttachCommand.attachedEntities.get(clientizen$renderingEntity.getUuid());
			if (data != null && data.noAnimation()) {
				return 0.0f;
			}
		}
		return animationProgress;
	}

	@Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;setAngles(Lnet/minecraft/entity/Entity;FFFFF)V"))
	private void clienizen$disableAnimations(M instance, Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		AttachCommand.AttachData data = AttachCommand.attachedEntities.get(entity.getUuid());
		if (data == null || !data.noAnimation()) {
			instance.setAngles((T) entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
		}
	}
}
