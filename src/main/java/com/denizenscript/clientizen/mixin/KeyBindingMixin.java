package com.denizenscript.clientizen.mixin;

import com.denizenscript.clientizen.serverevents.PlayerPressesKey;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBinding.class)
public class KeyBindingMixin {

	@Inject(method = "setKeyPressed", at = @At("HEAD"))
	private static void onKeyPressed(InputUtil.Key key, boolean pressed, CallbackInfo ci) {
		if (pressed) {
			PlayerPressesKey.instance.handleKeyPress(key.getCode());
		}
	}
}
