package com.denizenscript.clientizen.scripts;

import com.denizenscript.clientizen.network.Channels;
import com.denizenscript.clientizen.network.DataDeserializer;
import com.denizenscript.clientizen.network.NetworkManager;
import com.denizenscript.denizencore.DenizenCore;
import com.denizenscript.denizencore.objects.ObjectFetcher;
import com.denizenscript.denizencore.objects.core.ScriptTag;
import com.denizenscript.denizencore.scripts.ScriptHelper;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.CoreConfiguration;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.denizenscript.denizencore.utilities.ScriptUtilities;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;

import java.util.Map;

public class ClientScriptHelper {

	public static void init() {
		NetworkManager.registerInChannel(Channels.SET_SCRIPTS, ClientScriptHelper::loadScriptsFrom);
		NetworkManager.registerInChannel(Channels.RUN_SCRIPT, ClientScriptHelper::handleScriptRun);
	}

	public static void loadScriptsFrom(DataDeserializer data) {
		Map<String, String> scriptsMap = data.readStringMap();
		DenizenCore.runOnMainThread(() -> {
			ScriptHelper.buildAdditionalScripts.clear();
			for (Map.Entry<String, String> entry : scriptsMap.entrySet()) {
				ScriptHelper.buildAdditionalScripts.add(scripts -> scripts.add(YamlConfiguration.load(ScriptHelper.clearComments(entry.getKey(), entry.getValue(), true))));
			}
			DenizenCore.reloadScripts(true, null);
		});
	}

	public static void handleScriptRun(DataDeserializer data) {
		String scriptName = data.readString();
		String path = data.readString();
		Map<String, String> defMap = data.readStringMap();
		DenizenCore.runOnMainThread(() -> {
			ScriptTag script = ScriptTag.valueOf(scriptName, CoreUtilities.noDebugContext);
			if (script == null) {
				if (CoreConfiguration.debugExtraInfo) {
					Debug.echoError("Invalid script name to run received from server: " + scriptName + ".");
				}
				return;
			}
			ScriptUtilities.createAndStartQueue(script.getContainer(), path.isEmpty() ? null : path, null, null, queue -> {
				TagContext context = DenizenCore.implementation.getTagContext(script.getContainer());
				for (Map.Entry<String, String> entry : defMap.entrySet()) {
					queue.addDefinition(entry.getKey(), ObjectFetcher.pickObjectFor(entry.getValue(), context));
				}
			}, null, "SERVER_RUN:" + script.getContainer().getName(), null, null);
		});
	}
}