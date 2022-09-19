package com.denizenscript.clientizen.serverevents;

import com.denizenscript.clientizen.network.Channels;
import com.denizenscript.clientizen.network.DataSerializer;
import com.denizenscript.clientizen.network.NetworkManager;
import com.denizenscript.denizencore.utilities.debugging.Debug;

import java.util.HashMap;
import java.util.Map;

public class ServerEventManager {

	public static final Map<String, ServerEvent> serverEvents = new HashMap<>();

	public static void init() {
		NetworkManager.registerInChannel(Channels.EVENT_DATA, message -> {
			for (ServerEvent event : serverEvents.values()) {
				if (event.enabled) {
					event.enabled = false;
					event.disable();
				}
			}
			int size = message.readInt();
			for (int i = 0; i < size; i++) {
				String name = message.readString();
				ServerEvent event = serverEvents.get(name);
				if (event == null) {
					Debug.echoError("Invalid event '" + name + "' received from the server!");
					return;
				}
				event.enable(message);
				event.enabled = true;
			}
		});
	}

	public static void registerEvent(Class<? extends ServerEvent> event) {
		try {
			serverEvents.put(event.getSimpleName(), event.getConstructor().newInstance());
		}
		catch (Exception ex) {
			Debug.echoError("Something went wrong while registering server event '" + event.getName() + "':");
			Debug.echoError(ex);
		}
	}

	public static void fire(ServerEvent event, DataSerializer data) {
		DataSerializer eventData = new DataSerializer().writeString(event.getClass().getSimpleName());
		if (data != null) {
			eventData.writeBytes(data.byteBuf.array());
		}
		NetworkManager.send(Channels.FIRE_EVENT, eventData);
	}
}
