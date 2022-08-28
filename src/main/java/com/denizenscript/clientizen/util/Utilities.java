package com.denizenscript.clientizen.util;

import com.denizenscript.clientizen.util.debugging.Debug;
import com.denizenscript.denizencore.DenizenCore;
import com.denizenscript.denizencore.utilities.CoreConfiguration;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

public class Utilities {

	public static boolean canWriteToFile(File file) {
		try {
			String lown = CoreUtilities.toLowerCase(file.getCanonicalPath()).replace('\\', '/');
			if (lown.endsWith("/")) {
				lown = lown.substring(0, lown.length() - 1);
			}
			if (CoreConfiguration.debugVerbose) {
				Debug.log("Checking file canWrite: " + lown);
			}
			if (!CoreConfiguration.allowStrangeFileSaves &&
					!file.getCanonicalPath().startsWith(new File(".").getCanonicalPath())) {
				return false;
			}
			if (false /* !CoreUtilities.toLowerCase(Settings.fileLimitPath()).equals("none")
					&& !file.getCanonicalPath().startsWith(new File("./" + Settings.fileLimitPath()).getCanonicalPath())*/) {
				return false;
			}
			return isFileCanonicalStringSafeToWrite(lown) && isFileCanonicalStringSafeToWrite(lown + "/");
		}
		catch (Exception ex) {
			Debug.echoError(ex);
			return false;
		}
	}


	/** File extensions to just outright forbid generating from scripts, to reduce potential routes for abuse. Most importantly, forbid creation of files that the minecraft server will execute. */
	public static HashSet<String> FORBIDDEN_EXTENSIONS = new HashSet<>(Arrays.asList(
			"jar", "java", // Java related files
			"sh", "bash", // Linux scripts
			"bat", "ps1", "vb", "vbs", "vbscript", "batch", "cmd", "com", "msc", "sct", "ws", "wsf", // Windows scripts
			"exe", "scr", "msi", "dll", "bin", // Windows executables
			"lnk", "reg", "rgs", // other weird Windows files
			"secret" // Protected by Denizen
	));

	public static boolean isFileCanonicalStringSafeToWrite(String lown) {
		/*if (lown.contains("clientizen/config.yml")) {
			return false;
		}*/
		if (lown.contains("clientizen/secrets.secret")) {
			return false;
		}
		if (lown.contains("clientizen/scripts/")) {
			return false;
		}
		int dot = lown.lastIndexOf('.');
		return dot == -1 || !FORBIDDEN_EXTENSIONS.contains(lown.substring(dot + 1));
	}

	public static void runOnRenderThread(Runnable runnable) {
		if (/* DenizenCore.implementation.isSafeThread() */ true) {
			runnable.run();
		}
		else {
			MinecraftClient.getInstance().execute(runnable);
		}
	}
}
