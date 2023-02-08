package com.denizenscript.clientizen.scripts.commands;

import com.denizenscript.clientizen.debuggui.ClientizenDebugGUI;
import com.denizenscript.clientizen.debuggui.ClientizenDebugScreen;
import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer;
import com.denizenscript.denizencore.objects.core.ScriptTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.scripts.commands.Holdable;
import com.denizenscript.denizencore.scripts.commands.generator.ArgLinear;
import com.denizenscript.denizencore.scripts.commands.generator.ArgName;
import net.minecraft.client.MinecraftClient;

public class GuiCommand extends AbstractCommand implements Holdable {

    public GuiCommand() {
        setName("gui");
        setSyntax("gui [<script>]");
        setRequiredArguments(1, 1);
        autoCompile();
    }

	public static void autoExecute(ScriptEntry scriptEntry,
								   @ArgLinear @ArgName("script") ScriptTag script) {
        if (script.getContainer() instanceof GuiScriptContainer gui) {
			//MinecraftClient.getInstance().setScreen(new GuiScriptScreen(new GuiScriptScreen.Gui(gui)));
			MinecraftClient.getInstance().setScreen(new ClientizenDebugScreen(new ClientizenDebugGUI()));
			System.out.println(MinecraftClient.getInstance().currentScreen); // shows debug screen!
        }
    }
}
