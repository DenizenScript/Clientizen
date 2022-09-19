package com.denizenscript.clientizen.serverevents;

import com.denizenscript.clientizen.network.DataDeserializer;
import com.denizenscript.clientizen.network.DataSerializer;

import java.util.HashSet;
import java.util.Set;

public class PlayerPressesKey extends ServerEvent {

	Set<Integer> listenToKeys = new HashSet<>();
	public static PlayerPressesKey instance;

	public PlayerPressesKey() {
		instance = this;
	}

	public void handleKeyPress(int key) {
		if (listenToKeys.contains(key) || listenToKeys.isEmpty()) {
			fire(new DataSerializer().writeInt(key));
		}
	}

	@Override
	public void enable(DataDeserializer data) {
		listenToKeys.addAll(data.readIntList());
	}

	@Override
	public void disable() {
		listenToKeys.clear();
	}
}
