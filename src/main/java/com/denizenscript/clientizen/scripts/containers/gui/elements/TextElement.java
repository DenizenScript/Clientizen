package com.denizenscript.clientizen.scripts.containers.gui.elements;

import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer;
import com.denizenscript.denizencore.objects.core.ColorTag;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import io.github.cottonmc.cotton.gui.widget.WText;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.minecraft.text.Text;

public class TextElement implements GuiScriptContainer.GuiElementParser {

    @Override
    public WWidget parse(GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context) {
        String textContent = GuiScriptContainer.getTaggedString(config, "text", context);
        if (textContent == null) {
            Debug.echoError(context, "Invalid text element '" + pathToElement + "': must have text.");
            return null;
        }
        WText text = new WText(Text.literal(textContent));
        ColorTag color = GuiScriptContainer.getTaggedObject(ColorTag.class, config, pathToElement, "color", context);
        if (color != null) {
            text.setColor(color.asRGB());
        }
        return text;
    }
}
