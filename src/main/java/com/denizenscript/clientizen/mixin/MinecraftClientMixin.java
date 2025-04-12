package com.denizenscript.clientizen.mixin;

import com.denizenscript.clientizen.events.ScreenOpenCloseEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow
    @Nullable
    public Screen currentScreen;

    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Inject(method = "setScreen", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", opcode = Opcodes.PUTFIELD))
    private void clientizen$onScreenOpened(Screen screen, CallbackInfo ci){
        if (screen == null) {
            return;
        }
        // An InventoryScreen is opened which then opens a creative screen if needed, so ignore an InventoryScreen if a creative one will be opened
        if (screen instanceof InventoryScreen && player.isInCreativeMode()) {
            return;
        }
        Screen previousScreen = currentScreen;
        // Since an inventory screen is opened internally but isn't actually visible, this doesn't count as having a previous screen
        if (screen instanceof CreativeInventoryScreen && previousScreen instanceof InventoryScreen) {
            previousScreen = null;
        }
        ScreenOpenCloseEvent.instance.handleScreenChange(screen, previousScreen, true);
    }
}
