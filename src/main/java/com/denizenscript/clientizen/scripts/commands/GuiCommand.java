package com.denizenscript.clientizen.scripts.commands;

import com.denizenscript.clientizen.scripts.containers.GuiScriptContainer;
import com.denizenscript.denizencore.objects.core.ScriptTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;

public class GuiCommand extends AbstractCommand {

	public GuiCommand() {
		setName("gui");
		setSyntax("gui [<script>]");
		setRequiredArguments(1, 1);
		setRawValuesHandled("script");
	}

	@Override
	public void execute(ScriptEntry scriptEntry) {
		ScriptTag script = scriptEntry.getElement("script").asType(ScriptTag.class, scriptEntry.context);
		if (script.getContainer() instanceof GuiScriptContainer gui) {
			SpruceScreen screen = new SpruceScreen(new LiteralText("Test GUI screen")) {

				@Override
				protected void init() {
					super.init();
					addDrawableChild(new SpruceButtonWidget(Position.center(width, height), 150, 20, new LiteralText(gui.buttonName), btn -> {
						client.player.sendChatMessage("hello");
					}));
				}
			};
			screen.init(MinecraftClient.getInstance(), 500, 500);
		}
	}
}
