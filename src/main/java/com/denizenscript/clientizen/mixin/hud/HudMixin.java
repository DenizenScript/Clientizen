package com.denizenscript.clientizen.mixin.hud;

import com.denizenscript.clientizen.events.RenderScriptEvent;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class HudMixin {

    @Inject(method = "render", at = @At("RETURN"))
    private void clientizen$chatRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        for (RenderScriptEvent event : RenderScriptEvent.registered) {
            event.drawContext = context;
            event.handleRender();
        }
    }
}
