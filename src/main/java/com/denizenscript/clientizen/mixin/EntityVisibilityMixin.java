package com.denizenscript.clientizen.mixin;

import com.denizenscript.clientizen.events.EntitySeenUnseenByCamera;
import com.denizenscript.clientizen.objects.EntityTag;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.UUID;

@Mixin(EntityRenderDispatcher.class)
public class EntityVisibilityMixin {

    @Inject(method = "shouldRender", at = @At("RETURN"))
    private <E extends Entity> void clientizen$isEntityVisible(E entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        UUID uuid = entity.getUuid();
        List<UUID> visibleEntities = EntityTag.getVisibleEntities();
        if (!visibleEntities.contains(uuid) && cir.getReturnValue()) {
            visibleEntities.add(uuid);
            EntitySeenUnseenByCamera.instance.handleEntitySeenUnseen(new EntityTag(entity), true);
        }
        else if (visibleEntities.contains(uuid) && !cir.getReturnValue()) {
            visibleEntities.remove(uuid);
            EntitySeenUnseenByCamera.instance.handleEntitySeenUnseen(new EntityTag(entity), false);
        }
    }
}
