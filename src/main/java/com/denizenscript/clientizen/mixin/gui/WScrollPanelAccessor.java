package com.denizenscript.clientizen.mixin.gui;

import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WScrollPanel.class)
public interface WScrollPanelAccessor {

    @Accessor(remap = false)
    WWidget getWidget();
}
