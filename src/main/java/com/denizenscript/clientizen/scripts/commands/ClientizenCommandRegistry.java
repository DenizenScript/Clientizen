package com.denizenscript.clientizen.scripts.commands;

import com.denizenscript.denizencore.DenizenCore;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;

public class ClientizenCommandRegistry {

	public static void registerCommands() {
		registerCommand(NarrateCommand.class);
		registerCommand(GuiCommand.class);
		registerCommand(ServerEventCommand.class);
	}

	private static void registerCommand(Class<? extends AbstractCommand> command) {
		DenizenCore.commandRegistry.registerCommand(command);
	}
}
