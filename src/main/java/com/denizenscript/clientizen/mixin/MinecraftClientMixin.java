package com.denizenscript.clientizen.mixin;

import com.denizenscript.clientizen.events.ScreenOpenCloseEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Inject(method = "setScreen", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", opcode = Opcodes.PUTFIELD))
    private void clientizen$onScreenOpened(Screen screen, CallbackInfo ci){
        if (screen != null) {
            ScreenOpenCloseEvent.instance.handleScreenChange(screen, ((MinecraftClient) (Object) this).currentScreen, true);
        }
    }
}
