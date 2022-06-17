package com.denizenscript.clientizen;

import com.denizenscript.clientizen.events.ClientizenScriptEventRegistery;
import com.denizenscript.clientizen.network.NetworkManager;
import com.denizenscript.clientizen.objects.ClientizenObjectRegistery;
import com.denizenscript.clientizen.scripts.commands.ClientizenCommandRegistry;
import com.denizenscript.clientizen.scripts.containers.ClientizenContainerRegistery;
import com.denizenscript.clientizen.tags.ClientizenTagContext;
import com.denizenscript.clientizen.tags.ClientizenTagRegistery;
import com.denizenscript.clientizen.util.impl.DenizenCoreImpl;
import com.denizenscript.denizencore.DenizenCore;
import com.denizenscript.denizencore.DenizenImplementation;
import com.denizenscript.denizencore.scripts.ScriptHelper;
import com.denizenscript.denizencore.utilities.CoreConfiguration;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import org.apache.logging.log4j.core.Core;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;
import org.quiltmc.qsl.networking.api.client.ClientPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Clientizen implements ClientModInitializer {

	public static final String ID = "clientizen";

	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static String version;

	public DenizenImplementation coreImplementation = new DenizenCoreImpl();

	@Override
	public void onInitializeClient(ModContainer mod) {
		CoreUtilities.noDebugContext = new ClientizenTagContext(false, null, null);
		CoreUtilities.noDebugContext.showErrors = () -> false;
		CoreUtilities.basicContext = new ClientizenTagContext(true, null, null);
		CoreUtilities.errorButNoDebugContext = new ClientizenTagContext(false, null, null);
		DenizenCore.init(coreImplementation);

		version = mod.metadata().version().raw();
		Debug.log("Clientizen", "Loading Clientizen v" + version);
		NetworkManager.instance = new NetworkManager();

		ClientizenCommandRegistry commandRegistry = new ClientizenCommandRegistry();
		commandRegistry.registerCommands();
		DenizenCore.commandRegistry = commandRegistry;
		ClientizenContainerRegistery.registerContainers();
		ClientizenScriptEventRegistery.registerEvents();
		ClientizenObjectRegistery.registerObjects();
		ClientizenTagRegistery.registerTagHandlers();


		CoreConfiguration.allowConsoleRedirection = false;
		CoreConfiguration.allowFileCopy = false;
		CoreConfiguration.allowFileRead = false;
		CoreConfiguration.allowFileWrite = false;
		CoreConfiguration.allowLog = false;
		CoreConfiguration.allowRedis = false;
		CoreConfiguration.allowRestrictedActions = false;
		CoreConfiguration.allowSQL = false;
		CoreConfiguration.allowStrangeFileSaves = false;
		CoreConfiguration.allowWebget = false;

		File scriptsFolder = DenizenCore.implementation.getScriptFolder();
		if (!scriptsFolder.exists()) {
			Debug.log("Creating scripts folder at " + scriptsFolder);
			scriptsFolder.mkdir();
		}

		ClientTickEvents.START.register((event) -> {
			DenizenCore.tick(50);
		});

		// Clear scripts received from the server on disconnect
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			ScriptHelper.additionalScripts.clear();
			DenizenCore.reloadScripts();
		});
	}
}
