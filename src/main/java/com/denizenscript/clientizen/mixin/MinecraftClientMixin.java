package com.denizenscript.clientizen.mixin;

import com.denizenscript.clientizen.events.ScreenOpenCloseEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {

    @Shadow
    @Nullable
    public Screen screen;

    @Shadow
    @Nullable
    public LocalPlayer player;

    @Inject(method = "setScreen", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;screen:Lnet/minecraft/client/gui/screens/Screen;", opcode = Opcodes.PUTFIELD))
    private void clientizen$onScreenOpened(Screen screen, CallbackInfo ci){
        if (screen == null) {
            return;
        }
        // An InventoryScreen is opened which then opens a creative screen if needed, so ignore an InventoryScreen if a creative one will be opened
        if (screen instanceof InventoryScreen && player.hasInfiniteMaterials()) {
            return;
        }
        Screen previousScreen = screen;
        // Since an inventory screen is opened internally but isn't actually visible, this doesn't count as having a previous screen
        if (screen instanceof CreativeModeInventoryScreen && previousScreen instanceof InventoryScreen) {
            previousScreen = null;
        }
        ScreenOpenCloseEvent.instance.handleScreenChange(screen, previousScreen, true);
    }
}
