package com.denizenscript.clientizen.util.impl;

import com.denizenscript.clientizen.Clientizen;
import com.denizenscript.clientizen.tags.ClientizenTagContext;
import com.denizenscript.clientizen.util.Utilities;
import com.denizenscript.clientizen.util.debugging.Debug;
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
import com.denizenscript.denizencore.utilities.debugging.Debuggable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Formatting;

import java.io.File;
import java.util.function.Consumer;

import static com.denizenscript.denizencore.utilities.debugging.Debug.DebugElement;

public class DenizenCoreImpl implements DenizenImplementation {

	@Override
	public File getScriptFolder() {
		return new File(MinecraftClient.getInstance().runDirectory + File.separator + "clientizen/scripts");
	}

	@Override
	public String getImplementationVersion() {
		return Clientizen.version;
	}

	@Override
	public void debugMessage(String s) {
		Debug.log(s);
	}

	@Override
	public void debugMessage(String s, String s1) {
		Debug.log(s, s1);
	}

	@Override
	public void debugException(Throwable throwable) {
		Debug.echoError(throwable);
	}

	@Override
	public void debugError(String s, String s1) {
		Debug.echoError(null, s, s1, true);
	}

	@Override
	public void debugError(ScriptEntry scriptEntry, String s, String s1) {
		Debug.echoError(scriptEntry, s, s1, true);
	}

	@Override
	public void debugError(ScriptEntry scriptEntry, Throwable throwable) {
		Debug.echoError(scriptEntry, throwable);
	}

	@Override
	public void debugReport(Debuggable debuggable, String s, String s1) {
		Debug.report(debuggable, s, s1);
	}

	@Override
	public void debugReport(Debuggable debuggable, String s, Object... objects) {
		Debug.report(debuggable, s, objects);
	}

	@Override
	public void debugApproval(String s) {
		Debug.echoApproval(s);
	}

	@Override
	public void debugEntry(Debuggable debuggable, String s) {
		Debug.echoDebug(debuggable, s);
	}

	@Override
	public void debugEntry(Debuggable debuggable, DebugElement debugElement, String s) {
		Debug.echoDebug(debuggable, debugElement, s);
	}

	@Override
	public void debugEntry(Debuggable debuggable, DebugElement debugElement) {
		Debug.echoDebug(debuggable, debugElement);
	}

	@Override
	public String getImplementationName() {
		return "Client";
	}

	@Override
	public void preScriptReload() {

	}

	@Override
	public void onScriptReload() {

	}

	@Override
	public ScriptEntryData getEmptyScriptEntryData() {
		return new ClientizenScriptEntryData();
	}

	@Override
	public boolean handleCustomArgs(ScriptEntry scriptEntry, Argument argument, boolean b) {
		return false;
	}

	@Override
	public void refreshScriptContainers() {

	}

	@Override
	public TagContext getTagContext(ScriptContainer scriptContainer) {
		return new ClientizenTagContext(scriptContainer);
	}

	@Override
	public TagContext getTagContext(ScriptEntry scriptEntry) {
		return new ClientizenTagContext(scriptEntry);
	}

