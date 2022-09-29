package com.denizenscript.clientizen.events.server;

import com.denizenscript.clientizen.network.DataDeserializer;
import com.denizenscript.clientizen.network.DataSerializer;

public abstract class ServerEvent {

	public boolean enabled;
	public String id;

	// enable and disable aren't abstract to avoid events that don't need to send / receive data having empty impls
	public void enable(DataDeserializer data) {}

	public void disable() {}

	public void fire(DataSerializer data) {
		ServerEventManager.fire(this, data);
	}
}
