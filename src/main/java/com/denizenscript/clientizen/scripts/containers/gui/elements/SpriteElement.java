package com.denizenscript.clientizen.scripts.containers.gui.elements;

import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer;
import com.denizenscript.denizencore.objects.core.ColorTag;
import com.denizenscript.denizencore.objects.core.DurationTag;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.denizenscript.denizencore.utilities.text.StringHolder;
import io.github.cottonmc.cotton.gui.widget.WSprite;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.Texture;

import java.util.ArrayList;
import java.util.List;

import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedObject;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.parseTexture;

public class SpriteElement implements GuiScriptContainer.GuiElementParser {

    // <--[language]
    // @name Sprite GUI Element
    // @group GUI System
    // @description
    // Sprites display a <@link language GUI Textures>, optionally taking a list and cycling between them; they have a UI type of "sprite".
    //
    // <code>
    // ui_type: sprite
    // # A single texture to display, optional (must specify either this or a list of frames).
    // texture: <GUI Texture>
    // # A list of frames to display, optional (must specify either this or a single texture).
    // frames:
    //     <key>: <GUI Texture>
    // # The duration to display each frame for, required when specifying frames.
    // frame_duration: <DurationTag>
    // # A tint to apply onto each texture shown by the sprite element, optional.
    // tint: <ColorTag>
    // </code>
    // -->

    @Override
    public WWidget parse(GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context) {
        ColorTag tint = getTaggedObject(ColorTag.class, config, "tint", context);
        Texture texture = parseTexture(config, "texture", context);
        if (texture != null) {
            return applyTint(new WSprite(texture), tint);
        }
        DurationTag frameDuration = getTaggedObject(DurationTag.class, config, "frame_duration", context);
        if (frameDuration == null) {
            Debug.echoError("Must specify a frame duration.");
            return null;
        }
        YamlConfiguration frames = config.getConfigurationSection("frames");
        if (frames == null) {
            Debug.echoError("Must specify a single texture or frames for an animation.");
            return null;
        }
        List<Texture> frameTextures = new ArrayList<>(frames.contents.size());
        for (StringHolder frameIdHolder : frames.contents.keySet()) {
            Texture frame = parseTexture(frames, frameIdHolder.low, context);
            if (frame != null) {
                frameTextures.add(frame);
            }
        }
        return applyTint(new WSprite((int) frameDuration.getMillis(), frameTextures.toArray(new Texture[0])), tint);
    }

    private WSprite applyTint(WSprite sprite, ColorTag tint) {
        return tint != null ? sprite.setTint(tint.asARGB()) : sprite;
    }
}
