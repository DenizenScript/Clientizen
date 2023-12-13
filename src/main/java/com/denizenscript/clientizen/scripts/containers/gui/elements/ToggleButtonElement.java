package com.denizenscript.clientizen.scripts.containers.gui.elements;

import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer;
import com.denizenscript.clientizen.util.impl.ClientizenScriptEntryData;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.queues.ContextSource;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.ScriptUtilities;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import io.github.cottonmc.cotton.gui.widget.WToggleButton;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.Texture;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;

import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getSubPath;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedBoolean;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedString;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getWidgetId;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.parseTexture;

public class ToggleButtonElement implements GuiScriptContainer.GuiElementParser {

    @Override
    public WWidget parse(GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context) {
        WToggleButton toggleButton = new WToggleButton();
        String label = getTaggedString(config, "label", context);
        if (label != null) {
            toggleButton.setLabel(Text.literal(label));
        }
        Texture onTexture = parseTexture(config, "on_texture", context);
        if (onTexture != null) {
            toggleButton.setOnImage(onTexture);
        }
        Texture offTexture = parseTexture(config, "off_texture", context);
        if (offTexture != null) {
            toggleButton.setOffImage(offTexture);
        }
        Texture focuseTexture = parseTexture(config, "focus_texture", context);
        if (focuseTexture != null) {
            toggleButton.setFocusImage(focuseTexture);
        }
        Boolean state = getTaggedBoolean(config, "state", context);
        if (state != null) {
            toggleButton.setToggle(state);
        }
        final List<ScriptEntry> onToggle = container.getEntries(new ClientizenScriptEntryData(), getSubPath(pathToElement, "on_toggle"));
        if (onToggle != null) {
            final ContextSource.SimpleMap contextSource = new ContextSource.SimpleMap();
            final String queueName = getWidgetId(pathToElement) + "_toggled";
            toggleButton.setOnToggle(newValue -> {
                contextSource.contexts = Map.of("new_value", new ElementTag(newValue));
                ScriptUtilities.createAndStartQueueArbitrary(queueName, onToggle, null, contextSource, null);
            });
        }
        return toggleButton;
    }
}
