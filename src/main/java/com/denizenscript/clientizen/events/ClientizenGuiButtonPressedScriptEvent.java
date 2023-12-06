package com.denizenscript.clientizen.events;

import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;

public class ClientizenGuiButtonPressedScriptEvent extends ScriptEvent {

    public static ClientizenGuiButtonPressedScriptEvent instance;

    String id;

    public ClientizenGuiButtonPressedScriptEvent() {
        registerCouldMatcher("clientizen gui button pressed");
        registerSwitches("id");
        instance = this;
    }

    @Override
    public boolean matches(ScriptPath path) {
        if (!runGenericSwitchCheck(path, "id", id)) {
            return false;
        }
        return super.matches(path);
    }

    @Override
    public ObjectTag getContext(String name) {
        return switch (name) {
            case "id" -> new ElementTag(id, true);
            default -> super.getContext(name);
        };
    }

    // TODO: proper identification system for UI elements
    public void handleButtonPress(String id) {
        if (!enabled) {
            return;
        }
        this.id = id;
        fire();
    }

    boolean enabled = false;

    @Override
    public void init() {
        enabled = true;
    }

    @Override
    public void destroy() {
        enabled = false;
    }
}
