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

public class TabPanelElement implements GuiScriptContainer.GuiElementParser {

    @Override
    public WWidget parse(GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context) {
        WTabPanel tabPanel = new WTabPanel();
        YamlConfiguration tabsConfig = config.getConfigurationSection("tabs");
        if (tabsConfig == null) {
            return tabPanel;
        }
        for (StringHolder tabIdHolder : tabsConfig.contents.keySet()) {
            String tabId = tabIdHolder.str;
            YamlConfiguration tabConfig = tabsConfig.getConfigurationSection(tabId);
            if (tabConfig == null) {
                Debug.echoError("Invalid tab '" + tabId + "': no options/config found.");
                continue;
            }
            WWidget content = container.parseGUIWidget(tabConfig, "content", pathToElement + ".tabs." + tabId, context);
            if (content == null) {
                Debug.echoError("Invalid tab '" + tabId + "': must have valid content.");
                continue;
            }
            WTabPanel.Tab.Builder tabBuilder = new WTabPanel.Tab.Builder(content);
            if (tabConfig.contains("title")) {
                tabBuilder.title(Text.literal(GuiScriptContainer.getTaggedString(tabConfig, "title", context)));
            }
            if (tabConfig.contains("tooltip")) {
                tabBuilder.tooltip(GuiScriptContainer.getTaggedStringList(tabConfig, "tooltip", context).stream().map(Text::literal).toArray(Text[]::new));
            }
            if (tabConfig.contains("icon")) {
                Icon icon = GuiScriptContainer.parseIcon(tabConfig, "icon", context);
                if (icon != null) {
                    tabBuilder.icon(icon);
                }
            }
            tabPanel.add(tabBuilder.build());
        }
        return tabPanel;
    }
}
