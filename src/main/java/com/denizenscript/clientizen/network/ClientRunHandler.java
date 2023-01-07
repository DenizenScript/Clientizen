package com.denizenscript.clientizen.network;

import com.denizenscript.clientizen.util.impl.ClientizenScriptEntryData;
import com.denizenscript.denizencore.DenizenCore;
import com.denizenscript.denizencore.objects.ObjectFetcher;
import com.denizenscript.denizencore.objects.core.MapTag;
import com.denizenscript.denizencore.objects.core.ScriptTag;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.CoreConfiguration;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.denizenscript.denizencore.utilities.ScriptUtilities;
import com.denizenscript.denizencore.utilities.debugging.Debug;

import java.util.Map;

public class ClientRunHandler {

	public static void init() {
		NetworkManager.registerInChannel(Channels.RUN_SCRIPT, ClientRunHandler::handleScriptRun);
	}

	public static void handleScriptRun(DataDeserializer data) {
		String scriptName = data.readString();
		ScriptTag script = ScriptTag.valueOf(scriptName, CoreUtilities.noDebugContext);
		if (script == null) {
			if (CoreConfiguration.debugExtraInfo) {
				Debug.echoError("Invalid script name to run received from server: " + scriptName + ".");
			}
			return;
		}
		String path = data.readString();
		ScriptUtilities.createAndStartQueue(script.getContainer(), path.isEmpty() ? null : path, null, null, queue -> {
			TagContext context = DenizenCore.implementation.getTagContext(script.getContainer());
			for (Map.Entry<String, String> entry : data.readStringMap().entrySet()) {
				queue.addDefinition(entry.getKey(), ObjectFetcher.pickObjectFor(entry.getValue(), context));
			}
		}, null, null, null, null);
	}
}
