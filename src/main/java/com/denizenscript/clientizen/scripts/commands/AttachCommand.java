package com.denizenscript.clientizen.scripts.commands;

import com.denizenscript.clientizen.objects.EntityTag;
import com.denizenscript.denizencore.exceptions.InvalidArgumentsRuntimeException;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.scripts.commands.generator.ArgDefaultNull;
import com.denizenscript.denizencore.scripts.commands.generator.ArgLinear;
import com.denizenscript.denizencore.scripts.commands.generator.ArgName;
import com.denizenscript.denizencore.scripts.commands.generator.ArgPrefixed;

import java.util.*;

public class AttachCommand extends AbstractCommand {

	public record AttachData(boolean noShadow, boolean noAnimation) {}

	public static Map<UUID, List<EntityTag>> attachMap = new HashMap<>();
	public static Map<UUID, AttachData> attachedEntities = new HashMap<>();

	public AttachCommand() {
		setName("attach");
		setSyntax("attach [<entity>|...] [to:<entity>] (cancel) (no_shadow) (no_animation)");
		setRequiredArguments(2, 5);
		isProcedural = false;
		autoCompile();
	}

	public static void autoExecute(ScriptEntry scriptEntry,
								   @ArgLinear @ArgName("entities") ListTag attachingEntities,
								   @ArgDefaultNull @ArgPrefixed @ArgName("to") EntityTag toEntity,
								   @ArgName("cancel") boolean cancel,
								   @ArgName("no_shadow") boolean noShadow,
								   @ArgName("no_animation") boolean noAnimation) {
		if (!cancel && toEntity == null) {
			throw new InvalidArgumentsRuntimeException("Must specify an entity to attach to");
		}
		List<EntityTag> attaching = attachingEntities.filter(EntityTag.class, scriptEntry.context);
		AttachData attachData = new AttachData(noShadow, noAnimation);
		if (attachMap.containsKey(toEntity.uuid)) {
			attachMap.get(toEntity.uuid).addAll(attaching);
		}
		else {
			attachMap.put(toEntity.uuid, attaching);
		}
		for (EntityTag entityTag : attaching) {
			attachedEntities.put(entityTag.uuid, attachData);
		}
	}
}
