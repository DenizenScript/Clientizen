package com.denizenscript.clientizen;

import com.denizenscript.clientizen.events.ClientizenScriptEventRegistry;
import com.denizenscript.clientizen.network.NetworkManager;
import com.denizenscript.clientizen.objects.ClientizenObjectRegistry;
import com.denizenscript.clientizen.scripts.commands.ClientizenCommandRegistry;
import com.denizenscript.clientizen.scripts.containers.ClientizenContainerRegistry;
import com.denizenscript.clientizen.tags.ClientizenTagContext;
import com.denizenscript.clientizen.tags.ClientizenTagRegistry;
import com.denizenscript.clientizen.util.impl.DenizenCoreImpl;
import com.denizenscript.denizencore.DenizenCore;
import com.denizenscript.denizencore.DenizenImplementation;
import com.denizenscript.denizencore.scripts.ScriptHelper;
import com.denizenscript.denizencore.utilities.CoreConfiguration;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Clientizen implements ClientModInitializer {

	public static final String ID = "clientizen";

	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static Identifier id(String path) {
		return new Identifier(ID, path);
	}

	public static String version;

	public DenizenImplementation coreImplementation = new DenizenCoreImpl();

	@Override
	public void onInitializeClient() {
		initCore();
		version = FabricLoader.getInstance().getModContainer(ID).get().getMetadata().getVersion().toString();
		Debug.log("Clientizen", "Initializing Clientizen v" + version);
		NetworkManager.instance = new NetworkManager();
		registerAll();
		applyConfig();
		checkScriptsFolder();
		ClientTickEvents.START_CLIENT_TICK.register(client -> DenizenCore.tick(50));
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			ScriptHelper.additionalScripts.clear();
			DenizenCore.reloadScripts();
		});
	}

	public void initCore() {
		CoreUtilities.noDebugContext = new ClientizenTagContext(false, null, null);
		CoreUtilities.noDebugContext.showErrors = () -> false;
		CoreUtilities.basicContext = new ClientizenTagContext(true, null, null);
		CoreUtilities.errorButNoDebugContext = new ClientizenTagContext(false, null, null);
		DenizenCore.init(coreImplementation);
	}

	public static void registerAll() {
		ClientizenCommandRegistry.registerCommands();
		ClientizenContainerRegistry.registerContainers();
		ClientizenScriptEventRegistry.registerEvents();
		ClientizenObjectRegistry.registerObjects();
		ClientizenTagRegistry.registerTagHandlers();
	}

	public static void applyConfig() {
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
	}

	public static void checkScriptsFolder() {
		File scriptsFolder = DenizenCore.implementation.getScriptFolder();
		if (!scriptsFolder.exists()) {
			Debug.log("Creating scripts folder at " + scriptsFolder);
			scriptsFolder.mkdir();
		}
	}
}
