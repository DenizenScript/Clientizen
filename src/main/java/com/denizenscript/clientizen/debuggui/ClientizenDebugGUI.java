package com.denizenscript.clientizen.debuggui;

import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import io.github.cottonmc.cotton.gui.widget.WText;
import io.github.cottonmc.cotton.gui.widget.data.Color;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ClientizenDebugGUI extends LightweightGuiDescription {

	public ClientizenDebugGUI() {
		// current size of the screen
		int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
		int height = MinecraftClient.getInstance().getWindow().getScaledHeight();
		// the main panel to hold the entire GUI
		WPlainPanel panel = new WPlainPanel();
		panel.setSize(width, height);
		setRootPanel(panel);
		// panel to hold the console
		WPlainPanel consolePanel = new WPlainPanel();
		consolePanel.setSize(width - 15, width - 15);
		// the console text area
		WText debugConsole = new WText(Text.literal(String.join("\n", ClientizenDebugScreen.debug)));
		consolePanel.add(debugConsole, 0, 0, width - 15, height - 15);
		// scroll panel for the console
		WScrollPanel scrollPanel = new WScrollPanel(consolePanel);
		scrollPanel.setScrollingHorizontally(TriState.FALSE);
		scrollPanel.setScrollingVertically(TriState.TRUE);
		panel.add(scrollPanel, 5, 5, width - 10, height - 10);

		panel.validate(this);
	}

	@Override
	public void addPainters() {
		getRootPanel().setBackgroundPainter(BackgroundPainter.createColorful(Color.rgb(256, 0, 0,0).toRgb()));
	}
}
