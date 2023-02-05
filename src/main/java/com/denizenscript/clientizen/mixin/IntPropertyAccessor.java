package com.denizenscript.clientizen.mixin;

import net.minecraft.state.property.IntProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(IntProperty.class)
public interface IntPropertyAccessor {

    @Accessor
    int getMin();

    @Accessor
    int getMax();
}
