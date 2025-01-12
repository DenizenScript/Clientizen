package com.denizenscript.clientizen.scripts.commands;

import com.denizenscript.clientizen.objects.DrawContextTag;
import com.denizenscript.denizencore.exceptions.InvalidArgumentsRuntimeException;
import com.denizenscript.denizencore.objects.core.ColorTag;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.scripts.commands.generator.ArgDefaultNull;
import com.denizenscript.denizencore.scripts.commands.generator.ArgDefaultText;
import com.denizenscript.denizencore.scripts.commands.generator.ArgName;
import com.denizenscript.denizencore.scripts.commands.generator.ArgPrefixed;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class Render2DCommand extends AbstractCommand {

    // <--[command]
    // @Name render2d
    // @Syntax render2d [context:<context>] [pixel/rectangle/texture] (text:<text>) (width:<width>) (height:<height>) (filled) [x:<x>] [y:<y>] (color:<color>)
    // @Required 5
    // @Maximum 9
    // @Short Renders 2D object on screen.
    // @Group Render
    //
    // @Description
    // Renders 2D object on screen
    //
    // "context:" - draw context, see <@link ObjectType DrawContextTage>.
    // "x:" and "y:" - the position that should be drawn on.
    //
    // "text:" - a text to draw.
    //
    // "color:" - a <@link ObjectType ColorTag> of the color to draw in.
    //
    // For non-pixel shapes:
    // "width:" and "height:" - the size of the shape being drawn.
    // "filled" - whether the shape should be filled or just a border. optional, defaults to false.
    //
    // @Tags
    // None
    //
    // @Usage
    // Use to draw a purple 100x100 filled rectangle in the top left of a screen.
    // - render2d context:<context.draw_context> rectangle x:0 y:0 width:100 height:100 color:purple filled
    // -->

    public enum Renderable {PIXEL, RECTANGLE, TEXTURE, TEXT}

    public Render2DCommand() {
        setName("render2d");
        setSyntax("render2d [context:<context>] [pixel/rectangle/texture] (text:<text>) (width:<width>) (height:<height>) (filled) [x:<x>] [y:<y>] (color:<color>)");
        setRequiredArguments(5, 9);
        autoCompile();
    }

    public static void autoExecute(@ArgName("context") @ArgPrefixed DrawContextTag context,
                                   @ArgName("type") @ArgDefaultNull Renderable type,
                                   @ArgName("x") @ArgPrefixed int x,
                                   @ArgName("y") @ArgPrefixed int y,
                                   @ArgName("width") @ArgPrefixed @ArgDefaultText("-1") int width,
                                   @ArgName("height") @ArgPrefixed @ArgDefaultText("-1") int height,
                                   @ArgName("text") @ArgPrefixed @ArgDefaultText("") String text,
                                   @ArgName("color") @ArgPrefixed @ArgDefaultNull ColorTag color,
                                   @ArgName("filled") boolean filled) {
        DrawContext drawContext = context.getDrawContext();

        if (color == null) {
            throw new InvalidArgumentsRuntimeException("Must specify a color to draw in.");
        }

        if (!text.isBlank()) {
            type = Renderable.TEXT;
        }

        int rgbColor = color.getAWTColor().getRGB();

        switch (type) {
            case PIXEL -> {
                drawContext.fill(x, y, 1, 1, rgbColor);
            }
            case RECTANGLE -> {
                if (width == -1 || height == -1) {
                    throw new InvalidArgumentsRuntimeException("Must specify a width and height.");
                }

                if (filled) {
                    drawContext.fill(x, y, x + width, y + height, rgbColor);
                } else {
                    drawContext.drawBorder(x, y, x + width, y + height, rgbColor);
                }
            }
            case TEXTURE -> {
                // TODO: Texture tag and drawing stuff
            }
            case TEXT -> {
                MinecraftClient client = MinecraftClient.getInstance();
                drawContext.drawText(client.textRenderer, text, x, y, rgbColor, false);
            }
        }
    }
}
