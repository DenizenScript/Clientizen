package com.denizenscript.clientizen.scripts.containers.gui;

import com.denizenscript.clientizen.objects.ItemTag;
import com.denizenscript.clientizen.scripts.containers.gui.elements.*;
import com.denizenscript.clientizen.tags.ClientizenTagContext;
import com.denizenscript.denizencore.exceptions.InvalidArgumentsRuntimeException;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ColorTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.scripts.ScriptRegistry;
import com.denizenscript.denizencore.scripts.containers.ScriptContainer;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.tags.TagManager;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.denizenscript.denizencore.utilities.debugging.DebugInternals;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.cottonmc.cotton.gui.widget.data.Texture;
import io.github.cottonmc.cotton.gui.widget.icon.Icon;
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon;
import io.github.cottonmc.cotton.gui.widget.icon.TextureIcon;
import net.minecraft.resources.Identifier;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GuiScriptContainer extends ScriptContainer {

    // <--[language]
    // @name GUI Script Containers
    // @group Script Container System
    // @description
    // GUI script containers represent a UI element (such as a button, scroll panel, etc.).
    // They contain a <@link language GUI Element>, and can be opened using <@link command Gui>.
    //
    // <code>
    // # An example of a basic GUI script container, with a plain panel as its element.
    // my_gui:
    //     type: gui
    //     ui_type: plain_panel
    //     width: 200
    //     height: 200
    //     content: <GUI Element>
    // </code>
    // -->

    private static final Map<String, GuiElementParser> guiElementParsers = new HashMap<>();

    static {
        registerGuiElement("plain_panel", new PlainPanelElement());
        registerGuiElement("tab_panel", new TabPanelElement());
        registerGuiElement("scroll_panel", new ScrollPanelElement());
        registerGuiElement("grid_panel", new GridPanelElement());
        registerGuiElement("box_panel", new BoxPanelElement());
        registerGuiElement("button", new ButtonElement());
        registerGuiElement("toggle_button", new ToggleButtonElement());
        registerGuiElement("text", new TextElement());
        registerGuiElement("label", new LabelElement());
        registerGuiElement("dynamic_label", new DynamicLabelElement());
        registerGuiElement("item", new ItemElement());
        registerGuiElement("text_field", new TextFieldElement());
        registerGuiElement("slider", new SliderElement());
        registerGuiElement("labeled_slider", new LabeledSliderElement());
        registerGuiElement("progress_bar", new ProgressBarElement());
        registerGuiElement("sprite", new SpriteElement());
    }

    public static void registerGuiElement(String typeName, GuiElementParser parser) {
        if (guiElementParsers.putIfAbsent(typeName, parser) != null) {
            throw new InvalidArgumentsRuntimeException("A GUI element with type '" + typeName + "' is already registered.");
        }
    }

    public GuiScriptContainer(YamlConfiguration configurationSection, String scriptContainerName) {
        super(configurationSection, scriptContainerName);
    }

    // <--[language]
    // @name GUI Element
    // @group GUI System
    // @description
    // A GUI element is a single object in a GUI.
    // It takes in a required "ui_type" key, which controls its type; see the "GUI System" group on the meta docs for documentation on each type.
    // It also takes in optional "width" and "height" keys to control it's size, and optional "x" and "y" keys to control its position.
    // A GUI element's position is the position of its top left corner, relative to the top left corner of its parent element.
    // "parent element" refers to the GUI element this element is in, for example a button inside a plain panel.
    // -->

    @FunctionalInterface
    public interface GuiElementParser {
        WWidget parse(GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context);
    }

    public static String getTaggedString(YamlConfiguration config, String path, TagContext context) {
        return getTaggedString(config, path, null, context);
    }

    public static String getTaggedString(YamlConfiguration config, String path, String defaultValue, TagContext context) {
        String str = config.getString(path);
        return str != null ? TagManager.tag(str, context) : defaultValue;
    }

    public static Integer getTaggedInt(YamlConfiguration config, String path, TagContext context) {
        return getTaggedInt(config, path, null, context);
    }

    public static Integer getTaggedInt(YamlConfiguration config, String path, Integer defaultValue, TagContext context) {
        String str = getTaggedString(config, path, context);
        if (str == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(str);
        }
        catch (NumberFormatException numberFormatException) {
            Debug.echoError("Invalid integer number specified under '" + path + "': " + str + '.');
            return null;
        }
    }

    public static Float getTaggedFloat(YamlConfiguration config, String path, TagContext context) {
        return getTaggedFloat(config, path, null, context);
    }

    public static Float getTaggedFloat(YamlConfiguration config, String path, Float defaultValue, TagContext context) {
        String str = getTaggedString(config, path, context);
        if (str == null) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(str);
        }
        catch (NumberFormatException numberFormatException) {
            Debug.echoError("Invalid number specified under '" + path + "': " + str + '.');
            return null;
        }
    }

    public static Boolean getTaggedBoolean(YamlConfiguration config, String path, TagContext context) {
        String str = getTaggedString(config, path, context);
        if (str == null) {
            return null;
        }
        boolean equalsTrue = CoreUtilities.equalsIgnoreCase(str, "true");
        if (!equalsTrue && !CoreUtilities.equalsIgnoreCase(str, "false")) {
            Debug.echoError("Invalid boolean '" + str + "' specified under '" + path + "': must be either 'true' or 'false'.");
            return null;
        }
        return equalsTrue;
    }

    public static <T extends ObjectTag> T getTaggedObject(Class<T> objectType, YamlConfiguration config, String path, TagContext context) {
        String str = config.getString(path);
        if (str == null) {
            return null;
        }
        T converted = TagManager.tagObject(str, context).asType(objectType, context);
        if (converted == null) {
            Debug.echoError("Invalid " + DebugInternals.getClassNameOpti(objectType) + " specified under '" + path + "': " + str + '.');
            return null;
        }
        return converted;
    }

    public static <T extends ObjectTag> List<T> getTaggedObjectList(Class<T> objectType, YamlConfiguration config, String path, TagContext context) {
        Object object = config.get(path);
        if (object == null) {
            return null;
        }
        ListTag list = CoreUtilities.objectToTagForm(object, context, true, true, true).asType(ListTag.class, context);
        if (list == null) {
            return null;
        }
        return list.filter(objectType, context);
    }

    public static List<String> getTaggedStringList(YamlConfiguration config, String path, TagContext context) {
        Object object = config.get(path);
        if (object == null) {
            return null;
        }
        return CoreUtilities.objectToTagForm(object, context, true, true, true).asType(ListTag.class, context);
    }

    public static <T extends Enum<T>> T getTaggedEnum(Class<T> enumClass, YamlConfiguration config, String path, TagContext context) {
        return getTaggedEnum(enumClass, null, config, path, context);
    }

    public static <T extends Enum<T>> T getTaggedEnum(Class<T> enumClass, T defaultValue, YamlConfiguration config, String path, TagContext context) {
        String str = getTaggedString(config, path, context);
        if (str == null) {
            return defaultValue;
        }
        T converted = ElementTag.asEnum(enumClass, str);
        if (converted == null) {
            Debug.echoError("Invalid '" + path + "' value '" + str + "': must be one of " + Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).collect(Collectors.joining(", ")) + '.');
            return null;
        }
        return converted;
    }

    public static String getSubPath(String path, String subKey) {
        return path.isEmpty() ? subKey : path + '.' + subKey;
    }

    public static String getWidgetId(String path) {
        return path.substring(path.lastIndexOf('.') + 1);
    }

    public static String getDebugPath(String path) {
        return path.isEmpty() ? "(root element)" : path;
    }

    public static class ContextStringSupplier implements Supplier<String> {

        String context;

        public String set(String newContext) {
            String previousContext = context;
            context = newContext;
            return previousContext;
        }

        @Override
        public String get() {
            return context;
        }
    }

    public static ContextStringSupplier currentContextSupplier = null;

    public WPanel createGUIRoot() {
        currentContextSupplier = new ContextStringSupplier();
        Debug.pushErrorContext(currentContextSupplier);
        WWidget created = createGUI();
        Debug.popErrorContext();
        currentContextSupplier = null;
        if (created == null) {
            return null;
        }
        if (!(created instanceof WPanel rootPanel)) {
            Debug.echoError("Invalid GUI script '" + getOriginalName() + "': must have a panel as the root element.");
            return null;
        }
        return rootPanel;
    }

    public WWidget createGUI() {
        return parseGUIWidget(getContents(), "", "", new ClientizenTagContext(this));
    }

    public WWidget parseGUIWidget(YamlConfiguration config, String key, String pathToWidgetConfig, TagContext context) {
        if (config == null) {
            return null;
        }
        String pathToWidget = key.isEmpty() ? pathToWidgetConfig : pathToWidgetConfig + '.' + key;
        String debugPathToWidget = getDebugPath(pathToWidget);
        YamlConfiguration widgetConfig = config.getConfigurationSection(key);
        if (widgetConfig == null) {
            String guiScriptName = config.getString(key);
            GuiScriptContainer guiScript = ScriptRegistry.getScriptContainerAs(guiScriptName, GuiScriptContainer.class);
            if (guiScript == null) {
                Debug.echoError("Invalid GUI script container specified for GUI element '" + debugPathToWidget + "': " + guiScriptName + '.');
                return null;
            }
            return guiScript.createGUI();
        }
        String uiType = widgetConfig.getString("ui_type");
        if (uiType == null) {
            String previousContext = currentContextSupplier.set(null);
            Debug.echoError(this, "Invalid GUI element '" + debugPathToWidget + "' is missing a type!");
            currentContextSupplier.set(previousContext);
            return null;
        }
        String previousContext = currentContextSupplier.set("while parsing GUI element '<A>" + debugPathToWidget + "<LR>' of type '<A>" + uiType + "<LR>' in script '<A>" + getName() + "<LR>'");
        GuiElementParser parser = guiElementParsers.get(CoreUtilities.toLowerCase(uiType));
        if (parser == null) {
            Debug.echoError("Invalid type specified: " + uiType + '.');
            currentContextSupplier.set(previousContext);
            return null;
        }
        Integer width = getTaggedInt(widgetConfig, "width", 18, context), height = getTaggedInt(widgetConfig, "height", 18, context);
        if (width == null || height == null) {
            Debug.echoError("Invalid width/height specified.");
            currentContextSupplier.set(previousContext);
            return null;
        }
        Integer x = getTaggedInt(widgetConfig, "x", 0, context), y = getTaggedInt(widgetConfig, "y", 0, context);
        if (x == null || y == null) {
            Debug.echoError("Invalid x/y values specified.");
            currentContextSupplier.set(previousContext);
            return null;
        }
        WWidget widget = parser.parse(this, widgetConfig, pathToWidget, context);
        currentContextSupplier.set(previousContext);
        if (widget == null) {
            return null;
        }
        widget.setLocation(x, y);
        widget.setSize(width, height);
        return widget;
    }

    public static void applyInsets(YamlConfiguration config, Consumer<Insets> setter, TagContext context) {
        Insets insets = parseInsets(config.getConfigurationSection("insets"), context);
        if (insets != null) {
            setter.accept(insets);
        }
    }

    // <--[language]
    // @name GUI Insets
    // @group GUI System
    // @description
    // GUI elements that contain other elements sometimes allow setting insets.
    // Insets are a set of distances elements within another element should keep from its edges.
    // So for example, in a plain panel with an inset of 5 on all directions, all elements within it will automatically be at least 5 away from its edges.
    //
    // An example of a basic insets config:
    // <code>
    // # All 4 of these are required, and control the distance from different edges.
    // top: <number>
    // left: <number>
    // bottom: <number>
    // right: <number>
    // # Optionally replace all 4 of these with this single key, which sets a single inset for all edges.
    // all: <number>
    // </code>
    // -->

    public static Insets parseInsets(YamlConfiguration config, TagContext context) {
        if (config == null) {
            return null;
        }
        Integer all = getTaggedInt(config, "all", context);
        if (all != null) {
            return new Insets(all);
        }
        Integer top = getTaggedInt(config, "top", context),
                left = getTaggedInt(config, "left", context),
                bottom = getTaggedInt(config, "bottom", context),
                right = getTaggedInt(config, "right", context);
        if (top == null || left == null || bottom == null || right == null) {
            Debug.echoError("Invalid insets: must have top/left/bottom/right values.");
            return null;
        }
        return new Insets(top, left, bottom, right);
    }

    // <--[language]
    // @name GUI Textures
    // @group GUI System
    // @description
    // GUI Textures directly relate to textures loaded in from resource packs (including the built-in one).
    // They can be a single texture, or a section of a texture from a sprite sheet.
    // Since these use the resources system, you can also add and use custom textures yourself via custom resource packs.
    //
    // Textures can either be a single texture path: minecraft:textures/block/cobblestone.png
    // Or a section of a sprite sheet:
    // <code>
    // # The sprite sheet to use a part of, required.
    // texture: <texture path>
    // # The left value of the top left point of the area to use, required.
    // u: <decimal>
    // # The top value of the top left point of the area to use, required.
    // v: <decimal>
    // # The width of the area to use, required.
    // width: <decimal>
    // # The height of the area to use, required.
    // height: <decimal>
    // </code>
    // All of the UV values (including the width and height) are between 0 and 1 and are relative to the entire image.
    // So for example: 0, 0, 1, 1 (in order) will be the entire image, 0.5, 0.5, 0.5, 0.5 will be the bottom right half, etc.
    // -->

    public static Texture parseTexture(YamlConfiguration config, String path, TagContext context) {
        YamlConfiguration textureConfig = config.getConfigurationSection(path);
        String pathStr = textureConfig != null ? getTaggedString(textureConfig, "texture", context) : getTaggedString(config, path, context);
        if (pathStr == null) {
            if (textureConfig != null) {
                Debug.echoError("Invalid texture: must specify a texture path.");
            }
            return null;
        }
        Identifier texturePath = Identifier.tryParse(pathStr);
        if (texturePath == null) {
            Debug.echoError("Invalid texture: invalid texture path '" + pathStr + "' specified.");
            return null;
        }
        if (textureConfig == null) {
            return new Texture(texturePath);
        }
        Texture.Type type = getTaggedEnum(Texture.Type.class, Texture.Type.STANDALONE, textureConfig, "type", context);
        if (type == null) {
            Debug.echoError("Invalid texture: invalid type specified.");
            return null;
        }
        Float u = getTaggedFloat(textureConfig, "u", context),
                v = getTaggedFloat(textureConfig, "v", context),
                width = getTaggedFloat(textureConfig, "width", context),
                height = getTaggedFloat(textureConfig, "height", context);
        if (u == null || v == null || width == null || height == null) {
            Debug.echoError("Invalid texture: invalid UV coordinates specified.");
            return null;
        }
        return new Texture(texturePath, type, u, v, u + width, v + height);
    }

    // <--[language]
    // @name GUI Icons
    // @group GUI System
    // @description
    // Icons can take in either:
    // A single <@link ObjectType ItemTag>, to have the icon show an item.
    // Or a <@link language GUI Textures>, to have the icon show a specific texture.
    // -->

    public static Icon parseIcon(YamlConfiguration config, String path, TagContext context) {
        ObjectTag itemObject = getTaggedObject(ObjectTag.class, config, path, context);
        if (itemObject == null) { // The config doesn't have that key
            return null;
        }
        ItemTag item = itemObject.asType(ItemTag.class, CoreUtilities.noDebugContext);
        if (item != null) {
            return new ItemIcon(item.getStack());
        }
        Texture texture = parseTexture(config, path, context);
        if (texture == null) {
            Debug.echoError("Invalid icon: must have a valid item or texture.");
            return null;
        }
        return new TextureIcon(texture);
    }

    public static void applyBackgroundPainter(WPanel panel, YamlConfiguration config, TagContext context) {
        BackgroundPainter painter = parseBackgroundPainter(config, "background", context);
        if (painter != null) {
            panel.setBackgroundPainter(painter);
        }
    }

    // <--[language]
    // @name GUI Backgrounds
    // @group GUI System
    // @description
    // Backgrounds can take in a single <@link ObjectType ColorTag>, or a config for a color with a custom contrast between the bright/dark edges:
    // <code>
    // # The color to use for the background, required.
    // color: <ColorTag>
    // # The contrast between the color and the bright/dark edges of the background, required.
    // contrast: <decimal>
    // </code>
    // Alternatively, specify a texture path within the GUI sprite texture atlas, e.g. "minecraft:widget/button" for "minecraft/textures/gui/sprites/widget/button.png".
    // You can also use custom textures added by resource packs, see <@link url https://minecraft.wiki/w/Resource_pack#GUI> for more information.
    // -->

    public static BackgroundPainter parseBackgroundPainter(YamlConfiguration config, String path, TagContext context) {
        ObjectTag rawObject = getTaggedObject(ObjectTag.class, config, path, context);
        if (rawObject == null) { // The config doesn't have that key
            return null;
        }
        ColorTag color = rawObject.asType(ColorTag.class, CoreUtilities.noDebugContext);
        if (color != null) {
            return BackgroundPainter.createColorful(color.asARGB());
        }
        Identifier guiSprite = Identifier.tryParse(rawObject.toString());
        if (guiSprite != null) {
            return BackgroundPainter.createGuiSprite(guiSprite);
        }
        YamlConfiguration painterConfig = config.getConfigurationSection(path);
        if (painterConfig == null) {
            Debug.echoError("Invalid background: must specify a valid single color/texture or a config.");
            return null;
        }
        ColorTag baseColor = getTaggedObject(ColorTag.class, painterConfig, "color", context);
        if (baseColor == null) {
            Debug.echoError("Invalid background: must specify a color.");
            return null;
        }
        Float contrast = getTaggedFloat(painterConfig, "contrast", context);
        if (contrast == null) {
            Debug.echoError("Invalid background: must specify a contrast or use the single color format.");
            return null;
        }
        return BackgroundPainter.createColorful(baseColor.asARGB(), contrast);
    }
}
