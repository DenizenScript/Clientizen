package com.denizenscript.clientizen.debuggui;

import com.denizenscript.clientizen.Clientizen;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class ClientizenDebugScreen extends CottonClientScreen {

    public static void register() {
        KeyBinding openDebugScreenKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.clientizen.open_debug_screen",
                GLFW.GLFW_KEY_R,
                KeyBinding.Category.create(Clientizen.id("keys"))
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openDebugScreenKey.wasPressed()) {
                client.setScreen(new ClientizenDebugScreen(new ClientizenDebugGUI()));
            }
        });
    }

    public ClientizenDebugScreen(ClientizenDebugGUI description) {
        super(description);
    }

    @Override
    public void removed() {
        ((ClientizenDebugGUI) description).onClose();
    }
}
