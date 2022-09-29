package com.denizenscript.clientizen.events.server;

import com.denizenscript.clientizen.network.DataDeserializer;
import com.denizenscript.clientizen.network.DataSerializer;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScreenOpenCloseServerEvent extends ServerEvent {

	public static ScreenOpenCloseServerEvent instance;

	public static Map<String, Class<?>> TYPE_MAP = new HashMap<>();

	static {
		TYPE_MAP.put("inventory", InventoryScreen.class);
		TYPE_MAP.put("creative", CreativeInventoryScreen.class);
		TYPE_MAP.put("pause", GameMenuScreen.class);
		TYPE_MAP.put("options", OptionsScreen.class);
		TYPE_MAP.put("advancements", AdvancementsScreen.class);
	}

	public static List<String> listenToOpenScreens = new ArrayList<>();
	public static List<String> listenToCloseScreens = new ArrayList<>();

	public ScreenOpenCloseServerEvent() {
		instance = this;
		id = "PlayerOpenCloseScreen";
	}

	public void handleScreenChange(Screen screen, Screen otherScreen, boolean open) {
		for (String type : open ? listenToOpenScreens : listenToCloseScreens) {
			if (type.equals("inventory") && screen instanceof CreativeInventoryScreen) {
				continue;
			}
			if (TYPE_MAP.get(type).isInstance(screen)) {
				DataSerializer serializer = new DataSerializer()
						.writeString(type)
						.writeBoolean(open)
						.writeBoolean(otherScreen != null);
				fire(serializer);
				return;
			}
		}
	}

	@Override
	public void enable(DataDeserializer data) {
		listenToOpenScreens.addAll(data.readStringList());
		listenToCloseScreens.addAll(data.readStringList());
	}

	@Override
	public void disable() {
		listenToOpenScreens.clear();
		listenToCloseScreens.clear();
	}
}
