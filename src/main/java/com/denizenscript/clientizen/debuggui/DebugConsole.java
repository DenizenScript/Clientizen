package com.denizenscript.clientizen.debuggui;

import com.denizenscript.clientizen.Clientizen;
import com.denizenscript.clientizen.mixin.gui.WScrollPanelAccessor;
import com.denizenscript.clientizen.mixin.gui.WTextAccessor;
import com.mojang.blaze3d.platform.Window;
import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import io.github.cottonmc.cotton.gui.widget.WText;
import io.github.cottonmc.cotton.gui.widget.data.Color;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

public class DebugConsole extends WScrollPanel {

    public static StringBuilder debugText = new StringBuilder();

    static {
        Clientizen.SYNC_DISCONNECT.register(() -> debugText = new StringBuilder());
    }

    public static ConsoleTextArea textArea;

    public static void addDebug(String debug) {
        if (!debugText.isEmpty()) {
            debugText.append('\n').append(debug);
        }
        else {
            debugText.append(debug);
        }
        if (textArea != null) {
            textArea.addLine(debug);
        }
    }

    public Minecraft client = Minecraft.getInstance();

    public DebugConsole() {
        super(new WPlainPanel());
        Window window = client.getWindow();
        setScrollingHorizontally(TriState.FALSE);
        setBackgroundPainter(BackgroundPainter.createColorful(Color.BLACK.toRgb()));
        setSize(window.getGuiScaledWidth(), window.getGuiScaledHeight() - ClientizenDebugGUI.TAB_HEIGHT);
        verticalScrollBar.setScrollingSpeed(20);

        WPlainPanel heldPanel = (WPlainPanel) ((WScrollPanelAccessor) this).getWidget();
        heldPanel.setInsets(new Insets(5, 5, 0, 0));
        textArea = new ConsoleTextArea(debugText.toString());
        heldPanel.setSize(window.getGuiScaledWidth(), textArea.getHeight());
        heldPanel.add(textArea, 0, 0);
    }

    public void onClose() {
        textArea = null;
    }

    @Override
    public void validate(GuiDescription description) {
        super.validate(description);
        verticalScrollBar.setValue(verticalScrollBar.getMaxScrollValue());
    }

    public class ConsoleTextArea extends WText {

        public WTextAccessor accessor = (WTextAccessor) this;

        public ConsoleTextArea(String debug) {
            super(Component.literal(debug));
            accessor.setWrappedLines(new ArrayList<>(client.font.split(text, client.getWindow().getGuiScaledWidth())));
            updateSize(false);
        }

        public void addLine(String line) {
            accessor.getWrappedLines().addAll(client.font.split(Component.literal(line), client.getWindow().getGuiScaledWidth()));
            updateSize(true);
        }

        public void updateSize(boolean updateScroll) {
            setSize(width, client.font.lineHeight * accessor.getWrappedLines().size());
            if (updateScroll) {
                boolean wasAtBottom = verticalScrollBar.getValue() == verticalScrollBar.getMaxScrollValue();
                DebugConsole.this.layout(); // update the scroll panel to resize the scroll bar
                if (wasAtBottom) {
                    verticalScrollBar.setValue(verticalScrollBar.getMaxScrollValue());
                }
            }
            accessor.setWrappingScheduled(false);
        }

        @Override
        public boolean canResize() {
            return false;
        }

    }

}
