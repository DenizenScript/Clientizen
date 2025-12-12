package com.denizenscript.clientizen.events;

import com.denizenscript.clientizen.objects.EntityTag;
import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import net.minecraft.world.entity.Entity;

public class PlayerSprintScriptEvent extends ScriptEvent {

    // <--[event]
    // @Events
    // player starts|stops|toggles sprinting
    //
    // @Group Player
    //
    // @Cancellable true
    //
    // @Triggers when the client player starts/stops sprinting. Does not trigger for other entities/players currently.
    //
    // @Context
    // <context.entity> returns an EntityTag of the client player.
    // <context.sprinting> returns an ElementTag(Boolean) of whether the client player is sprinting.
    //
    // @Example
    // # Will block the player from sprinting
    // on player starts sprinting:
    // - determine cancelled
    // -->

    public static PlayerSprintScriptEvent instance;

    public PlayerSprintScriptEvent() {
        instance = this;
        registerCouldMatcher("player starts|stops|toggles sprinting");
    }

    public EntityTag entity;
    public boolean isSprinting;

    @Override
    public boolean matches(ScriptPath path) {
        String state = path.eventArgLowerAt(1);
        if (state.equals("starts") && !isSprinting) {
            return false;
        }
        if (state.equals("stops") && isSprinting) {
            return false;
        }
        return super.matches(path);
    }

    @Override
    public ObjectTag getContext(String name) {
        return switch (name) {
            case "entity" -> entity;
            case "sprinting" -> new ElementTag(isSprinting);
            default -> super.getContext(name);
        };
    }

    public boolean handleSprintingToggle(Entity entity, boolean isSprinting) {
        if (!eventData.isEnabled) {
            return false;
        }
        this.entity = new EntityTag(entity);
        this.isSprinting = isSprinting;
        return fire().cancelled;
    }
}
