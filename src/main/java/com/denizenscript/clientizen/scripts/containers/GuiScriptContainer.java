package com.denizenscript.clientizen.scripts.containers;

import com.denizenscript.denizencore.scripts.containers.ScriptContainer;
import com.denizenscript.denizencore.utilities.YamlConfiguration;

public class GuiScriptContainer extends ScriptContainer {

	public String buttonName;

	public GuiScriptContainer(YamlConfiguration configurationSection, String scriptContainerName) {
		super(configurationSection, scriptContainerName);
		buttonName = configurationSection.get("button name").toString();
	}
}
