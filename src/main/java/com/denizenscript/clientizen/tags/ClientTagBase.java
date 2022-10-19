package com.denizenscript.clientizen.tags;

import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.tags.PseudoObjectTagBase;
import com.denizenscript.denizencore.tags.TagManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

public class ClientTagBase extends PseudoObjectTagBase<ClientTagBase> {

	public static ClientTagBase instance;

	public static void register() {
		instance = new ClientTagBase();
		TagManager.registerStaticTagBaseHandler(ClientTagBase.class, "client", t -> instance);
	}

	@Override
	public void registerTags() {

		tagProcessor.registerTag(ListTag.class, "loaded_entities", (attribute, object) -> {
			ListTag list = new ListTag();
			for (Entity entity : MinecraftClient.getInstance().world.getEntities()) {
				list.addObject(new EntityTag(entity));
			}
			return list;
		});

		tagProcessor.registerTag(EntityTag.class, "target", (attribute, object) -> {
			System.out.println(MinecraftClient.getInstance().targetedEntity);
			return new EntityTag(MinecraftClient.getInstance().targetedEntity);
		});
	}
}
