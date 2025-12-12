package com.denizenscript.clientizen.mixin;

import com.denizenscript.clientizen.events.KeyPressReleaseScriptEvent;
import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyMapping.class)
public abstract class EventKeyBindingMixin {

    @Unique
    private static final IntSet clientizen$pressedKeys = new IntArraySet();

    @Inject(method = "set", at = @At("HEAD"))
    private static void clientizen$onKeyPressStateChanged(InputConstants.Key key, boolean pressed, CallbackInfo ci) {
        if (!KeyPressReleaseScriptEvent.instance.eventData.isEnabled) {
            return;
        }
        if (key.getType() == InputConstants.Type.KEYSYM || key.getType() == InputConstants.Type.MOUSE) {
            if (!pressed || !clientizen$pressedKeys.contains(key.getValue())) {
                KeyPressReleaseScriptEvent.instance.handleKeyPressStateChange(key, pressed);
            }
            if (pressed) {
                clientizen$pressedKeys.add(key.getValue());
            }
            else {
                clientizen$pressedKeys.remove(key.getValue());
            }
        }
    }
}
