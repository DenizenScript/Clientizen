package com.denizenscript.clientizen.objects;

import com.denizenscript.denizencore.objects.Fetchable;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

import java.util.UUID;

public class EntityTag implements ObjectTag {

	public UUID uuid;

	public EntityTag(UUID uuid) {
		this.uuid = uuid;
	}

	public EntityTag(Entity entity) {
		this(entity.getUuid());
	}

	@Fetchable("e")
	public static EntityTag valueOf(String string, TagContext tagContext) {
		if (string == null) {
			return null;
		}
		if (string.startsWith("e@")) {
			return new EntityTag(UUID.fromString(string.substring(2)));
		}
		if (tagContext == null || tagContext.showErrors()) {
			Debug.log("valueOf EntityTag returning null: " + string);
		}
		return null;
	}

	public Entity getEntity() {
		return MinecraftClient.getInstance().world.getEntityLookup().get(uuid);
	}

	public static ObjectTagProcessor<EntityTag> tagProcessor = new ObjectTagProcessor<>();

	public static void registerTags() {

		tagProcessor.registerTag(ElementTag.class, "entity_type", (attribute, object) -> {
			return new ElementTag(object.getEntity().getType().getUntranslatedName(), true);
		});
	}

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
