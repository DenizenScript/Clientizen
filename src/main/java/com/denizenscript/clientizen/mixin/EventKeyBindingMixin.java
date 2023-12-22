package com.denizenscript.clientizen.mixin;

import com.denizenscript.clientizen.events.KeyPressReleaseScriptEvent;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBinding.class)
public abstract class EventKeyBindingMixin {

    @Unique
    private static final IntSet clientizen$pressedKeys = new IntArraySet();

    @Inject(method = "setKeyPressed", at = @At("HEAD"))
    private static void clientizen$onKeyPressStateChanged(InputUtil.Key key, boolean pressed, CallbackInfo ci) {
        if (!KeyPressReleaseScriptEvent.instance.eventData.isEnabled) {
            return;
        }
        if (key.getCategory() == InputUtil.Type.KEYSYM || key.getCategory() == InputUtil.Type.MOUSE) {
            if (!pressed || !clientizen$pressedKeys.contains(key.getCode())) {
                KeyPressReleaseScriptEvent.instance.handleKeyPressStateChange(key, pressed);
            }
            if (pressed) {
                clientizen$pressedKeys.add(key.getCode());
            }
            else {
                clientizen$pressedKeys.remove(key.getCode());
            }
        }
    }
}
