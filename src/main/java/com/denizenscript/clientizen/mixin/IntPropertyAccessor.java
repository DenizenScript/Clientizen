package com.denizenscript.clientizen.mixin;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(IntegerProperty.class)
public interface IntPropertyAccessor {

    @Accessor
    int getMin();

    @Accessor
    int getMax();
}
