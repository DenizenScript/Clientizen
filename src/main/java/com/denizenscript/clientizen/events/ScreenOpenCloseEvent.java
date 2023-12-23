package com.denizenscript.clientizen.events;

import com.denizenscript.clientizen.util.ScreenNameMapping;
import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.Screen;

public class ScreenOpenCloseEvent extends ScriptEvent {

    // <--[event]
    // @Events
    // screen opened|closed
    //
    // @Group User Interface
    //
    // @Switch type:<screen_type> to only process the event if the type of screen opened matches the specified matcher.
    // @Switch from:<screen_type> to only process the event if the screen was opened from a different screen and that screen's type matches the specified matcher.
    //
    // @Triggers when a screen is opened or closed.
    //
    // @Context
    // <context.screen_type> returns an ElementTag of the screen type that opened/closed.
    // <context.previous_screen_type> returns an ElementTag of the screen this screen was opened from, if any.
    //
    // -->

    public static ScreenOpenCloseEvent instance;

    static {
        ScreenEvents.AFTER_INIT.register((client, openedScreen, scaledWidth, scaledHeight) -> {
            ScreenEvents.remove(openedScreen).register(closedScreen -> ScreenOpenCloseEvent.instance.handleScreenChange(closedScreen, null, false));
        });
    }

    public String type;
    public boolean opened;
    public String previousType;

    public ScreenOpenCloseEvent() {
        registerCouldMatcher("screen opened|closed");
        registerSwitches("type", "from");
        instance = this;
    }

    @Override
    public boolean matches(ScriptPath path) {
        if (!runGenericSwitchCheck(path, "type", type)) {
            return false;
        }
        if (!runGenericSwitchCheck(path, "from", previousType)) {
            return false;
        }
        if (opened != path.eventArgLowerAt(1).equals("opened")) {
            return false;
        }
        return super.matches(path);
    }

    @Override
    public ObjectTag getContext(String name) {
        return switch (name) {
            case "screen_type" -> new ElementTag(type, true);
            case "previous_screen_type" -> previousType != null ? new ElementTag(previousType, true) : null;
            default -> super.getContext(name);
        };
    }

    public void handleScreenChange(Screen screen, Screen previousScreen, boolean open) {
        if (!eventData.isEnabled) {
            return;
        }
        type = ScreenNameMapping.getScreenName(screen.getClass());
        previousType = previousScreen != null ? ScreenNameMapping.getScreenName(previousScreen.getClass()) : null;
        opened = open;
        fire();
    }
}
