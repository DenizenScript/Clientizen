package com.denizenscript.clientizen.scripts.commands;

import com.denizenscript.clientizen.objects.EntityTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.scripts.commands.generator.ArgLinear;
import com.denizenscript.denizencore.scripts.commands.generator.ArgName;
import com.denizenscript.denizencore.scripts.commands.generator.ArgPrefixed;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AttachCommand extends AbstractCommand {

	public static Map<UUID, EntityTag> attachedEntities = new HashMap<>();

	public AttachCommand() {
		setName("attach");
		setSyntax("attach [<entity>|...] [to:<entity>] (cancel)");
		setRequiredArguments(2, 2);
		isProcedural = false;
		autoCompile();
	}

	public static void autoExecute(ScriptEntry scriptEntry,
								   @ArgLinear @ArgName("entities") List<EntityTag> attachingEntities,
								   @ArgPrefixed @ArgName("to") EntityTag entity,
								   @ArgName("cancel") boolean cancel) {
		for (EntityTag attachingEntity : attachingEntities) {
			if (cancel) {
				attachedEntities.remove(attachingEntity.uuid);
			}
			else {
				attachedEntities.put(attachingEntity.uuid, entity);
			}
		}
	}
}
