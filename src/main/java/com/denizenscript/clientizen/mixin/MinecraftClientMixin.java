package com.denizenscript.clientizen.mixin;

import com.denizenscript.clientizen.events.ScreenOpenCloseEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow
    @Nullable
    public Screen currentScreen;

    @Inject(method = "setScreen", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", ordinal = 2))
    private void clientizen$screenOpened(Screen screen, CallbackInfo ci) {
        if (screen != null) {
            ScreenOpenCloseEvent.instance.handleScreenChange(screen, currentScreen, true);
        }
    }

    @Inject(method = "setScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;removed()V"))
    private void clientizen$screenClosed(Screen screen, CallbackInfo ci) {
        if (screen != null) {
            ScreenOpenCloseEvent.instance.handleScreenChange(currentScreen, screen, false);
        }
    }
}
