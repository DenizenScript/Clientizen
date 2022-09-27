package com.denizenscript.clientizen.serverevents;

import com.denizenscript.clientizen.network.DataDeserializer;
import com.denizenscript.clientizen.network.DataSerializer;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

public class KeyPressReleaseServerEvent extends ServerEvent {

	public static KeyPressReleaseServerEvent instance;

	private static final IntSet listenToPressKeys = new IntOpenHashSet();
	private static final IntSet listenToReleaseKeys = new IntOpenHashSet();

	public KeyPressReleaseServerEvent() {
		instance = this;
		id = "PlayerPressReleaseKey";
	}

	public void handleKeyPressStateChange(int key, boolean pressed) {
		if ((pressed ? listenToPressKeys : listenToReleaseKeys).contains(key)) {
			fire(new DataSerializer().writeInt(key).writeBoolean(pressed));
		}
	}

	@Override
	public void enable(DataDeserializer data) {
		listenToPressKeys.addAll(data.readIntList());
		listenToReleaseKeys.addAll(data.readIntList());
	}

	@Override
	public void disable() {
		listenToPressKeys.clear();
		listenToReleaseKeys.clear();
	}
}
