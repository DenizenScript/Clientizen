package com.denizenscript.clientizen.events.server;

public class ServerEventRegistry {

	public static void registerEvents() {
		ServerEventManager.registerEvent(KeyPressReleaseServerEvent.class);
		ServerEventManager.registerEvent(ScreenOpenCloseServerEvent.class);
	}
}
