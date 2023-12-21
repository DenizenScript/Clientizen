package com.denizenscript.clientizen.events;

import com.denizenscript.clientizen.objects.EntityTag;
import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import net.minecraft.entity.Entity;

public class EntitySeenUnseenByCamera extends ScriptEvent {

    // <--[event]
    // @Events
    // <entity> seen|unseen by camera
    //
    // @Triggers when an entity is seen by the client's camera. This does not mean the entity will be visible to the client, but within the camera's viewing frustum.
    //
    // @Context
    // <context.entity> returns an EntityTag of the entity being seen or unseen.
    // <context.seen> returns an ElementTag(Boolean) of whether the entity is being seen or unseen.
    //
    // @Warning This event may fire very rapidly.
    // -->

    public static EntitySeenUnseenByCamera instance;
    public EntityTag entity;
    public boolean seen;

    public EntitySeenUnseenByCamera() {
        registerCouldMatcher("<entity> seen|unseen by camera");
        instance = this;
    }

    @Override
    public boolean matches(ScriptPath path) {
        if (!path.tryArgObject(0, entity)) {
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

    public void handleEntitySeenUnseen(Entity entity, boolean seen) {
        this.entity = new EntityTag(entity);
        this.seen = seen;
        fire();
    }
}
