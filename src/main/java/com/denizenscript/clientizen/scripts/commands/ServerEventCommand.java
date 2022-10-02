package com.denizenscript.clientizen.scripts.commands;

import com.denizenscript.clientizen.network.Channels;
import com.denizenscript.clientizen.network.DataSerializer;
import com.denizenscript.clientizen.network.NetworkManager;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.MapTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.scripts.commands.generator.ArgDefaultNull;
import com.denizenscript.denizencore.scripts.commands.generator.ArgName;
import com.denizenscript.denizencore.scripts.commands.generator.ArgPrefixed;
import com.denizenscript.denizencore.utilities.text.StringHolder;

import java.util.HashMap;
import java.util.Map;

public class ServerEventCommand extends AbstractCommand {

	public ServerEventCommand() {
		setName("serverevent");
		setSyntax("serverevent [id:<id>] (data:<map>)");
		setRequiredArguments(1, 2);
		autoCompile();
	}

	public static void autoExecute(ScriptEntry scriptEntry,
								   @ArgPrefixed @ArgName("id") String id,
								   @ArgDefaultNull @ArgPrefixed @ArgName("data") MapTag dataInput) {
		Map<String, String> data = new HashMap<>();
		if (dataInput != null) {
			for (Map.Entry<StringHolder, ObjectTag> entry : dataInput.map.entrySet()) {
				data.put(entry.getKey().str, entry.getValue().identify());
			}
		}
		NetworkManager.send(Channels.FIRE_EVENT, new DataSerializer().writeString(id).writeStringMap(data));
	}
}
