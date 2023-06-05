package com.denizenscript.clientizen.scripts.commands;

import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.scripts.commands.generator.ArgLinear;
import com.denizenscript.denizencore.scripts.commands.generator.ArgName;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class NarrateCommand extends AbstractCommand {

	// <--[command]
	// @Name Narrate
	// @Syntax narrate [<text>]
	// @Required 1
	// @Maximum 1
	// Prints some text into the client's chat area.
	//
	// @Description
	// Prints some text into the client's chat area.
	//
	// @Tags
	// None
	//
	// @Usage
	// # Use to narrate text.
	// - narrate "Hello World!"
	// -->

    public NarrateCommand() {
        setName("narrate");
        setSyntax("narrate [<text>]");
        setRequiredArguments(1, 1);
        autoCompile();
    }

    public static void autoExecute(ScriptEntry scriptEntry,
                                   @ArgLinear @ArgName("text") String text) {
        MinecraftClient.getInstance().player.sendMessage(Text.of(text), false);
    }
}
