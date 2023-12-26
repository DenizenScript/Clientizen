package com.denizenscript.clientizen.scripts.commands;

import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer;
import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptGuiDescription;
import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptScreen;
import com.denizenscript.denizencore.objects.core.ScriptTag;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.scripts.commands.Holdable;
import com.denizenscript.denizencore.scripts.commands.generator.ArgLinear;
import com.denizenscript.denizencore.scripts.commands.generator.ArgName;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import net.minecraft.client.MinecraftClient;

public class GuiCommand extends AbstractCommand implements Holdable {

    // <--[command]
    // @Name gui
    // @Syntax gui [<script>]
    // @Required 1
    // @Maximum 1
    // @Short Opens a GUI script into a GUI.
    // @Group GUI System
    //
    // @Description
    // Opens the GUI script specified as a GUI.
    // The GUI script must have a panel of any kind as it's element to be opened (which can contain any other elements within it).
    //
    // @Tags
    // None
    //
    // @Usage
    // Opens a GUI script name "my_gui"
    // - gui my_gui
    //
    // -->

    public GuiCommand() {
        setName("gui");
        setSyntax("gui [<script>]");
        setRequiredArguments(1, 1);
        autoCompile();
    }

    @Override
    public void addCustomTabCompletions(TabCompletionsBuilder tab) {
        tab.addScriptsOfType(GuiScriptContainer.class);
    }

    public static void autoExecute(@ArgLinear @ArgName("script") ScriptTag script) {
        if (!(script.getContainer() instanceof GuiScriptContainer guiScriptContainer)) {
            Debug.echoError("Invalid script '" + script.debuggable() + "<W>' specified: must be a GUI script.");
            return;
        }
        WPanel rootPanel = guiScriptContainer.createGUIRoot();
        if (rootPanel == null) {
            Debug.echoError("GUI script '" + script.debuggable() + "<W>' is invalid.");
            return;
        }
        GuiScriptScreen screen = new GuiScriptScreen(new GuiScriptGuiDescription(rootPanel), guiScriptContainer);
        MinecraftClient client = MinecraftClient.getInstance();
        client.send(() -> client.setScreen(screen));
    }
}
