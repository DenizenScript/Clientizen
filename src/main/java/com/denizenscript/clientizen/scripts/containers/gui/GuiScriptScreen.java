package com.denizenscript.clientizen.scripts.containers.gui;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;

public class GuiScriptScreen extends CottonClientScreen {

    GuiScriptContainer script;

    public GuiScriptScreen(GuiDescription description, GuiScriptContainer script) {
        super(description);
        this.script = script;
    }

    public GuiScriptContainer getScript() {
        return script;
    }
}
