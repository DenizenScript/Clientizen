package com.denizenscript.clientizen.scripts.commands;

import com.denizenscript.clientizen.util.debugging.Debug;
import com.denizenscript.denizencore.exceptions.InvalidArgumentsException;
import com.denizenscript.denizencore.objects.Argument;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class NarrateCommand extends AbstractCommand {

	public NarrateCommand() {
		setName("narrate");
		setSyntax("narrate [<text>]");
		setRequiredArguments(1, 1);
	}

	@Override
	public void parseArgs(ScriptEntry scriptEntry) throws InvalidArgumentsException {
		for (Argument arg : scriptEntry) {
			if (!scriptEntry.hasObject("text")) {
				scriptEntry.addObject("text", arg.asElement());
			}
		}
		if (!scriptEntry.hasObject("text")) {
			throw new InvalidArgumentsException("Must specify text!");
		}
	}

	@Override
	public void execute(ScriptEntry scriptEntry) {
		ElementTag text = scriptEntry.getElement("text");
		if (scriptEntry.dbCallShouldDebug()) {
			Debug.report(scriptEntry, getName(), db("Narrating", text));
		}
		MinecraftClient.getInstance().player.sendMessage(Text.literal(text.asString()), false);
	}
}
