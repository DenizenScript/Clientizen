package com.denizenscript.clientizen.render;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class ClientizenAttachedEntityFeatureRenderer extends FeatureRenderer<Entity, EntityModel<Entity>> {

	public ClientizenAttachedEntityFeatureRenderer(FeatureRendererContext<Entity, EntityModel<Entity>> context) {
		super(context);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Entity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {

	}
}
