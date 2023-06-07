package com.denizenscript.clientizen.events;

import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;

import java.util.HashMap;
import java.util.Map;

public class ScreenOpenCloseEvent extends ScriptEvent {

    // <--[event]
    // @Events
    // <'screen_type'> screen opened|closed
    //
    // @Triggers when a screen is opened or closed.
    //
    // @Context
    // <context.screen_type> returns an ElementTag of the screen type that opened.
    // <context.switched> returns an ElementTag(Boolean) of whether the screen was opened from another screen.
    //
    // -->

    // TODO: This event needs a partial redo, mainly:
    // - CreativeInventoryScreen no longer extends InventoryScreen
    // - Add all relevant screen types
    // - Potentially add support for handling screens that aren't directly defined via class name

    public static ScreenOpenCloseEvent instance;

    public static Map<String, Class<?>> TYPE_MAP = new HashMap<>();

    static {
        TYPE_MAP.put("inventory", InventoryScreen.class);
        TYPE_MAP.put("creative", CreativeInventoryScreen.class);
        TYPE_MAP.put("pause", GameMenuScreen.class);
        TYPE_MAP.put("options", OptionsScreen.class);
        TYPE_MAP.put("advancements", AdvancementsScreen.class);
    }

    public String type;
    public boolean opened;
    public boolean switched;

    public ScreenOpenCloseEvent() {
        registerCouldMatcher("<'screen_type'> screen opened|closed");
        instance = this;
    }

    @Override
    public boolean matches(ScriptPath path) {
        String screenMatcher = path.eventArgLowerAt(0);
        if (!runGenericCheck(screenMatcher, type)) {
            return false;
        }
        if (opened != path.eventArgLowerAt(2).equals("opened")) {
            return false;
        }
        return super.matches(path);
    }

    @Override
    public ObjectTag getContext(String name) {
        return switch (name) {
            case "screen_type" -> new ElementTag(type);
            case "switched" -> new ElementTag(switched);
            default -> super.getContext(name);
        };
    }


    public void handleScreenChange(Screen screen, Screen otherScreen, boolean open) {
        for (Map.Entry<String, Class<?>> pair : TYPE_MAP.entrySet()) {
            if (pair.getKey().equals("inventory") && screen instanceof CreativeInventoryScreen) {
                continue;
            }
            if (pair.getValue().isInstance(screen)) {
                type = pair.getKey();
                opened = open;
                switched = otherScreen != null;
                fire();
                return;
            }
        }
    }
}
