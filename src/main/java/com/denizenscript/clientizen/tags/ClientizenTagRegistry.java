package com.denizenscript.clientizen.tags;

import com.denizenscript.denizencore.objects.ObjectFetcher;
import com.denizenscript.denizencore.objects.ObjectType;

public class ClientizenTagRegistry {

	public static ObjectType<EntityTag> TYPE_ENTITY;

	public static void registerTagHandlers() {
		ClientTagBase.register();
		TYPE_ENTITY = ObjectFetcher.registerWithObjectFetcher(EntityTag.class, EntityTag.tagProcessor).setAsNOtherCode().generateBaseTag();
	}
}
