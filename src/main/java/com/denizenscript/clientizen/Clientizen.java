package com.denizenscript.clientizen;

import com.denizenscript.clientizen.network.NetworkManager;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Clientizen implements ClientModInitializer {

	public static final String ID = "clientizen";

	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static String version;

	@Override
	public void onInitializeClient(ModContainer mod) {
		version = mod.metadata().version().raw();
		NetworkManager.instance = new NetworkManager();
		LOGGER.info("Loading Clientizen v" + version);
	}
}
