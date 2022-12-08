package com.denizenscript.clientizen.util.impl;

import com.denizenscript.clientizen.Clientizen;
import com.denizenscript.clientizen.debuggui.ClientizenDebugScreen;
import com.denizenscript.clientizen.tags.ClientizenTagContext;
import com.denizenscript.denizencore.DenizenImplementation;
import com.denizenscript.denizencore.flags.FlaggableObject;
import com.denizenscript.denizencore.objects.Argument;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import com.denizenscript.denizencore.scripts.containers.ScriptContainer;
import com.denizenscript.denizencore.scripts.queues.ScriptQueue;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Formatting;

import java.io.File;
import java.util.Arrays;

public class DenizenCoreImpl implements DenizenImplementation {

	@Override
	public File getScriptFolder() {
		return new File(MinecraftClient.getInstance().runDirectory, "clientizen/scripts");
	}

	@Override
	public String getImplementationVersion() {
		return Clientizen.version;
	}

	@Override
	public String getImplementationName() {
		return "Client";
	}

	@Override
	public void preScriptReload() {}

	@Override
	public void onScriptReload() {}

	@Override
	public String queueHeaderInfo(ScriptEntry scriptEntry) {
		return "";
	}

	@Override
	public boolean needsHandleArgPrefix(String prefix) {
		return false;
	}

	@Override
	public boolean handleCustomArgs(ScriptEntry scriptEntry, Argument argument) {
		return false;
	}

	@Override
	public void refreshScriptContainers() {}

	@Override
	public TagContext getTagContext(ScriptContainer scriptContainer) {
		return new ClientizenTagContext(scriptContainer);
	}

	@Override
	public TagContext getTagContext(ScriptEntry scriptEntry) {
		return new ClientizenTagContext(scriptEntry);
	}

	@Override
	public ScriptEntryData getEmptyScriptEntryData() {
		return new ClientizenScriptEntryData();
	}

	@Override
	public String cleanseLogString(String input) {
		return input;
//		char esc_char = (char) 0x1b;
//		String esc = String.valueOf(esc_char);
//		String repc = String.valueOf(Formatting.);
//		if (input.indexOf(esc_char) != -1) {
//			input = CoreUtilities.replace(input, esc + "[0;30;22m", repc + "0");
//			input = CoreUtilities.replace(input, esc + "[0;34;22m", repc + "1");
//			input = CoreUtilities.replace(input, esc + "[0;32;22m", repc + "2");
//			input = CoreUtilities.replace(input, esc + "[0;36;22m", repc + "3");
//			input = CoreUtilities.replace(input, esc + "[0;31;22m", repc + "4");
//			input = CoreUtilities.replace(input, esc + "[0;35;22m", repc + "5");
//			input = CoreUtilities.replace(input, esc + "[0;33;22m", repc + "6");
//			input = CoreUtilities.replace(input, esc + "[0;37;22m", repc + "7");
//			input = CoreUtilities.replace(input, esc + "[0;30;1m", repc + "8");
//			input = CoreUtilities.replace(input, esc + "[0;34;1m", repc + "9");
//			input = CoreUtilities.replace(input, esc + "[0;32;1m", repc + "a");
//			input = CoreUtilities.replace(input, esc + "[0;36;1m", repc + "b");
//			input = CoreUtilities.replace(input, esc + "[0;31;1m", repc + "c");
//			input = CoreUtilities.replace(input, esc + "[0;35;1m", repc + "d");
//			input = CoreUtilities.replace(input, esc + "[0;33;1m", repc + "e");
//			input = CoreUtilities.replace(input, esc + "[0;37;1m", repc + "f");
//			input = CoreUtilities.replace(input, esc + "[5m", repc + "k");
//			input = CoreUtilities.replace(input, esc + "[21m", repc + "l");
//			input = CoreUtilities.replace(input, esc + "[9m", repc + "m");
//			input = CoreUtilities.replace(input, esc + "[4m", repc + "n");
//			input = CoreUtilities.replace(input, esc + "[3m", repc + "o");
//			input = CoreUtilities.replace(input, esc + "[m", repc + "r");
//		}
//		return input;
	}

	@Override
	public void preTagExecute() {}

	@Override
	public void postTagExecute() {}

	@Override
	public boolean canWriteToFile(File file) {
		return false;
	}

	private static final Formatting[] colors = Arrays.stream(Formatting.values()).filter(Formatting::isColor).toArray(Formatting[]::new);

	@Override
	public String getRandomColor() {
		return colors[CoreUtilities.getRandom().nextInt(colors.length)].toString();
	}

	@Override
	public boolean canReadFile(File file) {
		return false;
	}

	@Override
	public File getDataFolder() {
		return new File(MinecraftClient.getInstance().runDirectory, "clientizen/data");
	}

	@Override
	public FlaggableObject simpleWordToFlaggable(String word, ScriptEntry scriptEntry) {
		return null;
	}

	@Override
	public ObjectTag getSpecialDef(String def, ScriptQueue scriptQueue) {
		return null;
	}

	@Override
	public boolean setSpecialDef(String def, ScriptQueue scriptQueue, ObjectTag objectTag) {
		return false;
	}

	@Override
	public void addExtraErrorHeaders(StringBuilder stringBuilder, ScriptEntry scriptEntry) {}

	@Override
	public String applyDebugColors(String uncolored) {
		if (!CoreUtilities.contains(uncolored, '<') || !CoreUtilities.contains(uncolored, '>')) {
			return uncolored;
		}
		return uncolored
				.replace("<Y>", Formatting.YELLOW.toString())
				.replace("<O>", Formatting.GOLD.toString())
				.replace("<G>", Formatting.DARK_GRAY.toString())
				.replace("<LG>", Formatting.GRAY.toString())
				.replace("<GR>", Formatting.GREEN.toString())
				.replace("<A>", Formatting.AQUA.toString())
				.replace("<R>", Formatting.DARK_RED.toString())
				.replace("<LR>", Formatting.RED.toString())
				.replace("<LP>", Formatting.LIGHT_PURPLE.toString())
				.replace("<W>", Formatting.WHITE.toString());
	}

	@Override
	public void doFinalDebugOutput(String text) {
		text = text.replace("<FORCE_ALIGN>", "");
		Clientizen.LOGGER.info(Formatting.strip(text));
		ClientizenDebugScreen.debug.add(text);
	}

	@Override
	public String stripColor(String text) {
		return Formatting.strip(text);
	}
}
