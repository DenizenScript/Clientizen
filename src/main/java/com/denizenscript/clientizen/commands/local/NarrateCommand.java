package com.denizenscript.clientizen.commands.local;

import com.denizenscript.denizen2core.commands.AbstractCommand;
import com.denizenscript.denizen2core.commands.CommandEntry;
import com.denizenscript.denizen2core.commands.CommandQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;

public class NarrateCommand extends AbstractCommand {

    // <--[command]
    // @Name narrate
    // @Arguments <message>
    // @Short shows a message in the client chat box.
    // @Updated 2017/02/01
    // @Group Local
    // @Minimum 1
    // @Maximum 1
    // @Description
    // Shows a message inside the client chat box as if it were a typical
    // system message.
    // @Example
    // # This example shows the message 'Hello'.
    // - narrate "Hello"
    // -->

    @Override
    public String getName() {
        return "narrate";
    }

    @Override
    public String getArguments() {
        return "<message>";
    }

    @Override
    public int getMinimumArguments() {
        return 1;
    }

    @Override
    public int getMaximumArguments() {
        return 1;
    }

    @Override
    public void execute(CommandQueue queue, CommandEntry entry) {
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(entry.getArgumentObject(queue, 0).toString()));
    }
}
