package com.denizenscript.clientizen;

import com.denizenscript.clientizen.debuggui.ClientizenDebugScreen;
import com.denizenscript.clientizen.events.ClientizenScriptEventRegistry;
import com.denizenscript.clientizen.network.NetworkManager;
import com.denizenscript.clientizen.objects.ClientizenObjectRegistry;
import com.denizenscript.clientizen.objects.properties.PropertyRegistry;
import com.denizenscript.clientizen.scripts.commands.ClientizenCommandRegistry;
import com.denizenscript.clientizen.scripts.containers.ClientizenContainerRegistry;
import com.denizenscript.clientizen.tags.ClientizenTagContext;
import com.denizenscript.clientizen.tags.ClientizenTagRegistry;
import com.denizenscript.clientizen.util.CexCommand;
import com.denizenscript.clientizen.util.impl.DenizenCoreImpl;
import com.denizenscript.denizencore.DenizenCore;
import com.denizenscript.denizencore.DenizenImplementation;
import com.denizenscript.denizencore.scripts.ScriptHelper;
import com.denizenscript.denizencore.utilities.CoreConfiguration;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.denizenscript.denizencore.utilities.ExCommandHelper;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

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
        // Note: intentionally before initializing Denizen-Core as it reads the implementation version
        version = FabricLoader.getInstance().getModContainer(ID).get().getMetadata().getVersion().toString();
        LOGGER.info("Initializing Clientizen v" + version);

        // Initialize Denizen-Core
        CoreUtilities.noDebugContext = new ClientizenTagContext(false, null, null);
        CoreUtilities.noDebugContext.showErrors = () -> false;
        CoreUtilities.basicContext = new ClientizenTagContext(true, null, null);
        CoreUtilities.errorButNoDebugContext = new ClientizenTagContext(false, null, null);
        DenizenCore.init(coreImplementation);
        DenizenCore.reloadSaves();

        // Configure Denizen-Core
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

        // Register commands, script containers, events, objects, and tag handlers
        ClientizenCommandRegistry.registerCommands();
        ClientizenContainerRegistry.registerContainers();
        ClientizenScriptEventRegistry.registerEvents();
        ClientizenObjectRegistry.registerObjects();
        ClientizenTagRegistry.registerTagHandlers();
        PropertyRegistry.register();

        // Initialize Clientizen systems
        NetworkManager.init();
        ClientizenDebugScreen.register();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> CexCommand.register(dispatcher));
        // Check for the client scripts folder
        File scriptsFolder = DenizenCore.implementation.getScriptFolder();
        if (!scriptsFolder.exists()) {
            Debug.log("Creating scripts folder at " + scriptsFolder);
            scriptsFolder.mkdirs();
        }

        // Load all scripts in
        DenizenCore.reloadScripts(false, null);

        // Tick Denizen-Core
        ClientTickEvents.START_CLIENT_TICK.register(client -> DenizenCore.tick(50));

        // Shutdown Denizen-Core when the client is stopping
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> DenizenCore.shutdown());

        // Remove scripts received from the server once the client disconnects from it
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            ScriptHelper.buildAdditionalScripts.clear();
            DenizenCore.reloadScripts(false, null);
        });
    }
}
