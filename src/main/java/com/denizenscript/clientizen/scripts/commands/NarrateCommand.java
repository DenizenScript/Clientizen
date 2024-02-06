package com.denizenscript.clientizen.scripts.commands;

import com.denizenscript.clientizen.Clientizen;
import com.denizenscript.clientizen.util.Utilities;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.scripts.commands.generator.ArgLinear;
import com.denizenscript.denizencore.scripts.commands.generator.ArgName;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class NarrateCommand extends AbstractCommand {

    // <--[command]
    // @Name Narrate
    // @Syntax narrate [<text>] (center)
    // @Required 1
    // @Maximum 2
    // @Short Prints some text into the client's chat area.
    //
    // @Description
    // Prints some text into the client's chat area, optionally centering it.
    // When centering text, new lines will be split with each line being centered,
    // and if the text is wider than the chat HUD it will be wrapped into multiple centered lines.
    //
    // @Tags
    // None
    //
    // @Usage
    // Use to narrate text.
    // - narrate "Hello World!"
    //
    // @Usage
    // Use to narrate 2 centered lines.
    // - narrate "Hello!<n>This is centered!" center
    // -->

    public static final Style SPACING_FONT = Style.EMPTY.withFont(Clientizen.id("spacing"));

    public NarrateCommand() {
        setName("narrate");
        setSyntax("narrate [<text>] (center)");
        setRequiredArguments(1, 2);
        autoCompile();
    }

    public static void autoExecute(@ArgLinear @ArgName("text") String text,
                                   @ArgName("center") boolean center) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!center) {
            client.inGameHud.getChatHud().addMessage(Text.literal(text), null, null);
            return;
        }
        int hudWidth = MathHelper.floor((double) client.inGameHud.getChatHud().getWidth() / client.inGameHud.getChatHud().getChatScale());
        if (CoreUtilities.contains(text, '\n')) {
            for (String rawLine : CoreUtilities.split(text, '\n')) {
                sendSpacedLine(Text.literal(rawLine), hudWidth);
            }
            return;
        }
        sendSpacedLine(Text.literal(text), hudWidth);
    }

    private static void sendSpacedLine(MutableText line, int hudWidth) {
        MinecraftClient client = MinecraftClient.getInstance();
        int lineWidth = client.textRenderer.getWidth(line);
        if (lineWidth > hudWidth) {
            for (OrderedText wrappedLine : client.textRenderer.wrapLines(line, hudWidth)) {
                sendSpacedLine(Text.literal(Utilities.orderedTextToString(wrappedLine)), hudWidth);
            }
            return;
        }
        String spacing = " ".repeat((int) ((hudWidth - lineWidth) / 2f / 0.5f));
        Text space = Text.literal(spacing).setStyle(SPACING_FONT);
        client.inGameHud.getChatHud().addMessage(Text.empty().append(space).append(line).append(space), null, null);
    }
}
