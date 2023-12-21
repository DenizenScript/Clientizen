package com.denizenscript.clientizen.events;

import com.denizenscript.clientizen.objects.EntityTag;
import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import net.minecraft.entity.Entity;

public class EntityStartsStopsRenderingScriptEvent extends ScriptEvent {

    // <--[event]
    // @Events
    // <entity> starts|stops rendering
    //
    // @Triggers when an entity is rendered by the client's camera. This does not mean the entity will always be visible, but within the camera's viewing frustum.
    //
    // @Context
    // <context.entity> returns an EntityTag of the entity being rendered.
    // <context.rendered> returns an ElementTag(Boolean) of whether the entity is being rendered.
    //
    // @Warning This event may fire very rapidly.
    // -->

    public static EntityStartsStopsRenderingScriptEvent instance;
    public EntityTag entity;
    public boolean rendered;

    public EntityStartsStopsRenderingScriptEvent() {
        registerCouldMatcher("<entity> starts|stops rendering");
        instance = this;
    }

    @Override
    public boolean matches(ScriptPath path) {
        if (!path.tryArgObject(0, entity)) {
            return false;
        }
        if (rendered != path.eventArgLowerAt(1).equals("starts")) {
            return false;
        }
        return super.matches(path);
    }

    @Override
    public ObjectTag getContext(String name) {
        return switch (name) {
            case "entity" -> entity;
            case "rendered" -> new ElementTag(rendered);
            default -> super.getContext(name);
        };
    }

    public void handleEntityRendered(Entity entity, boolean seen) {
        this.entity = new EntityTag(entity);
        this.rendered = seen;
        fire();
    }
}
