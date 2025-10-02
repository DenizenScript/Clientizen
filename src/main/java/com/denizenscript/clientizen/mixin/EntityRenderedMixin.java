package com.denizenscript.clientizen.mixin;

import com.denizenscript.clientizen.events.EntityStartsStopsRenderingScriptEvent;
import com.denizenscript.clientizen.objects.EntityTag;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;
import java.util.UUID;

@Mixin(EntityRenderManager.class)
public class EntityRenderedMixin {

    @Inject(method = "shouldRender", at = @At("RETURN"))
    private <E extends Entity> void clientizen$isEntityRendered(E entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        UUID uuid = entity.getUuid();
        Set<UUID> renderedEntities = EntityTag.getRenderedEntities();
        if (cir.getReturnValue() && renderedEntities.add(uuid)) {
            EntityStartsStopsRenderingScriptEvent.instance.handleEntityRendered(entity, true);
        }
        else if (!cir.getReturnValue() && renderedEntities.remove(uuid)) {
            EntityStartsStopsRenderingScriptEvent.instance.handleEntityRendered(entity, false);
        }
    }
}
