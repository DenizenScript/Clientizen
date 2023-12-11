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

import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.*;

public class BarElement implements GuiScriptContainer.GuiElementParser {

    public static final int VALUE_INDEX = 0, MAX_VALUE_INDEX = 1;

    @Override
    public WWidget parse(GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context) {
        WBar.Direction direction = getEnum(WBar.Direction.class, config, "direction", context);
        if (direction == null) {
            Debug.echoError("must specify a direction.");
            return null;
        }
        Texture backgroundTexture = parseTexture(config.getConfigurationSection("background"), context);
        Texture barTexture = parseTexture(config.getConfigurationSection("bar"), context);
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
