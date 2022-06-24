package com.denizenscript.clientizen.scripts.containers;

import com.denizenscript.denizencore.scripts.ScriptRegistry;

public class ClientizenContainerRegistry {

	public static void registerContainers() {
		ScriptRegistry._registerCoreTypes();
		ScriptRegistry._registerType("gui", GuiScriptContainer.class);
	}
}
