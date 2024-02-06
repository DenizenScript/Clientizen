package com.denizenscript.clientizen.objects.extensions;

import com.denizenscript.clientizen.util.Utilities;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ClientizenElementExtensions {

    public static void register() {

        // <--[tag]
        // @attribute <ElementTag.text_width>
        // @returns ElementTag(Number)
        // @description
        // Returns an element's width in pixels, using the client's text rendering.
        // @example
        // # Use to check if a message is too wide.
        // - if <[message].text_width> > 30:
        //   - narrate "That message is too wide!"
        //   - stop
        // -->
        ElementTag.tagProcessor.registerTag(ElementTag.class, "text_width", (attribute, object) -> {
            return new ElementTag(MinecraftClient.getInstance().textRenderer.getWidth(object.asString()));
        });

        // <--[tag]
        // @attribute <ElementTag.wrap_lines[<width>]>
        // @returns ListTag
        // @description
        // Returns the element split into multiple lines, with each line being less (or just as) wide as the specified width.
        // @example
        // # Use to split text into lines, with each line's width being no more than 30.
        // - foreach <[text].wrap_lines[30]> as:line:
        //   - narrate <[line]>
        // -->
        ElementTag.tagProcessor.registerTag(ListTag.class, ElementTag.class, "wrap_lines", (attribute, object, param) -> {
            if (!param.isInt()) {
                attribute.echoError("Invalid width specified: " + param + '.');
                return null;
            }
            return new ListTag(MinecraftClient.getInstance().textRenderer.wrapLines(Text.literal(object.asString()), param.asInt()),
                    line -> new ElementTag(Utilities.orderedTextToString(line), true));
        });
    }
}
