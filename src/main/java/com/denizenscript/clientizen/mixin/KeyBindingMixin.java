package com.denizenscript.clientizen.mixin;

import com.denizenscript.clientizen.events.KeyPressReleaseScriptEvent;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBinding.class)
public class KeyBindingMixin {

    private static final IntSet pressedKeys = new IntArraySet();

    @Inject(method = "setKeyPressed", at = @At("HEAD"))
    private static void clientizen$onKeyPressStateChanged(InputUtil.Key key, boolean pressed, CallbackInfo ci) {
        if (key.getCategory() == InputUtil.Type.KEYSYM || key.getCategory() == InputUtil.Type.MOUSE) {
            if (!pressed || !pressedKeys.contains(key.getCode())) {
                KeyPressReleaseScriptEvent.instance.handleKeyPressStateChange(key, pressed);
            }
            if (pressed) {
                pressedKeys.add(key.getCode());
            }
            else {
                pressedKeys.remove(key.getCode());
            }
        }
    }
}
