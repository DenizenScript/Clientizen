package com.denizenscript.clientizen.debuggui;

import com.denizenscript.denizencore.utilities.CoreConfiguration;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Color;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.BooleanSupplier;

public class ClientizenDebugGUI extends LightweightGuiDescription {

	public ClientizenDebugGUI() {
		// current size of the screen
		int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
		int height = MinecraftClient.getInstance().getWindow().getScaledHeight();
		// the main panel to hold the entire GUI
		WTabPanel mainPanel = new WTabPanel();
		mainPanel.setSize(width, height);
		setRootPanel(mainPanel);
		// panel to hold the console
		Text debugText = Text.literal(String.join("\n", ClientizenDebugScreen.debug)); // need the debug text to warp and get the size
		int totalTextHeight = MinecraftClient.getInstance().textRenderer.fontHeight * MinecraftClient.getInstance().textRenderer.wrapLines(debugText, width).size();
		WPlainPanel consolePanel = new WPlainPanel();
		consolePanel.setInsets(new Insets(5, 5, 0, 0));
		consolePanel.setSize(width, totalTextHeight);
		// the console text area
		WText debugConsole = new WText(debugText);
		consolePanel.add(debugConsole, 0, 0, width, totalTextHeight);
		// scroll panel for the console
		WScrollPanel scrollPanel = new WScrollPanel(consolePanel);
		scrollPanel.setScrollingHorizontally(TriState.FALSE);
		scrollPanel.setBackgroundPainter(BackgroundPainter.createColorful(Color.BLACK.toRgb()));
		scrollPanel.setSize(width, height - 30);
		mainPanel.add(scrollPanel, tab -> tab.title(Text.literal("Console").formatted(Formatting.BOLD)));

		// debug Options tab
		WPlainPanel debugOptionsPanel = new WPlainPanel();
		debugOptionsPanel.setSize(width, height - 30);
		WButton debugVerboseButton = createToggleButton("Verbose", () -> CoreConfiguration.debugVerbose, bool -> {
			CoreConfiguration.debugVerbose = bool;
			CoreConfiguration.debugUltraVerbose = false;
		});
		WButton debugUltraVerboseButton = createToggleButton("Ultra Verbose", () -> CoreConfiguration.debugUltraVerbose, bool -> {
			CoreConfiguration.debugUltraVerbose = bool;
			CoreConfiguration.debugVerbose = bool;
		});
		WButton debugOverrideButton = createToggleButton("Override", () -> CoreConfiguration.debugOverride, bool -> CoreConfiguration.debugOverride = bool);
		WButton futureWarningsEnabledButton = createToggleButton("Future Warnings", () -> CoreConfiguration.futureWarningsEnabled, bool -> CoreConfiguration.futureWarningsEnabled = bool);
		WButton showSourcesButton = createToggleButton("Show Sources", () -> CoreConfiguration.debugShowSources, bool -> CoreConfiguration.debugShowSources = bool);
		WButton shouldTrimDebugButton = createToggleButton("Trimming", () -> CoreConfiguration.debugShouldTrim, bool -> CoreConfiguration.debugShouldTrim = bool);
		WButton debugLoadingInfoButton = createToggleButton("Loading Info", () -> CoreConfiguration.debugLoadingInfo, bool -> CoreConfiguration.debugLoadingInfo = bool);
		WButton debugStackTracesButton = createToggleButton("Stack Traces", () -> CoreConfiguration.debugStackTraces, bool -> CoreConfiguration.debugStackTraces = bool);
		WButton debugExtraInfoButton = createToggleButton("Extra Info", () -> CoreConfiguration.debugExtraInfo, bool -> CoreConfiguration.debugExtraInfo = bool);
		WButton debugScriptBuilderButton = createToggleButton("Script Builder", () -> CoreConfiguration.debugScriptBuilder, bool -> CoreConfiguration.debugScriptBuilder = bool);
		debugOptionsPanel.add(debugVerboseButton,          18, 18, 100, 18);
		debugOptionsPanel.add(debugUltraVerboseButton,     18, 54, 100, 18);
		debugOptionsPanel.add(debugOverrideButton,         18, 90, 100, 18);
		debugOptionsPanel.add(futureWarningsEnabledButton, 18, 126, 100, 18);
		debugOptionsPanel.add(showSourcesButton,           18, 162, 100, 18);
		debugOptionsPanel.add(shouldTrimDebugButton,       136, 18, 100, 18);
		debugOptionsPanel.add(debugLoadingInfoButton,      136, 54, 100, 18);
		debugOptionsPanel.add(debugStackTracesButton,      136, 90, 100, 18);
		debugOptionsPanel.add(debugExtraInfoButton,        136, 126, 100, 18);
		debugOptionsPanel.add(debugScriptBuilderButton,    136, 162, 100, 18);
		mainPanel.add(debugOptionsPanel, tab -> tab.title(Text.literal("Debug Options").formatted(Formatting.BOLD)));

		mainPanel.validate(this);
	}

	public static WButton createToggleButton(String optionName, BooleanSupplier value, BooleanConsumer setter) {
		String prefix = optionName + ": ";
		WButton button = new WButton(Text.literal(prefix + stringify(value.getAsBoolean())));
		button.setOnClick(() -> {
			boolean newValue = !value.getAsBoolean();
			setter.accept(newValue);
			button.setLabel(Text.literal(prefix + stringify(newValue)));
		});
		return button;
	}

	public static String stringify(boolean bool) {
		return bool ? Formatting.GREEN + "ON" : Formatting.RED + "OFF";
	}

	public static BackgroundPainter transparent;

	static {
		int color = Color.rgb(256, 0, 0, 0).toRgb();
		// Based on ScreenDrawing#drawGuiPanel(MatrixStack matrices, int x, int y, int width, int height, int panelColor)
		transparent = (matrices, left, top, panel) -> {
			int shadowColor = ScreenDrawing.multiplyColor(color, 0.50f);
			int hilightColor = ScreenDrawing.multiplyColor(color, 1.25f);
			ScreenDrawing.drawGuiPanel(matrices, left, top, panel.getWidth(), panel.getHeight(), shadowColor, color, hilightColor, color);
		};
	}

	@Override
	public void addPainters() {
		getRootPanel().setBackgroundPainter(transparent);
	}
}
