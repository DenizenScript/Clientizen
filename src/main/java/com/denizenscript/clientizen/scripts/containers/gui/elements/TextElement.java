package com.denizenscript.clientizen.scripts.containers.gui.elements;

import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer;
import com.denizenscript.denizencore.objects.core.ColorTag;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import io.github.cottonmc.cotton.gui.widget.WText;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.minecraft.text.Text;

import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.*;

public class TextElement implements GuiScriptContainer.GuiElementParser {

    @Override
    public WWidget parse(GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context) {
        String textContent = getTaggedString(config, "text", context);
        if (textContent == null) {
            Debug.echoError("must have text.");
            return null;
        }
        WText text = new WText(Text.literal(textContent));
        ColorTag color = getTaggedObject(ColorTag.class, config, "color", context);
        if (color != null) {
            text.setColor(color.asRGB());
        }
        return text;
    }
}
