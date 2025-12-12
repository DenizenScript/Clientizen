package com.denizenscript.clientizen.scripts.commands;

import com.denizenscript.clientizen.Clientizen;
import com.denizenscript.clientizen.util.Utilities;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.scripts.commands.generator.ArgLinear;
import com.denizenscript.denizencore.scripts.commands.generator.ArgName;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

public class NarrateCommand extends AbstractCommand {

    // <--[command]
    // @Name Narrate
    // @Syntax narrate [<text>] (center)
    // @Required 1
    // @Maximum 2
    // @Short Prints some text into the client's chat area.
    // @Group interface
    //
    // @Description
    // Prints some text into the client's chat area, optionally centering it.
    // When centering text, new lines will be split with each line being centered, and text wider than the chat HUD will be wrapped into multiple centered lines.
    //
    // @Tags
    // None
    //
    // @Usage
    // Use to narrate text.
    // - narrate "Hello World!"
    //
    // @Usage
    // Use to narrate some text in the center of the chat area.
    // - narrate "This is in the center!" center
    //
    // @Usage
    // Use to narrate 2 centered lines.
    // - narrate "Hello!<n>This is centered!" center
    // -->

    public static final Style SPACING_FONT = Style.EMPTY.withFont(new FontDescription.Resource(Clientizen.id("spacing")));

    public NarrateCommand() {
        setName("narrate");
        setSyntax("narrate [<text>] (center)");
        setRequiredArguments(1, 2);
        autoCompile();
    }

    public static void autoExecute(@ArgLinear @ArgName("text") String text,
                                   @ArgName("center") boolean center) {
        Minecraft client = Minecraft.getInstance();
        if (!center) {
            client.gui.getChat().addMessage(Component.literal(text), null, null);
            return;
        }
        int hudWidth = Mth.floor((double) client.gui.getChat().getWidth() / client.gui.getChat().getScale());
        if (CoreUtilities.contains(text, '\n')) {
            for (String rawLine : CoreUtilities.split(text, '\n')) {
                sendCenteredLine(Component.literal(rawLine), hudWidth);
            }
            return;
        }
        sendCenteredLine(Component.literal(text), hudWidth);
    }

    private static void sendCenteredLine(Component line, int hudWidth) {
        Minecraft client = Minecraft.getInstance();
        int lineWidth = client.font.width(line);
        if (lineWidth > hudWidth) {
            for (FormattedCharSequence wrappedLine : client.font.split(line, hudWidth)) {
                sendCenteredLine(Component.literal(Utilities.orderedTextToString(wrappedLine)), hudWidth);
            }
            return;
        }
        String spacingStr = " ".repeat((int) ((hudWidth - lineWidth) / 2f / 0.5f));
        Component spacing = Component.literal(spacingStr).setStyle(SPACING_FONT);
        client.gui.getChat().addMessage(Component.empty().append(spacing).append(line).append(spacing), null, null);
    }
}
