package com.denizenscript.clientizen.scripts.commands;

import com.denizenscript.denizencore.DenizenCore;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;

public class ClientizenCommandRegistry {

    public static void registerCommands() {
        registerCommand(GuiCommand.class);
        registerCommand(NarrateCommand.class);
        registerCommand(ParticleCommand.class);
        registerCommand(ServerEventCommand.class);
        registerCommand(Render2DCommand.class);
    }

    private static void registerCommand(Class<? extends AbstractCommand> command) {
        DenizenCore.commandRegistry.registerCommand(command);
    }
}
