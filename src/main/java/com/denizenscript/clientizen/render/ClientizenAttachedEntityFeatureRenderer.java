package com.denizenscript.clientizen.render;

import com.denizenscript.clientizen.objects.EntityTag;
import com.denizenscript.clientizen.scripts.commands.AttachCommand;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Direction;

import java.util.List;

public class ClientizenAttachedEntityFeatureRenderer<T extends Entity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {

    public static void init() {
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
            registrationHelper.register(new ClientizenAttachedEntityFeatureRenderer<>(entityRenderer));
        });
    }

    public ClientizenAttachedEntityFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        List<EntityTag> attachedEntities = AttachCommand.attachMap.get(entity.getUuid());
        if (attachedEntities == null) {
            return;
        }
        for (EntityTag attached : attachedEntities) {
            matrices.push();
            matrices.scale(1, 1, 1);
            matrices.multiply(Direction.DOWN.getRotationQuaternion());
            MinecraftClient.getInstance().getEntityRenderDispatcher().render(attached.getEntity(), 0, 0, 0, attached.getEntity().getYaw(tickDelta), tickDelta, matrices, vertexConsumers, light);
            matrices.pop();
        }
    }
}
