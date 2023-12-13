package com.denizenscript.clientizen.scripts.containers.gui.elements;

import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.denizenscript.denizencore.utilities.text.StringHolder;
import io.github.cottonmc.cotton.gui.widget.WTabPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.icon.Icon;
import net.minecraft.text.Text;

import java.util.List;

import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getSubPath;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedString;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedStringList;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.parseIcon;

public class TabPanelElement implements GuiScriptContainer.GuiElementParser {

    @Override
    public WWidget parse(GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context) {
        WTabPanel tabPanel = new WTabPanel();
        YamlConfiguration tabsConfig = config.getConfigurationSection("tabs");
        if (tabsConfig == null) {
            return tabPanel;
        }
        String tabsPath = getSubPath(pathToElement, "tabs");
        for (StringHolder tabIdHolder : tabsConfig.contents.keySet()) {
            String tabId = tabIdHolder.str;
            YamlConfiguration tabConfig = tabsConfig.getConfigurationSection(tabId);
            if (tabConfig == null) {
                Debug.echoError("Invalid tab '" + tabId + "': no options/config found.");
                continue;
            }
            WWidget content = container.parseGUIWidget(tabConfig, "content", getSubPath(tabsPath, tabId), context);
            if (content == null) {
                Debug.echoError("Invalid tab '" + tabId + "': must have valid content.");
                continue;
            }
            WTabPanel.Tab.Builder tabBuilder = new WTabPanel.Tab.Builder(content);
            String title = getTaggedString(tabConfig, "title", context);
            if (title != null) {
                tabBuilder.title(Text.literal(title));
            }
            List<String> tooltip = getTaggedStringList(tabConfig, "tooltip", context);
            if (tooltip != null) {
                tabBuilder.tooltip(tooltip.stream().map(Text::literal).toArray(Text[]::new));
            }
            Icon icon = parseIcon(tabConfig, "icon", context);
            if (icon != null) {
                tabBuilder.icon(icon);
            }
            tabPanel.add(tabBuilder.build());
        }
        return tabPanel;
    }
}
