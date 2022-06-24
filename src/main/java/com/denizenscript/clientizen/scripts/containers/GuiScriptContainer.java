package com.denizenscript.clientizen.scripts.containers;

import com.denizenscript.denizencore.scripts.containers.ScriptContainer;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.text.StringHolder;

public class GuiScriptContainer extends ScriptContainer {

	public String buttonName;

	public GuiScriptContainer(YamlConfiguration configurationSection, String scriptContainerName) {
		super(configurationSection, scriptContainerName);
		buttonName = ((StringHolder) configurationSection.get("button name")).str;
	}
}
