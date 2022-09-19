package com.denizenscript.clientizen.serverevents;

import com.denizenscript.clientizen.network.DataDeserializer;
import com.denizenscript.clientizen.network.DataSerializer;

public abstract class ServerEvent {

	public boolean enabled;

	public void enable(DataDeserializer data) {}

	public void disable() {}

	public void fire(DataSerializer data) {
		ServerEventManager.fire(this, data);
	}
}
