package com.denizenscript.clientizen.scripts.commands;

import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer;
import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptGuiDescription;
import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptScreen;
import com.denizenscript.denizencore.exceptions.InvalidArgumentsRuntimeException;
import com.denizenscript.denizencore.objects.core.ScriptTag;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.scripts.commands.generator.ArgDefaultNull;
import com.denizenscript.denizencore.scripts.commands.generator.ArgLinear;
import com.denizenscript.denizencore.scripts.commands.generator.ArgName;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import net.minecraft.client.MinecraftClient;

public class GuiCommand extends AbstractCommand {

    // <--[command]
    // @Name gui
    // @Syntax gui [open <script>]/[close]
    // @Required 1
    // @Maximum 2
    // @Short Opens a GUI script into a GUI or closes a currently open one.
    // @Group GUI System
    //
    // @Description
    // Opens the GUI script specified as a GUI, or closes a currently open one.
    // The GUI script must have a panel of any kind as it's element to be opened (which can contain any other elements within it).
    //
    // @Tags
    // None
    //
    // @Usage
    // Opens a GUI script named "my_gui".
    // - gui open my_gui
    //
    // @Usage
    // Closes the currently open GUI.
    // - gui close
    //
    // -->

    public GuiCommand() {
        setName("gui");
        setSyntax("gui [open <script>]/[close]");
        setRequiredArguments(1, 2);
        autoCompile();
    }

    @Override
    public void addCustomTabCompletions(TabCompletionsBuilder tab) {
        tab.addScriptsOfType(GuiScriptContainer.class);
    }

    public enum Action {OPEN, CLOSE}

    public static void autoExecute(@ArgName("action") Action action,
                                   @ArgName("script") @ArgLinear @ArgDefaultNull ScriptTag script) {
        MinecraftClient client = MinecraftClient.getInstance();
        switch (action) {
            case OPEN -> {
                if (script == null) {
                    throw new InvalidArgumentsRuntimeException("Must specify a GUI script to open.");
                }
                if (!(script.getContainer() instanceof GuiScriptContainer guiScript)) {
                    Debug.echoError("Invalid script '" + script.debuggable() + "<W>' specified: must be a GUI script.");
                    return;
                }
                WPanel rootPanel = guiScript.createGUIRoot();
                if (rootPanel == null) {
                    Debug.echoError("GUI script '" + script.debuggable() + "<W>' is invalid.");
                    return;
                }
                GuiScriptScreen screen = new GuiScriptScreen(new GuiScriptGuiDescription(rootPanel), guiScript);
                client.send(() -> client.setScreen(screen));
            }
            case CLOSE -> client.send(() -> client.setScreen(null));
        }
    }
}
