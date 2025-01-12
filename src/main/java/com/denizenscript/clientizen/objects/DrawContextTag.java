package com.denizenscript.clientizen.objects;

import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import net.minecraft.client.gui.DrawContext;

public class DrawContextTag implements ObjectTag {

    // <--[ObjectType]
    // @name DrawContextTag
    // @prefix draw_context
    // @base ElementTag
    //
    // @description
    // A DrawContextTag represents a DrawContext class instance.
    //
    // DrawContext class is the main class used for rendering in the game. It is used for rendering shapes, text and textures.
    // -->


    public static boolean matches(String string) {
        if (string.startsWith("draw_context@")) {
            return true;
        }
        return false;
    }

    public final DrawContext context;

    public DrawContextTag(DrawContext context) {
        this.context = context;
    }

    public DrawContext getDrawContext() {
        return this.context;
    }

    @Override
    public String identify() {
        return "draw_context@";
    }

    @Override
    public String debuggable() {
        return "<LG>draw_context@<Y>";
    }

    @Override
    public String identifySimple() {
        return identify();
    }

    @Override
    public String toString() {
        return identify();
    }

    @Override
    public boolean advancedMatches(String matcher, TagContext context) {
        String matcherLower = CoreUtilities.toLowerCase(matcher);
        if (matcherLower.equals("draw_context")) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isUnique() {
        return true;
    }

    String prefix = "DrawContext";

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public ObjectTag setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }
}
