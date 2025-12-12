package com.denizenscript.clientizen.mixin.gui;

import io.github.cottonmc.cotton.gui.widget.WText;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(WText.class)
public interface WTextAccessor {

    @Accessor(remap = false)
    List<FormattedCharSequence> getWrappedLines();

    @Accessor(remap = false)
    void setWrappedLines(List<FormattedCharSequence> wrappedLines);

    @Accessor(remap = false)
    void setWrappingScheduled(boolean wrappingScheduled);
}
