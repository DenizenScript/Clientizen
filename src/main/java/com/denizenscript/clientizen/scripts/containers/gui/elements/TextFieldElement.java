package com.denizenscript.clientizen.scripts.containers.gui.elements;

import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer;
import com.denizenscript.clientizen.util.impl.ClientizenScriptEntryData;
import com.denizenscript.denizencore.objects.core.ColorTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.queues.ContextSource;
import com.denizenscript.denizencore.tags.ParseableTag;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.tags.TagManager;
import com.denizenscript.denizencore.utilities.ScriptUtilities;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;

import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getDebugPath;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getSubPath;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedBoolean;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedInt;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedObject;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getTaggedString;
import static com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer.getWidgetId;

public class TextFieldElement implements GuiScriptContainer.GuiElementParser {

    @Override
    public WWidget parse(GuiScriptContainer container, YamlConfiguration config, String pathToElement, TagContext context) {
        WTextField textField = new WTextField();
        String suggestion = getTaggedString(config, "suggestion", context);
        if (suggestion != null) {
            textField.setSuggestion(Text.literal(suggestion));
        }
        String text = getTaggedString(config, "text", context);
        if (text != null) {
            textField.setText(text);
        }
        Boolean editable = getTaggedBoolean(config, "editable", context);
        if (editable != null) {
            textField.setEditable(editable);
        }
        Integer maxLength = getTaggedInt(config, "max_length", context);
        if (maxLength != null) {
            textField.setMaxLength(maxLength);
        }
        ColorTag disabledColor = getTaggedObject(ColorTag.class, config, "disabled_color", context);
        if (disabledColor != null) {
            textField.setDisabledColor(disabledColor.asARGB());
        }
        ColorTag enabledColor = getTaggedObject(ColorTag.class, config, "enabled_color", context);
        if (enabledColor != null) {
            textField.setEnabledColor(enabledColor.asARGB());
        }
        ColorTag suggestionColor = getTaggedObject(ColorTag.class, config, "suggestion_color", context);
        if (suggestionColor != null) {
            textField.setSuggestionColor(suggestionColor.asARGB());
        }
        String inputChecker = config.getString("input_checker");
        if (inputChecker != null) {
            final ParseableTag checkerTag = TagManager.parseTextToTag(inputChecker, context);
            final String errorContext = "in input checker for text field '<A>" + getDebugPath(pathToElement) + "<LR>'";
            final TagContext checkingContext = context.clone();
            final ContextSource.SimpleMap contextSource = new ContextSource.SimpleMap();
            checkingContext.contextSource = contextSource;
            textField.setTextPredicate(newText -> {
                contextSource.contexts = Map.of("new_text", new ElementTag(newText, true));
                Debug.pushErrorContext(errorContext);
                try {
                    ElementTag parsedCheck = checkerTag.parse(checkingContext).asElement();
                    if (!parsedCheck.isBoolean()) {
                        Debug.echoError("Invalid boolean value '" + parsedCheck + "' returned: must be either 'true' or 'false'.");
                        return true;
                    }
                    return parsedCheck.asBoolean();
                }
                finally {
                    Debug.popErrorContext();
                }
            });
        }
        final List<ScriptEntry> onChange = container.getEntries(new ClientizenScriptEntryData(), getSubPath(pathToElement, "on_change"));
        if (onChange != null) {
            final ContextSource.SimpleMap mapContextSource = new ContextSource.SimpleMap();
            final String queueName = getWidgetId(pathToElement) + "_changed";
            textField.setChangedListener(newText -> {
                mapContextSource.contexts = Map.of("new_text", new ElementTag(newText, true));
                ScriptUtilities.createAndStartQueueArbitrary(queueName, onChange, null, mapContextSource, null);
                // TODO: proper id system - events
            });
        }
        return textField;
    }
}
