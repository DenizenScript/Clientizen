package com.denizenscript.clientizen.debuggui;

import com.denizenscript.clientizen.Clientizen;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class ClientizenDebugScreen extends CottonClientScreen {

    public static void register() {
        KeyMapping openDebugScreenKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.clientizen.open_debug_screen",
                GLFW.GLFW_KEY_R,
                KeyMapping.Category.register(Clientizen.id("keys"))
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openDebugScreenKey.consumeClick()) {
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
