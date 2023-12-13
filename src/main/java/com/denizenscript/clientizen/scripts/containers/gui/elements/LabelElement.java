package com.denizenscript.clientizen.scripts.containers.gui.elements;

import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer;
import com.denizenscript.denizencore.objects.core.ColorTag;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment;
import net.minecraft.text.Text;

import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.*;

public class LabelElement implements GuiScriptContainer.GuiElementParser {

    @Override
    public WWidget parse(GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context) {
        String text = getTaggedString(config, "text", context);
        if (text == null) {
            Debug.echoError("must have text.");
            return null;
        }
        WLabel label = new WLabel(Text.literal(text));
        VerticalAlignment verticalAlignment = getTaggedEnum(VerticalAlignment.class, config, "vertical_alignment", context);
        if (verticalAlignment != null) {
            label.setVerticalAlignment(verticalAlignment);
        }
        HorizontalAlignment horizontalAlignment = getTaggedEnum(HorizontalAlignment.class, config, "horizontal_alignment", context);
        if (horizontalAlignment != null) {
            label.setHorizontalAlignment(horizontalAlignment);
        }
        ColorTag color = getTaggedObject(ColorTag.class, config, "color", context);
        if (color != null) {
            label.setColor(color.asRGB());
        }
        return label;
    }
}
