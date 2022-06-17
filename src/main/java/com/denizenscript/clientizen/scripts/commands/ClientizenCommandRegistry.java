package com.denizenscript.clientizen.scripts.commands;

import com.denizenscript.denizencore.scripts.commands.CommandRegistry;
import com.denizenscript.denizencore.scripts.commands.core.*;
import com.denizenscript.denizencore.scripts.commands.queue.*;

public class ClientizenCommandRegistry extends CommandRegistry {

	public static ClientizenCommandRegistry instance;

	public void registerCommands() {
		// Register core commands - not using registerCoreCommands() as we don't want all the core commands
		this.registerCommand(AdjustCommand.class);
		this.registerCommand(CustomEventCommand.class);
		this.registerCommand(DebugCommand.class);
		//this.registerCommand(EventCommand.class);
		this.registerCommand(FlagCommand.class);
		this.registerCommand(NoteCommand.class);
		this.registerCommand(ReloadCommand.class);
		//this.registerCommand(SQLCommand.class);
		//this.registerCommand(RedisCommand.class);
		this.registerCommand(WebGetCommand.class);
		//this.registerCommand(WebServerCommand.class);
		//this.registerCommand(FileCopyCommand.class);
		//this.registerCommand(FileReadCommand.class);
		//this.registerCommand(FileWriteCommand.class);
		//this.registerCommand(LogCommand.class);
		//this.registerCommand(YamlCommand.class);
		this.registerCommand(ChooseCommand.class);
		this.registerCommand(DefineCommand.class);
		this.registerCommand(DefineMapCommand.class);
		this.registerCommand(DetermineCommand.class);
		this.registerCommand(ElseCommand.class);
		this.registerCommand(ForeachCommand.class);
		this.registerCommand(GotoCommand.class);
		this.registerCommand(IfCommand.class);
		this.registerCommand(InjectCommand.class);
		this.registerCommand(MarkCommand.class);
		this.registerCommand(QueueCommand.class);
		this.registerCommand(RandomCommand.class);
		this.registerCommand(RateLimitCommand.class);
		this.registerCommand(RepeatCommand.class);
		this.registerCommand(RunCommand.class);
		this.registerCommand(RunLaterCommand.class);
		this.registerCommand(StopCommand.class);
		this.registerCommand(WaitCommand.class);
		this.registerCommand(WaitUntilCommand.class);
		this.registerCommand(WhileCommand.class);

		registerCommand(NarrateCommand.class);
	}
}
