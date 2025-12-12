package com.denizenscript.clientizen.mixin;

import net.minecraft.client.ToggleKeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.BooleanSupplier;

@Mixin(ToggleKeyMapping.class)
public interface StickyKeyBindingAccessor {

    @Accessor("needsToggle")
    BooleanSupplier getToggleModeChecker();
}
