package com.denizenscript.clientizen.scripts.containers.gui;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WPanel;

public class GuiScriptGuiDescription extends LightweightGuiDescription {
    public GuiScriptGuiDescription(WPanel rootPanel) {
        setRootPanel(rootPanel);
        rootPanel.validate(this);
    }
}