	@Override
	public String cleanseLogString(String input) {
		char esc_char = (char) 0x1b;
		String esc = String.valueOf(esc_char);
		String repc = String.valueOf(Formatting.field_33292);
		if (input.indexOf(esc_char) != -1) {
			input = CoreUtilities.replace(input, esc + "[0;30;22m", repc + "0");
			input = CoreUtilities.replace(input, esc + "[0;34;22m", repc + "1");
			input = CoreUtilities.replace(input, esc + "[0;32;22m", repc + "2");
			input = CoreUtilities.replace(input, esc + "[0;36;22m", repc + "3");
			input = CoreUtilities.replace(input, esc + "[0;31;22m", repc + "4");
			input = CoreUtilities.replace(input, esc + "[0;35;22m", repc + "5");
			input = CoreUtilities.replace(input, esc + "[0;33;22m", repc + "6");
			input = CoreUtilities.replace(input, esc + "[0;37;22m", repc + "7");
			input = CoreUtilities.replace(input, esc + "[0;30;1m", repc + "8");
			input = CoreUtilities.replace(input, esc + "[0;34;1m", repc + "9");
			input = CoreUtilities.replace(input, esc + "[0;32;1m", repc + "a");
			input = CoreUtilities.replace(input, esc + "[0;36;1m", repc + "b");
			input = CoreUtilities.replace(input, esc + "[0;31;1m", repc + "c");
			input = CoreUtilities.replace(input, esc + "[0;35;1m", repc + "d");
			input = CoreUtilities.replace(input, esc + "[0;33;1m", repc + "e");
			input = CoreUtilities.replace(input, esc + "[0;37;1m", repc + "f");
			input = CoreUtilities.replace(input, esc + "[5m", repc + "k");
			input = CoreUtilities.replace(input, esc + "[21m", repc + "l");
			input = CoreUtilities.replace(input, esc + "[9m", repc + "m");
			input = CoreUtilities.replace(input, esc + "[4m", repc + "n");
			input = CoreUtilities.replace(input, esc + "[3m", repc + "o");
			input = CoreUtilities.replace(input, esc + "[m", repc + "r");
		}
		return input;
	}

	public static Thread tagThread = null;

	@Override
	public boolean isSafeThread() {
		return MinecraftClient.getInstance().isOnThread() && Thread.currentThread().equals(tagThread);
	}

	@Override
	public void preTagExecute() {
		tagThread = Thread.currentThread();
	}

	@Override
	public void postTagExecute() {
		tagThread = null;
	}

	@Override
	public boolean needsHandleArgPrefix(String s) {
		return false;
	}

	@Override
	public boolean shouldDebug(Debuggable debuggable) {
		return debuggable.shouldDebug();
	}

	@Override
	public void debugQueueExecute(ScriptEntry scriptEntry, String s, String s1) {

	}

	@Override
	public boolean canWriteToFile(File file) {
		return Utilities.canWriteToFile(file);
	}

	public static Formatting[] DEBUG_FRIENDLY_COLORS = new Formatting[] {
			Formatting.AQUA, Formatting.BLUE, Formatting.DARK_AQUA, Formatting.DARK_BLUE, Formatting.DARK_GREEN,
			Formatting.DARK_PURPLE, Formatting.GOLD, Formatting.GRAY, Formatting.GREEN,
			Formatting.LIGHT_PURPLE, Formatting.WHITE, Formatting.YELLOW
	};

	@Override
	public String getRandomColor() {
		return DEBUG_FRIENDLY_COLORS[CoreUtilities.getRandom().nextInt(DEBUG_FRIENDLY_COLORS.length)].toString();
	}

	@Override
	public boolean canReadFile(File file) {
		return false;
	}

	@Override
	public File getDataFolder() {
		return null;
	}

	@Override
	public String queueHeaderInfo(ScriptEntry scriptEntry) {
		return null;
	}

	@Override
	public void startRecording() {

	}

	@Override
	public void stopRecording() {

	}

	@Override
	public void submitRecording(Consumer<String> consumer) {

	}

	@Override
	public FlaggableObject simpleWordToFlaggable(String s, ScriptEntry scriptEntry) {
		return null;
	}

	@Override
	public ObjectTag getSpecialDef(String s, ScriptQueue scriptQueue) {
		return null;
	}

	@Override
	public boolean setSpecialDef(String s, ScriptQueue scriptQueue, ObjectTag objectTag) {
		return false;
	}

	@Override
	public String getTextColor() {
		return Formatting.WHITE.toString();
	}

	@Override
	public String getEmphasisColor() {
		return Formatting.AQUA.toString();
	}

	@Override
	public void saveClassToLoader(Class<?> clazz) {
		return;
	}
}
