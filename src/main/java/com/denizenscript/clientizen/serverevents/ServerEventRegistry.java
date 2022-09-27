package com.denizenscript.clientizen.serverevents;

public class ServerEventRegistry {

	public static void registerEvents() {
		ServerEventManager.registerEvent(KeyPressReleaseServerEvent.class);
	}
}
