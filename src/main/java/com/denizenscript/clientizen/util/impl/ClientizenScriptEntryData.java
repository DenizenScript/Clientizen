package com.denizenscript.clientizen.util.impl;

import com.denizenscript.clientizen.tags.ClientizenTagContext;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.YamlConfiguration;

public class ClientizenScriptEntryData extends ScriptEntryData {

	@Override
	public void transferDataFrom(ScriptEntryData scriptEntryData) {
		if (scriptEntryData == null) {
			return;
		}
		scriptEntry = scriptEntryData.scriptEntry;
	}

	@Override
	public TagContext getTagContext() {
		return new ClientizenTagContext(scriptEntry);
	}

	@Override
	public YamlConfiguration save() {
		return new YamlConfiguration();
	}

	@Override
	public void load(YamlConfiguration yamlConfiguration) {
		// Do nothing
	}
}
