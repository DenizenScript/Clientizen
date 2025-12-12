package com.denizenscript.clientizen.debuggui;

import com.mojang.blaze3d.platform.Window;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WTabPanel;
import io.github.cottonmc.cotton.gui.widget.data.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class ClientizenDebugGUI extends LightweightGuiDescription {

    public static final int TAB_HEIGHT = 30; // WTabPanel.TAB_HEIGHT
    public static final Component CONSOLE_TITLE = Component.translatable("clientizen.debug.tab.console").withStyle(ChatFormatting.BOLD);
    public static final Component OPTIONS_TITLE = Component.translatable("clientizen.debug.tab.options").withStyle(ChatFormatting.BOLD);

    public DebugConsole console = new DebugConsole();

    public ClientizenDebugGUI() {
        Window window = Minecraft.getInstance().getWindow();
        WTabPanel mainPanel = new WTabPanel();
        mainPanel.setSize(window.getGuiScaledWidth(), window.getGuiScaledHeight());
        setRootPanel(mainPanel);
        mainPanel.add(console, tab -> tab.title(CONSOLE_TITLE));
        mainPanel.add(new DebugOptionsMenu(), tab -> tab.title(OPTIONS_TITLE));
        mainPanel.validate(this);
    }

    public void onClose() {
        console.onClose();
    }

    public static BackgroundPainter transparent;

    static {
        int color = Color.rgb(256, 0, 0, 0).toRgb();
        // Based on ScreenDrawing#drawGuiPanel(MatrixStack matrices, int x, int y, int width, int height, int panelColor)
        int shadowColor = ScreenDrawing.multiplyColor(color, 0.50f);
        int hilightColor = ScreenDrawing.multiplyColor(color, 1.25f);
        transparent = (matrices, left, top, panel) -> {
            ScreenDrawing.drawGuiPanel(matrices, left, top, panel.getWidth(), panel.getHeight(), shadowColor, color, hilightColor, color);
        };
    }

    @Override
    public void addPainters() {
        rootPanel.setBackgroundPainter(transparent);
    }
}
