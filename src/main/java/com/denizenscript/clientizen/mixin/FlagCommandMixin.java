package com.denizenscript.clientizen.mixin;

import com.denizenscript.denizencore.scripts.commands.core.FlagCommand;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

// TODO: This is a temporary hack, should be added to Core
@Mixin(FlagCommand.class)
public abstract class FlagCommandMixin {

    @ModifyArg(method = "execute",
            at = @At(value = "INVOKE", target = "Lcom/denizenscript/denizencore/utilities/CoreUtilities;equalsIgnoreCase(Ljava/lang/String;Ljava/lang/String;)Z"),
            index = 1,
            remap = false)
    private String clientizen$modifyGlobalFlaggable(String input) {
        return "client";
    }

    @ModifyArg(method = "execute",
            at = @At(value = "INVOKE", target = "Lcom/denizenscript/denizencore/utilities/debugging/Debug;echoError(Ljava/lang/String;)V", ordinal = 1),
            remap = false)
    private String clientizen$modifyErrorMessage(String input) {
        return CoreUtilities.replace(input, "server", "client");
    }
}
