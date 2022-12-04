package com.denizenscript.clientizen.tags;

import com.denizenscript.clientizen.objects.EntityTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.tags.PseudoObjectTagBase;
import com.denizenscript.denizencore.tags.TagManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

public class ClientTagBase extends PseudoObjectTagBase<ClientTagBase> {

	public static ClientTagBase instance;

	public ClientTagBase() {
		instance = this;
		TagManager.registerStaticTagBaseHandler(ClientTagBase.class, "client", t -> instance);
	}

	@Override
	public void register() {
		tagProcessor.registerTag(ListTag.class, "loaded_entities", (attribute, object) -> {
			ListTag list = new ListTag();
			for (Entity entity : MinecraftClient.getInstance().world.getEntities()) {
				list.addObject(new EntityTag(entity));
			}
			return list;
		});
		tagProcessor.registerTag(EntityTag.class, "target", (attribute, object) -> {
			Entity target = MinecraftClient.getInstance().targetedEntity;
			return target != null ? new EntityTag(target) : null;
		});
	}
}
