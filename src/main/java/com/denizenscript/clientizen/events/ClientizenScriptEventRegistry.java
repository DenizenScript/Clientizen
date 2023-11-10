package com.denizenscript.clientizen.events;

import com.denizenscript.denizencore.events.ScriptEvent;

public class ClientizenScriptEventRegistry {

    public static void registerEvents() {
        ScriptEvent.registerScriptEvent(ClientizenGuiButtonPressedScriptEvent.class);
        ScriptEvent.registerScriptEvent(KeyPressReleaseScriptEvent.class);
        ScriptEvent.registerScriptEvent(ScreenOpenCloseEvent.class);
    }
}
