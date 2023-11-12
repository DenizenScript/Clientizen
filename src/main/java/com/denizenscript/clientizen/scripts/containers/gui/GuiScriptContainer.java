package com.denizenscript.clientizen.scripts.containers.gui;

import com.denizenscript.clientizen.events.ClientizenGuiButtonPressedScriptEvent;
import com.denizenscript.clientizen.objects.ItemTag;
import com.denizenscript.clientizen.tags.ClientizenTagContext;
import com.denizenscript.clientizen.util.impl.ClientizenScriptEntryData;
import com.denizenscript.denizencore.objects.ArgumentHelper;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ColorTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.scripts.ScriptBuilder;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.containers.ScriptContainer;
import com.denizenscript.denizencore.tags.ParseableTag;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.tags.TagManager;
import com.denizenscript.denizencore.utilities.ScriptUtilities;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.denizenscript.denizencore.utilities.debugging.DebugInternals;
import com.denizenscript.denizencore.utilities.text.StringHolder;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment;
import io.github.cottonmc.cotton.gui.widget.icon.Icon;
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon;
import io.github.cottonmc.cotton.gui.widget.icon.TextureIcon;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GuiScriptContainer extends ScriptContainer {

    enum GUIPartType {
        PLAIN_PANEL,
        TAB_PANEL,
        SCROLL_PANEL,
        BUTTON,
        TEXT,
        LABEL,
        DYNAMIC_LABEL
    }

    public GuiScriptContainer(YamlConfiguration configurationSection, String scriptContainerName) {
        super(configurationSection, scriptContainerName);
    }

    public String getTaggedString(YamlConfiguration config, String path, TagContext context) {
        return getTaggedString(config, path, null, context);
    }

    public String getTaggedString(YamlConfiguration config, String path, String defaultValue, TagContext context) {
        String str = config.getString(path);
        return str != null ? TagManager.tag(str, context) : defaultValue;
    }

    public Integer getTaggedInt(YamlConfiguration config, String id, String path, TagContext context) {
        return getTaggedInt(config, id, path, null, context);
    }

    public Integer getTaggedInt(YamlConfiguration config, String id, String path, Integer defaultValue, TagContext context) {
        String str = getTaggedString(config, path, context);
        if (str == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(str);
        }
        catch (NumberFormatException numberFormatException) {
            Debug.echoError("Invalid number at '" + id + '.' + path + "': " + str + '.');
            return null;
        }
    }

    public <T extends ObjectTag> T getTaggedObject(Class<T> objectType, YamlConfiguration config, String id, String path, TagContext context) {
        String str = config.getString(path);
        if (str == null) {
            return null;
        }
        T converted = TagManager.tagObject(str, context).asType(objectType, context);
        if (converted == null) {
            Debug.echoError(context, "Invalid " + DebugInternals.getClassNameOpti(objectType) + " specified at '" + id + '.' + path + "': " + str + '.');
            return null;
        }
        return converted;
    }

    public List<String> getTaggedStringList(YamlConfiguration config, String path, TagContext context) {
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

    public <T extends Enum<T>> T getEnum(Class<T> enumClass, YamlConfiguration config, String id, String path, TagContext context) {
        String str = getTaggedString(config, path, context);
        if (str == null) {
            return null;
        }
        T converted = ElementTag.asEnum(enumClass, str);
        if (converted == null) {
            Debug.echoError(context, "Invalid '" + id + '.' + path + "' value: must be one of " + Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).collect(Collectors.joining(", ")) + '.');
            return null;
        }
        return converted;
    }

    public WPanel createGUI(TagContext context) {
        WWidget root = parseGUIWidget(getContents(), getName(), context);
        if (!(root instanceof WPanel rootPanel)) {
            Debug.echoError(context, "Invalid GUI script '" + getName() + "': must have a panel as the root element.");
            return null;
        }
        return rootPanel;
    }

    public WWidget parseGUIWidget(YamlConfiguration config, String id, TagContext context) {
        if (!config.contains("ui_type")) {
            Debug.echoError("Invalid GUI element '" + id + "' is missing a type!");
            return null;
        }
        GUIPartType type = ElementTag.asEnum(GUIPartType.class, config.getString("ui_type"));
        String widthStr = getTaggedString(config, "width", context), heightStr = getTaggedString(config, "height", context);
        if (widthStr == null || heightStr == null || !ArgumentHelper.matchesInteger(widthStr) || !ArgumentHelper.matchesInteger(heightStr)) {
            Debug.echoError(context, "Invalid GUI element '" + id + "': must have valid width and height.");
            return null;
        }
        int width = Integer.parseInt(widthStr), height = Integer.parseInt(heightStr);
        String xStr = getTaggedString(config, "x", "0", context), yStr = getTaggedString(config, "y", "0", context);
        if (!ArgumentHelper.matchesInteger(xStr) || !ArgumentHelper.matchesInteger(yStr)) {
            Debug.echoError(context, "Invalid GUI element '" + id + "': must have valid x and y values.");
            return null;
        }
        int x = Integer.parseInt(xStr), y = Integer.parseInt(yStr);
        return switch (type) {
            case PLAIN_PANEL -> {
                WPlainPanel plainPanel = new WPlainPanel();
                plainPanel.setLocation(x, y);
                plainPanel.setSize(width, height);
                Insets insets = parseInsets(config.getConfigurationSection("insets"), id + ".insets", context);
                if (insets != null) {
                    plainPanel.setInsets(insets);
                }
                YamlConfiguration children = config.getConfigurationSection("children");
                if (children == null) {
                    yield plainPanel;
                }
                for (StringHolder childIdHolder : children.contents.keySet()) {
                    YamlConfiguration childConfig = children.getConfigurationSection(childIdHolder.str);
                    if (childConfig == null) {
                        Debug.echoError("Invalid GUI element '" + childIdHolder + "' in plain panel '" + id + "': no options/config found.");
                        continue;
                    }
                    WWidget child = parseGUIWidget(childConfig, id + ".children." + childIdHolder, context);
                    if (child != null) {
                        plainPanel.add(child, child.getX(), child.getY(), child.getWidth(), child.getHeight());
                    }
                }
                yield plainPanel;
            }
            case TAB_PANEL -> {
                WTabPanel tabPanel = new WTabPanel();
                tabPanel.setLocation(x, y);
                tabPanel.setSize(width, height);
                YamlConfiguration tabsConfig = config.getConfigurationSection("tabs");
                if (tabsConfig == null) {
                    yield tabPanel;
                }
                for (StringHolder tabIdHolder : tabsConfig.contents.keySet()) {
                    String tabId = tabIdHolder.str;
                    YamlConfiguration tabConfig = tabsConfig.getConfigurationSection(tabId);
                    if (tabConfig == null) {
                        Debug.echoError(context, "Invalid tab '" + tabId + "' in tab panel '" + id + "': no options/config found.");
                        continue;
                    }
                    WWidget content = tabConfig.contains("content") ? parseGUIWidget(tabConfig.getConfigurationSection("content"), id + ".tabs." + tabId + ".content", context) : null;
                    if (content == null) {
                        Debug.echoError(context, "Invalid tab '" + tabId + "' in tab panel '" + id + "': must have valid content.");
                        continue;
                    }
                    WTabPanel.Tab.Builder tabBuilder = new WTabPanel.Tab.Builder(content);
                    if (tabConfig.contains("title")) {
                        tabBuilder.title(Text.literal(getTaggedString(tabConfig, "title", context)));
                    }
                    if (tabConfig.contains("tooltip")) {
                        tabBuilder.tooltip(getTaggedStringList(tabConfig, "tooltip", context).stream().map(Text::literal).toArray(Text[]::new));
                    }
                    if (tabConfig.contains("icon")) {
                        Icon icon = parseIcon(tabConfig.getConfigurationSection("icon"), tabId, context);
                        if (icon != null) {
                            tabBuilder.icon(icon);
                        }
                    }
                    tabPanel.add(tabBuilder.build());
                }
                yield tabPanel;
            }
            case SCROLL_PANEL -> {
                WWidget content = config.contains("content") ? parseGUIWidget(config.getConfigurationSection("content"), id + ".content", context) : null;
                if (content == null) {
                    Debug.echoError(context, "Invalid scroll panel '" + id + "': must have valid content.");
                    yield null;
                }
                WScrollPanel scrollPanel = new WScrollPanel(content);
                scrollPanel.setLocation(x, y);
                scrollPanel.setSize(width, height);
                TriState verticalScroll = getEnum(TriState.class, config, id, "vertical_scroll", context);
                TriState horizontalScroll = getEnum(TriState.class, config, id, "horizontal_scroll", context);
                if (verticalScroll != null) {
                    scrollPanel.setScrollingVertically(verticalScroll);
                }
                if (horizontalScroll != null) {
                    scrollPanel.setScrollingHorizontally(horizontalScroll);
                }
                yield scrollPanel;
            }
            case BUTTON -> {
                String label = config.getString("label");
                Icon icon = parseIcon(config.getConfigurationSection("icon"), id + ".icon", context);
                WButton button = new WButton(icon, label != null ? Text.literal(label) : null);
                button.setLocation(x, y);
                button.setSize(width, height);
                HorizontalAlignment textAlignment = getEnum(HorizontalAlignment.class, config, id, "text_alignment", context);
                if (textAlignment != null) {
                    button.setAlignment(textAlignment);
                }
                List<ScriptEntry> onClick = getEntries(new ClientizenScriptEntryData(), id.substring(id.indexOf('.') + 1) + ".on_click");
                button.setOnClick(() -> {
                    if (onClick != null) {
                        ScriptUtilities.createAndStartQueueArbitrary(id.substring(id.lastIndexOf('.') + 1) + "_pressed", onClick, null, null, null);
                    }
                    ClientizenGuiButtonPressedScriptEvent.instance.handleButtonPress(id);
                });
                yield button;
            }
            case TEXT -> {
                String textContent = getTaggedString(config, "text", context);
                if (textContent == null) {
                    Debug.echoError(context, "Invalid text element '" + id + "': must have text.");
                    yield null;
                }
                WText text = new WText(Text.literal(textContent));
                text.setLocation(x, y);
                text.setSize(width, height);
                ColorTag color = getTaggedObject(ColorTag.class, config, id, "color", context);
                if (color != null) {
                    text.setColor(color.asRGB());
                }
                yield text;
            }
            case LABEL -> {
                String text = getTaggedString(config, "text", context);
                if (text == null) {
                    Debug.echoError(context, "Invalid label element '" + id + "': must have text.");
                    yield null;
                }
                WLabel label = new WLabel(Text.literal(text));
                label.setLocation(x, y);
                label.setSize(width, height);
                VerticalAlignment verticalAlignment = getEnum(VerticalAlignment.class, config, id, "vertical_alignment", context);
                if (verticalAlignment != null) {
                    label.setVerticalAlignment(verticalAlignment);
                }
                HorizontalAlignment horizontalAlignment = getEnum(HorizontalAlignment.class, config, id, "horizontal_alignment", context);
                if (horizontalAlignment != null) {
                    label.setHorizontalAlignment(horizontalAlignment);
                }
                ColorTag color = getTaggedObject(ColorTag.class, config, id, "color", context);
                if (color != null) {
                    label.setColor(color.asRGB());
                }
                yield label;
            }
            case DYNAMIC_LABEL -> {
                String text = config.getString("text");
                if (text == null) {
                    Debug.echoError(context, "Invalid dynamic label element '" + id + "': must have text.");
                    yield null;
                }
                TagContext contextFromScript = new ClientizenTagContext(this);
                ParseableTag parseableTag = TagManager.parseTextToTag(text, contextFromScript);
                WDynamicLabel dynamicLabel = new WDynamicLabel(() -> parseableTag.parse(contextFromScript).toString());
                dynamicLabel.setLocation(x, y);
                dynamicLabel.setSize(width, height);
                HorizontalAlignment horizontalAlignment = getEnum(HorizontalAlignment.class, config, id, "horizontal_alignment", context);
                if (horizontalAlignment != null) {
                    dynamicLabel.setAlignment(horizontalAlignment);
                }
                ColorTag color = getTaggedObject(ColorTag.class, config, id, "color", context);
                if (color != null) {
                    dynamicLabel.setColor(color.asRGB(), WDynamicLabel.DEFAULT_DARKMODE_TEXT_COLOR);
                }
                yield dynamicLabel;
            }
        };
    }

    public Icon parseIcon(YamlConfiguration config, String id, TagContext context) {
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

    public Insets parseInsets(YamlConfiguration config, String id, TagContext context) {
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
