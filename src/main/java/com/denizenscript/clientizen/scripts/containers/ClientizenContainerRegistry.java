package com.denizenscript.clientizen.scripts.containers;

import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer;
import com.denizenscript.denizencore.scripts.ScriptRegistry;

public class ClientizenContainerRegistry {

    public static void registerContainers() {
        ScriptRegistry._registerType("gui", GuiScriptContainer.class);
        ScriptRegistry._registerType("particle", ParticleScriptContainer.class);
    }
}
