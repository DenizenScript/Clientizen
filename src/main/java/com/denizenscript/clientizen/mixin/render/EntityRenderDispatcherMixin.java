package com.denizenscript.clientizen.mixin.render;

import com.denizenscript.clientizen.scripts.commands.AttachCommand;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

	/*@Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
	private static <E extends Entity> void clientizen$cancelRenderIfAttached(
			E entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
		if (AttachCommand.attachedEntities.containsKey(entity.getUuid())) {
			cir.setReturnValue(false);
		}
	}*/
}
