package com.denizenscript.clientizen.scripts.containers.gui.elements;

import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import io.github.cottonmc.cotton.gui.widget.WBar;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.Texture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ContainerData;

import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedEnum;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedInt;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedString;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.parseTexture;

public class ProgressBarElement implements GuiScriptContainer.GuiElementParser {

    // <--[language]
    // @name Bar GUI Element
    // @group GUI System
    // @description
    // Progress bars let you display progress out of a custom max value; they have a UI type of "progress_bar".
    //
    // <code>
    // ui_type: progress_bar
    // # The direction the bar moves in as its value increases, required.
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
        ContainerData properties = new ProgressBarPropertyDelegate();
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
            bar.withTooltip(Component.literal(tooltip));
        }
        return bar;
    }

    private static class ProgressBarPropertyDelegate implements ContainerData {

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
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
