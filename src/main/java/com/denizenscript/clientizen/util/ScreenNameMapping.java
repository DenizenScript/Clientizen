package com.denizenscript.clientizen.util;

import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptScreen;
import net.minecraft.client.gui.screens.Screen;

import java.util.HashMap;
import java.util.Map;

public class ScreenNameMapping {

    private static final Map<Class<? extends Screen>, String> TYPE_MAP = new HashMap<>();

    public static String getScreenName(Screen screen) {
        if (screen instanceof GuiScriptScreen guiScriptScreen) {
            return guiScriptScreen.getScript().getName();
        }
        return TYPE_MAP.computeIfAbsent(screen.getClass(), clazz -> {
            String className = clazz.getSimpleName();
            if (className.endsWith("Screen")) {
                className = className.substring(0, className.length() - "Screen".length());
            }
            return Utilities.camelCaseToSnake(className);
        });
    }
}
