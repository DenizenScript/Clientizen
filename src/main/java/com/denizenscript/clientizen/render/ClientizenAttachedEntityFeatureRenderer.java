package com.denizenscript.clientizen.render;

import com.denizenscript.clientizen.objects.EntityTag;
import com.denizenscript.clientizen.scripts.commands.AttachCommand;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class ClientizenAttachedEntityFeatureRenderer<T extends Entity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {

	public ClientizenAttachedEntityFeatureRenderer(FeatureRendererContext<T, M> context) {
		super(context);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		List<EntityTag> attached = AttachCommand.attachMap.get(entity.getUuid());
		if (attached == null) {
			return;
		}
		for (EntityTag entityTag : attached) {
			//System.out.println(entityTag);
			matrices.push();
			matrices.scale(1, -1, 1);
			// Empty vector = the client player's center position more or less
			Vec3d pos = MinecraftClient.getInstance().player.getPos().subtract(entity.getPos());
			MinecraftClient.getInstance().getEntityRenderDispatcher().render(entityTag.entity, pos.x, pos.y, pos.z, entityTag.entity.getYaw(tickDelta), tickDelta, matrices, vertexConsumers, light);
			matrices.pop();
		}
	}
}
