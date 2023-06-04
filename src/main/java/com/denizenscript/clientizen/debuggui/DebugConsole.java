package com.denizenscript.clientizen.debuggui;

import com.denizenscript.clientizen.mixin.gui.WScrollPanelAccessor;
import com.denizenscript.clientizen.mixin.gui.WTextAccessor;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WScrollBar;
import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import io.github.cottonmc.cotton.gui.widget.WText;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.Color;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class DebugConsole extends WScrollPanel {

    public static StringBuilder debugText = new StringBuilder();

    static {
        ClientLoginConnectionEvents.DISCONNECT.register((handler, client) -> {
            debugText = new StringBuilder();
        });
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

    public MinecraftClient client = MinecraftClient.getInstance();

    public DebugConsole() {
        super(new WPlainPanel());
        Window window = client.getWindow();
        setScrollingHorizontally(TriState.FALSE);
        setBackgroundPainter(BackgroundPainter.createColorful(Color.BLACK.toRgb()));
        setSize(window.getScaledWidth(), window.getScaledHeight() - ClientizenDebugGUI.TAB_HEIGHT);

        children.remove(verticalScrollBar);
        verticalScrollBar = new CustomScrollBar(Axis.VERTICAL, 20);
        verticalScrollBar.setParent(this);
        children.add(verticalScrollBar);

        WPlainPanel heldPanel = (WPlainPanel) ((WScrollPanelAccessor) this).getWidget();
        heldPanel.setInsets(new Insets(5, 5, 0, 0));
        textArea = new ConsoleTextArea(debugText.toString());
        heldPanel.setSize(window.getScaledWidth(), textArea.getHeight());
        heldPanel.add(textArea, 0, 0);
    }

    public void onClose() {
        textArea = null;
    }

    public static class CustomScrollBar extends WScrollBar {

        public int scrollSpeed;

        public CustomScrollBar(Axis axis, int scrollSpeed) {
            super(axis);
            this.scrollSpeed = scrollSpeed;
        }

        @Override
        public InputResult onMouseScroll(int x, int y, double amount) { // Same as super method, but uses a custom scrolling speed
            setValue(getValue() + (int) -amount * scrollSpeed);
            return InputResult.PROCESSED;
        }
    }

    public class ConsoleTextArea extends WText {

        public WTextAccessor accessor = (WTextAccessor) this;

        public ConsoleTextArea(String debug) {
            super(Text.literal(debug));
            accessor.setWrappedLines(new ArrayList<>(client.textRenderer.wrapLines(text, client.getWindow().getScaledWidth())));
            updateSize(false);
        }

        public void addLine(String line) {
            accessor.getWrappedLines().addAll(client.textRenderer.wrapLines(Text.literal(line), client.getWindow().getScaledWidth()));
            updateSize(true);
        }

        public void updateSize(boolean updateScroll) {
            setSize(width, client.textRenderer.fontHeight * accessor.getWrappedLines().size());
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
