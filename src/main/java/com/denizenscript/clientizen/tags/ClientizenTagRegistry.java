package com.denizenscript.clientizen.tags;

import com.denizenscript.clientizen.objects.EntityTag;
import com.denizenscript.denizencore.objects.ObjectFetcher;
import com.denizenscript.denizencore.objects.ObjectType;

public class ClientizenTagRegistry {

	public static void registerTagHandlers() {
		new ClientTagBase();
	}
}
