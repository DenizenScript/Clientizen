package com.denizenscript.clientizen.tags;

import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

import java.util.UUID;

public class EntityTag implements ObjectTag {

	public UUID uuid;

	public EntityTag(Entity entity) {
		uuid = entity.getUuid();
	}

	public Entity getEntity() {
		return MinecraftClient.getInstance().world.getEntityLookup().get(uuid);
	}

	public static ObjectTagProcessor<EntityTag> tagProcessor = new ObjectTagProcessor<>();

	private String prefix = "Entity";

	@Override
	public String getPrefix() {
		return prefix;
	}

	@Override
	public boolean isUnique() {
		return true;
	}

	@Override
	public String identify() {
		return "e@" + uuid;
	}

	@Override
	public String identifySimple() {
		return identify();
	}

	@Override
	public ObjectTag setPrefix(String prefix) {
		this.prefix = prefix;
		return this;
	}
}
