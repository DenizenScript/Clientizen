package com.denizenscript.clientizen.scripts.containers.gui;

import com.denizenscript.clientizen.objects.ItemTag;
import com.denizenscript.clientizen.scripts.containers.gui.elements.*;
import com.denizenscript.denizencore.exceptions.InvalidArgumentsRuntimeException;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.scripts.ScriptBuilder;
import com.denizenscript.denizencore.scripts.ScriptRegistry;
import com.denizenscript.denizencore.scripts.containers.ScriptContainer;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.tags.TagManager;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.denizenscript.denizencore.utilities.debugging.DebugInternals;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.cottonmc.cotton.gui.widget.icon.Icon;
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon;
import io.github.cottonmc.cotton.gui.widget.icon.TextureIcon;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class GuiScriptContainer extends ScriptContainer {

    static {
        guiElementParsers = new HashMap<>();
        registerGuiElement("plain_panel", new PlainPanelElement());
        registerGuiElement("tab_panel", new TabPanelElement());
        registerGuiElement("scroll_panel", new ScrollPanelElement());
        registerGuiElement("button", new ButtonElement());
        registerGuiElement("text", new TextElement());
        registerGuiElement("label", new LabelElement());
        registerGuiElement("dynamic_label", new DynamicLabelElement());
    }

    private static final Map<String, GuiElementParser> guiElementParsers;

    public static void registerGuiElement(String typeName, GuiElementParser parser) {
        if (guiElementParsers.putIfAbsent(typeName, parser) != null) {
            throw new InvalidArgumentsRuntimeException("A GUI element with type '" + typeName + "' is already registered.");
        }
    }

    public GuiScriptContainer(YamlConfiguration configurationSection, String scriptContainerName) {
        super(configurationSection, scriptContainerName);
    }

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

    public static Integer getTaggedInt(YamlConfiguration config, String pathToInt, String path, TagContext context) {
        return getTaggedInt(config, pathToInt, path, null, context);
    }

    public static Integer getTaggedInt(YamlConfiguration config, String pathToInt, String path, Integer defaultValue, TagContext context) {
        String str = getTaggedString(config, path, context);
        if (str == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(str);
        }
        catch (NumberFormatException numberFormatException) {
            Debug.echoError("Invalid number at '" + pathToInt + '.' + path + "': " + str + '.');
            return null;
        }
    }

    public static <T extends ObjectTag> T getTaggedObject(Class<T> objectType, YamlConfiguration config, String pathToObject, String path, TagContext context) {
        String str = config.getString(path);
        if (str == null) {
            return null;
        }
        T converted = TagManager.tagObject(str, context).asType(objectType, context);
        if (converted == null) {
            Debug.echoError(context, "Invalid " + DebugInternals.getClassNameOpti(objectType) + " specified at '" + pathToObject + '.' + path + "': " + str + '.');
            return null;
        }
        return converted;
    }

    public static List<String> getTaggedStringList(YamlConfiguration config, String path, TagContext context) {
        List<String> stringList = config.getStringList(path);
        if (stringList == null) {
            return null;
        }
        List<String> taggedList = new ArrayList<>(stringList.size());
        for (String str : stringList) {
            taggedList.add(TagManager.tag(ScriptBuilder.stripLinePrefix(str), context));
        }
        return taggedList;
    }

    public static <T extends Enum<T>> T getEnum(Class<T> enumClass, YamlConfiguration config, String pathToEnum, String path, TagContext context) {
        String str = getTaggedString(config, path, context);
        if (str == null) {
            return null;
        }
        T converted = ElementTag.asEnum(enumClass, str);
        if (converted == null) {
            Debug.echoError(context, "Invalid '" + pathToEnum + '.' + path + "' value: must be one of " + Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).collect(Collectors.joining(", ")) + '.');
            return null;
        }
        return converted;
    }

    public WPanel createGUIRoot(TagContext context) {
        if (!(createGUI(context) instanceof WPanel rootPanel)) {
            Debug.echoError(context, "Invalid GUI script '" + getName() + "': must have a panel as the root element.");
            return null;
        }
        return rootPanel;
    }

    public WWidget createGUI(TagContext context) {
        return parseGUIWidget(getContents(), "", getName(), context);
    }

    public WWidget parseGUIWidget(YamlConfiguration config, String key, String pathToWidgetConfig, TagContext context) {
        if (config == null) {
            return null;
        }
        String pathToWidget = key.isEmpty() ? pathToWidgetConfig : pathToWidgetConfig + '.' + key;
        YamlConfiguration widgetConfig = config.getConfigurationSection(key);
        if (widgetConfig == null) {
            String guiScriptName = config.getString(key);
            GuiScriptContainer guiScript = ScriptRegistry.getScriptContainerAs(guiScriptName, GuiScriptContainer.class);
            if (guiScript == null) {
                Debug.echoError("Invalid GUI script container specified for GUI element '" + pathToWidget + "': " + guiScriptName + '.');
                return null;
            }
            return guiScript.createGUI(context);
        }
        String uiType = widgetConfig.getString("ui_type");
        if (uiType == null) {
            Debug.echoError("Invalid GUI element '" + pathToWidget + "' is missing a type!");
            return null;
        }
        GuiElementParser parser = guiElementParsers.get(CoreUtilities.toLowerCase(uiType));
        if (parser == null) {
            Debug.echoError("Invalid type specified for GUI element '" + pathToWidget + "': " + uiType + '.');
            return null;
        }
        Integer width = getTaggedInt(widgetConfig, pathToWidget, "width", 18, context), height = getTaggedInt(widgetConfig, pathToWidget, "height", 18, context);
        if (width == null || height == null) {
            Debug.echoError(context, "Invalid GUI element '" + pathToWidget + "': must have valid width and height.");
            return null;
        }
        Integer x = getTaggedInt(widgetConfig, pathToWidget, "x", 0, context), y = getTaggedInt(widgetConfig, pathToWidget, "y", 0, context);
        if (x == null || y == null) {
            Debug.echoError(context, "Invalid GUI element '" + pathToWidget + "': must have valid x and y values.");
            return null;
        }
        WWidget widget = parser.parse(this, widgetConfig, pathToWidget, context);
        if (widget == null) {
            return null;
        }
        widget.setLocation(x, y);
        widget.setSize(width, height);
        return widget;
    }

    public static void applyInsets(YamlConfiguration config, String pathToElement, Consumer<Insets> setter, TagContext context) {
        Insets insets = parseInsets(config.getConfigurationSection("insets"), pathToElement + ".insets", context);
        if (insets != null) {
            setter.accept(insets);
        }
    }

    public static Icon parseIcon(YamlConfiguration config, String id, TagContext context) {
        if (config == null) {
            return null;
        }
        ItemTag item = getTaggedObject(ItemTag.class, config, id, "item", context);
        if (item != null) {
            return new ItemIcon(item.getStack());
        }
        String texturePath = getTaggedString(config, "texture", context);
        if (texturePath == null) {
            Debug.echoError(context, "Invalid icon '" + id + "': must have a valid item or texture.");
            return null;
        }
        // TODO: sprite sheet support
        Identifier texture = Identifier.tryParse(texturePath);
        if (texture == null) {
            Debug.echoError(context, "Invalid icon '" + id + "': invalid texture path specified.");
            return null;
        }
        return new TextureIcon(texture);
    }

    public static Insets parseInsets(YamlConfiguration config, String id, TagContext context) {
        if (config == null) {
            return null;
        }
        Integer all = getTaggedInt(config, id, "all", context);
        if (all != null) {
            return new Insets(all);
        }
        Integer top = getTaggedInt(config, id, "top", context),
                left = getTaggedInt(config, id, "left", context),
                bottom = getTaggedInt(config, id, "bottom", context),
                right = getTaggedInt(config, id, "right", context);
        if (top == null || left == null || bottom == null || right == null) {
            Debug.echoError(context, "Invalid insets at '" + id + "': must have top/left/bottom/right values.");
            return null;
        }
        return new Insets(top, left, bottom, right);
    }
}
