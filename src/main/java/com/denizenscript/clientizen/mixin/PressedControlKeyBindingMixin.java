package com.denizenscript.clientizen.mixin;

import com.denizenscript.clientizen.access.KeyBindingMixinAccess;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyMapping.class)
public abstract class PressedControlKeyBindingMixin implements KeyBindingMixinAccess {

    @Unique
    boolean clientizen$disabled = false;

    @Override
    public void clientizen$disableUntilPress() {
        clientizen$disabled = true;
    }

    @Shadow
    private boolean isDown;

    @Override
    public void clientizen$forceSetPressed(boolean pressed) {
        this.isDown = pressed;
    }

    @Inject(method = "setDown", at = @At("HEAD"), cancellable = true)
    private void clientizen$onSetPressed(boolean pressed, CallbackInfo ci) {
        if (!clientizen$disabled) {
            return;
        }
        if (pressed == this.isDown || (this instanceof StickyKeyBindingAccessor stickyKeyBindingAccessor && stickyKeyBindingAccessor.getToggleModeChecker().getAsBoolean())) {
            clientizen$disabled = false;
            return;
        }
        ci.cancel();
    }
}
