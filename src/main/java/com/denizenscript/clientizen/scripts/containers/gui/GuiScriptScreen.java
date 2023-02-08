package com.denizenscript.clientizen.scripts.containers.gui;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class GuiScriptScreen extends CottonClientScreen {

	public GuiScriptScreen(GuiDescription description) {
		super(description);
	}

	public static class Gui extends LightweightGuiDescription {

		public Gui(GuiScriptContainer gui) {
			System.out.println("I am creating the screen");
			WGridPanel root = new WGridPanel();
			setRootPanel(root);
			root.setSize(256, 240);
			root.setInsets(Insets.ROOT_PANEL);

			WButton button = new WButton(Text.literal(gui.buttonName)).setOnClick(() -> {
				MinecraftClient.getInstance().player.sendMessage(Text.literal("hello :)"));
			});
			root.add(button, 0, 3, 4, 1);

			root.validate(this);
		}
	}
}
