package com.denizenscript.clientizen.mixin.render;

import com.denizenscript.clientizen.scripts.commands.AttachCommand;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {

	/*@Inject(method = "<init>", at = @At("TAIL"))
	private void clientizen$addAttachFeatureRenderer(EntityRendererFactory.Context ctx, CallbackInfo ci) {
		add
	}*/

	@Inject(method = "getPositionOffset", cancellable = true, at = @At("HEAD"))
	private void clientizen$getAttachedOffset(T entity, float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
		if (AttachCommand.attachedEntities.containsKey(entity.getUuid())) {
			cir.setReturnValue(AttachCommand.attachedEntities.get(entity.getUuid()).getEntity().getPos().subtract(entity.getPos()));
		}
	}
}
