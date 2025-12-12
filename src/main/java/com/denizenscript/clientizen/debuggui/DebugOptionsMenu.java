package com.denizenscript.clientizen.debuggui;

import com.denizenscript.denizencore.utilities.CoreConfiguration;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.mojang.blaze3d.platform.Window;
import io.github.cottonmc.cotton.gui.widget.TooltipBuilder;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.function.BooleanSupplier;

public class DebugOptionsMenu extends WPlainPanel {

    public static final int BUTTON_HEIGHT = 18, BUTTON_WIDTH = 100;
    public static final int OFFSET = 18;

    public DebugOptionsMenu() {
        Window window = Minecraft.getInstance().getWindow();
        int width = window.getGuiScaledWidth();
        int height = window.getGuiScaledHeight() - ClientizenDebugGUI.TAB_HEIGHT;
        setSize(width, height);
        BooleanOptionButton verboseButton = addButton("verbose", () -> CoreConfiguration.debugVerbose, bool -> CoreConfiguration.debugVerbose = bool);
        addButton("ultra_verbose", () -> CoreConfiguration.debugUltraVerbose, bool -> {
            CoreConfiguration.debugUltraVerbose = bool;
            CoreConfiguration.debugVerbose = bool;
            verboseButton.updateLabel();
        });
        addButton("override", () -> CoreConfiguration.debugOverride, bool -> CoreConfiguration.debugOverride = bool);
        addButton("future_warnings", () -> CoreConfiguration.futureWarningsEnabled, bool -> CoreConfiguration.futureWarningsEnabled = bool);
        addButton("show_sources", () -> CoreConfiguration.debugShowSources, bool -> CoreConfiguration.debugShowSources = bool);
        addButton("trimming", () -> CoreConfiguration.debugShouldTrim, bool -> CoreConfiguration.debugShouldTrim = bool);
        addButton("loading_info", () -> CoreConfiguration.debugLoadingInfo, bool -> CoreConfiguration.debugLoadingInfo = bool);
        addButton("stack_traces", () -> CoreConfiguration.debugStackTraces, bool -> CoreConfiguration.debugStackTraces = bool);
        addButton("extra_info", () -> CoreConfiguration.debugExtraInfo, bool -> CoreConfiguration.debugExtraInfo = bool);
        addButton("script_builder", () -> CoreConfiguration.debugScriptBuilder, bool -> CoreConfiguration.debugScriptBuilder = bool);
        int x = OFFSET, y = -BUTTON_HEIGHT;
        for (WWidget child : children) {
            y += BUTTON_HEIGHT + OFFSET;
            if (y + BUTTON_HEIGHT > height - OFFSET) { // if the button is too low, move to the next column
                y = OFFSET;
                x += BUTTON_WIDTH + OFFSET;
            }
            if (x + BUTTON_WIDTH > width - OFFSET) { // if the button is too far right then the panel is full
                break;
            }
            child.setLocation(x, y);
        }
    }

    public BooleanOptionButton addButton(String buttonId, BooleanSupplier getter, BooleanConsumer setter) {
        BooleanOptionButton button = new BooleanOptionButton(buttonId, getter, setter);
        children.add(button);
        button.setParent(this);
        return button;
    }

    public static class BooleanOptionButton extends WButton {

        public static final Component ON = Component.translatable("options.on").withStyle(ChatFormatting.GREEN);
        public static final Component OFF = Component.translatable("options.off").withStyle(ChatFormatting.RED);

        public Component tooltip;
        public Component prefix;
        public BooleanSupplier getter;
        public BooleanConsumer setter;

        public BooleanOptionButton(String id, BooleanSupplier getter, BooleanConsumer setter) {
            String key = "clientizen.debug.options." + id;
            prefix = Component.translatable(key).append(": ");
            tooltip = Component.translatable(key + ".description");
            this.getter = getter;
            this.setter = setter;
            updateLabel();
            setOnClick(() -> {
                setter.accept(!getter.getAsBoolean());
                updateLabel();
            });
            setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        }

        public void updateLabel() {
            setLabel(prefix.copy().append(getter.getAsBoolean() ? ON : OFF));
        }

        @Override
        public void addTooltip(TooltipBuilder builder) {
            String translated = tooltip.getString();
            if (!CoreUtilities.contains(translated, '\n')) {
                builder.add(tooltip);
                return;
            }
            for (String splitLine : CoreUtilities.split(translated, '\n')) {
                builder.add(Component.literal(splitLine).getVisualOrderText());
            }
        }
    }

}
