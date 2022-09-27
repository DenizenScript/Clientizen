package com.denizenscript.clientizen.mixin;

import com.denizenscript.clientizen.serverevents.KeyPressReleaseServerEvent;
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
	private static void onKeyPressStateChanged(InputUtil.Key key, boolean pressed, CallbackInfo ci) {
		if (KeyPressReleaseServerEvent.instance.enabled && key.getCategory() == InputUtil.Type.KEYSYM) {
			int code = key.getCode();
			if (!pressed || !pressedKeys.contains(code)) {
				KeyPressReleaseServerEvent.instance.handleKeyPressStateChange(code, pressed);
			}
			if (pressed) {
				pressedKeys.add(code);
			}
			else {
				pressedKeys.remove(code);
			}
		}
	}
}
