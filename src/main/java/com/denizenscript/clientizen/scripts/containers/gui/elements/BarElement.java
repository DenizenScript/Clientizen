package com.denizenscript.clientizen.scripts.containers.gui.elements;

import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import io.github.cottonmc.cotton.gui.widget.WBar;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.Texture;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.text.Text;

import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedEnum;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedInt;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedString;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.parseTexture;

public class BarElement implements GuiScriptContainer.GuiElementParser {

    // <--[language]
    // @name Bar GUI Element
    // @group GUI System
    // @description
    // Bars are GUI elements that work as a progress bar, with the UI type "bar".
    //
    // <code>
    // ui_type: bar
    // # The direction a bar moves in as its value increases, required.
    // direction: UP/RIGHT/DOWN/LEFT
    // # The texture used for the bar's background, optional.
    // background: <GUI Texture>
    // # The texture used for the bar's progress meter, optional.
    // bar: <GUI Texture>
    // # The amount of progress the bar made out of its max progress, optional.
    // value: <number>
    // # The max value a bar can have, a bar's value is counting up towards this value, optional.
    // max_value: <number>
    // # A tooltip to be shown when hovering over the bar.
    // tooltip: <text>
    // </code>
    // -->

    public static final int VALUE_INDEX = 0, MAX_VALUE_INDEX = 1;

    @Override
    public WWidget parse(GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context) {
        WBar.Direction direction = getTaggedEnum(WBar.Direction.class, config, "direction", context);
        if (direction == null) {
            Debug.echoError("Must specify a direction.");
            return null;
        }
        Texture backgroundTexture = parseTexture(config, "background", context);
        Texture barTexture = parseTexture(config, "bar", context);
        WBar bar = new WBar(backgroundTexture, barTexture, VALUE_INDEX, MAX_VALUE_INDEX, direction);
        PropertyDelegate properties = new BarPropertyDelegate();
        bar.setProperties(properties);
        Integer value = getTaggedInt(config, "value", context);
        if (value != null) {
            properties.set(VALUE_INDEX, value);
        }
        Integer maxValue = getTaggedInt(config, "max_value", context);
        if (maxValue != null) {
            properties.set(MAX_VALUE_INDEX, maxValue);
        }
        String tooltip = getTaggedString(config, "tooltip", context);
        if (tooltip != null) {
            bar.withTooltip(Text.literal(tooltip));
        }
        return bar;
    }

    private static class BarPropertyDelegate implements PropertyDelegate {

        int value;
        int maxValue;

        @Override
        public int get(int index) {
            return switch (index) {
                case VALUE_INDEX -> value;
                case MAX_VALUE_INDEX -> maxValue;
                default -> throw new IllegalArgumentException("Invalid index: " + index);
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case VALUE_INDEX -> this.value = value;
                case MAX_VALUE_INDEX -> this.maxValue = value;
                default -> throw new IllegalArgumentException("Invalid index: " + index);
            };
        }

        @Override
        public int size() {
            return 2;
        }
    }
}
