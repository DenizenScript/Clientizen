package com.denizenscript.clientizen;

import com.denizenscript.clientizen.network.NetworkManager;
import com.denizenscript.clientizen.util.impl.DenizenCoreImpl;
import com.denizenscript.denizencore.DenizenCore;
import com.denizenscript.denizencore.DenizenImplementation;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Clientizen implements ClientModInitializer {

	public static final String ID = "clientizen";

	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static String version;

	public DenizenImplementation coreImplementation = new DenizenCoreImpl();

	@Override
	public void onInitializeClient(ModContainer mod) {
		DenizenCore.init(coreImplementation);
		version = mod.metadata().version().raw();
		Debug.log("Clientizen", "Loading Clientizen v" + version);
		NetworkManager.instance = new NetworkManager();
	}
}
