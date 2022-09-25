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
				String id = message.readString();
				ServerEvent event = serverEvents.get(id);
				if (event == null) {
					Debug.echoError("Invalid event '" + id + "' received from server! is your Clientizen version out of date?");
					return;
				}
				event.enabled = true;
				event.enable(message);
			}
		});
	}

	public static void registerEvent(Class<? extends ServerEvent> event) {
		try {
			ServerEvent instance = event.getConstructor().newInstance();
			if (serverEvents.containsKey(instance.id)) {
				Debug.echoError("Tried registering event '" + instance.id + "' but an event with that ID is already registered.");
				return;
			}
			serverEvents.put(instance.id, instance);
		}
		catch (Exception ex) {
			Debug.echoError("Something went wrong while registering server event '" + event.getName() + "':");
			Debug.echoError(ex);
		}
	}

	public static void fire(ServerEvent event, DataSerializer data) {
		DataSerializer eventData = new DataSerializer().writeString(event.id);
		if (data != null) {
			eventData.writeBytes(data.byteBuf.array());
		}
		NetworkManager.send(Channels.FIRE_EVENT, eventData);
	}
}
