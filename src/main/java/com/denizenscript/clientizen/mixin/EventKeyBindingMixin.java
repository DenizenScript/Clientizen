package com.denizenscript.clientizen.mixin;

import com.denizenscript.clientizen.events.KeyPressReleaseScriptEvent;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public abstract class EventKeyBindingMixin {

    @Unique
    private static final IntSet clientizen$pressedKeys = new IntArraySet();

    @Inject(method = "onKey", at = @At("RETURN"))
    private static void clientizen$onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (!KeyPressReleaseScriptEvent.instance.eventData.isEnabled) {
            return;
        }

        InputUtil.Key inputKey = InputUtil.fromKeyCode(key, scancode);
        boolean pressed = InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), key);

        if (inputKey.getCategory() == InputUtil.Type.KEYSYM || inputKey.getCategory() == InputUtil.Type.MOUSE) {
            if (!pressed || !clientizen$pressedKeys.contains(scancode)) {
                KeyPressReleaseScriptEvent.instance.handleKeyPressStateChange(inputKey, pressed);
            }
            if (pressed) {
                clientizen$pressedKeys.add(inputKey.getCode());
            }
            else {
                clientizen$pressedKeys.remove(inputKey.getCode());
            }
        }
    }
}
