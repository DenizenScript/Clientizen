package com.denizenscript.clientizen.mixin.render;

import com.denizenscript.clientizen.render.ClientizenAttachedEntityFeatureRenderer;
import com.denizenscript.clientizen.scripts.commands.AttachCommand;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class EntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {

	@Shadow
	public abstract boolean addFeature(FeatureRenderer<T, M> feature);

	@Inject(method = "<init>", at = @At("TAIL"))
	private void clientizen$addAttachFeatureRenderer(EntityRendererFactory.Context ctx, EntityModel<T> model, float shadowRadius, CallbackInfo ci) {
		LivingEntityRenderer<T, M> renderer = (LivingEntityRenderer<T, M>) (Object) this;
		addFeature(new ClientizenAttachedEntityFeatureRenderer<>(renderer));
	}

	/*@Inject(method = "getPositionOffset", cancellable = true, at = @At("HEAD"))
	private void clientizen$getAttachedOffset(T entity, float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
		if (AttachCommand.attachedEntities.containsKey(entity.getUuid())) {
			cir.setReturnValue(AttachCommand.attachedEntities.get(entity.getUuid()).getEntity().getLerpedPos(tickDelta).subtract(entity.getLerpedPos(tickDelta)));
		}
	}*/
}
