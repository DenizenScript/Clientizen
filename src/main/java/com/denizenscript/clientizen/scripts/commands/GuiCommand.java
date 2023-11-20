package com.denizenscript.clientizen.scripts.commands;

import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer;
import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptGuiDescription;
import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptScreen;
import com.denizenscript.denizencore.objects.core.ScriptTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.scripts.commands.Holdable;
import com.denizenscript.denizencore.scripts.commands.generator.ArgLinear;
import com.denizenscript.denizencore.scripts.commands.generator.ArgName;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import net.minecraft.client.MinecraftClient;

public class GuiCommand extends AbstractCommand implements Holdable {

    public GuiCommand() {
        setName("gui");
        setSyntax("gui [<script>]");
        setRequiredArguments(1, 1);
        autoCompile();
    }

    public static void autoExecute(ScriptEntry scriptEntry,
                                   @ArgLinear @ArgName("script") ScriptTag script) {
        if (script.getContainer() instanceof GuiScriptContainer guiScriptContainer) {
            WPanel rootPanel = guiScriptContainer.createGUIRoot(scriptEntry.getContext());
            MinecraftClient client = MinecraftClient.getInstance();
            client.send(() -> client.setScreen(new GuiScriptScreen(new GuiScriptGuiDescription(rootPanel))));
        }
    }
}
