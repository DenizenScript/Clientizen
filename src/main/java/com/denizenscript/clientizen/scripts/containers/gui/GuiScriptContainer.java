package com.denizenscript.clientizen.scripts.containers.gui;

import com.denizenscript.clientizen.events.ClientizenGuiButtonPressedScriptEvent;
import com.denizenscript.clientizen.objects.ItemTag;
import com.denizenscript.clientizen.util.impl.ClientizenScriptEntryData;
import com.denizenscript.denizencore.objects.ArgumentHelper;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.scripts.ScriptBuilder;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.containers.ScriptContainer;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.tags.TagManager;
import com.denizenscript.denizencore.utilities.ScriptUtilities;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.denizenscript.denizencore.utilities.text.StringHolder;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
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
        TEXT
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
            default -> null;
        };
    }

    public Icon parseIcon(YamlConfiguration config, String id, TagContext context) {
        if (config == null) {
            return null;
        }
        ItemTag item = ItemTag.valueOf(config.getString("item"), context);
        if (item != null) {
            return new ItemIcon(item.getStack());
        }
        String texturePath = getTaggedString(config, "texture", context);
        if (texturePath == null) {
            Debug.echoError("Invalid icon '" + id + "': must have a valid item or texture.");
            return null;
        }
        // TODO: sprite sheet support
        Identifier texture = Identifier.tryParse(texturePath);
        if (texture == null) {
            Debug.echoError("Invalid icon '" + id + "': invalid texture path specified.");
            return null;
        }
        return new TextureIcon(texture);
    }
}
