package com.denizenscript.clientizen.events;

import com.denizenscript.clientizen.objects.EntityTag;
import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;

public class EntitySeenUnseenByCamera extends ScriptEvent {

    // <--[event]
    // @Events
    // <'entity'> seen|unseen by camera
    //
    // @Triggers when an entity is seen by any player's camera. This does not mean the entity will be visible to the client, but within the camera's viewing frustum.
    //
    // @Context
    // <context.entity> returns an EntityTag of the entity being seen or unseen.
    // <context.switched> returns an ElementTag(Boolean) of whether the entity is being seen or unseen.
    //
    // @Warning This event may fire very rapidly.
    // -->

    public static EntitySeenUnseenByCamera instance;
    public EntityTag entity;
    public boolean seen;

    public EntitySeenUnseenByCamera() {
        registerCouldMatcher("<'entity'> seen|unseen by camera");
        instance = this;
    }

    @Override
    public boolean matches(ScriptPath path) {
        // This can be simplified once Clientizen supports entity matchers
        if (!runGenericCheck(path.eventArgLowerAt(0), entity.getTypeName()) && !runGenericCheck(path.eventArgLowerAt(0), "entity")) {
            return false;
        }
        if (seen != path.eventArgLowerAt(1).equals("seen")) {
            return false;
        }
        return super.matches(path);
    }

    @Override
    public ObjectTag getContext(String name) {
        return switch (name) {
            case "entity" -> entity;
            case "seen" -> new ElementTag(seen);
            default -> super.getContext(name);
        };
    }

    public void handleEntitySeenUnseen(EntityTag entity, boolean seen) {
        this.entity = entity;
        this.seen = seen;
        fire();
    }
}
