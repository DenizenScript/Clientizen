package com.denizenscript.clientizen.util;

import com.denizenscript.denizencore.DenizenCore;
import com.denizenscript.denizencore.objects.ArgumentHelper;
import com.denizenscript.denizencore.objects.ObjectFetcher;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.ObjectType;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;
import com.denizenscript.denizencore.tags.TagManager;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.denizenscript.denizencore.utilities.ExCommandHelper;
import com.denizenscript.denizencore.utilities.debugging.DebugInternals;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ClientExecuteCommand implements SuggestionProvider<FabricClientCommandSource> {

    public ClientExecuteCommand(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("cex")
                .then(literal("-q")
                        .then(argument("silent_command", greedyString())
                                .suggests(this)
                                .executes(context -> {
                                    ExCommandHelper.runString("CEXCOMMAND", getString(context, "silent_command"), null, null);
                                    return 1;
                                })))
                .then(argument("command", greedyString())
                        .suggests(this)
                        .executes(context -> {
                            ExCommandHelper.runString("CEXCOMMAND", getString(context, "command"), null, queue -> {
                                queue.debugOutput = debug -> context.getSource().sendFeedback(Component.literal(debug.replace("<FORCE_ALIGN>", "")));
                            });
                            return 1;
                        })));
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) {
        String command = builder.getRemaining();
        String[] args = ArgumentHelper.buildArgs(command, true);
        boolean isNewArg = command.isEmpty() || command.charAt(command.length() - 1) == ' ';
        boolean isCommandArg = args.length == 0 || (args.length == 1 && !isNewArg) || args[args.length - (isNewArg ? 1 : 2)].equals("-");
        int lastSpaceIndex = command.lastIndexOf(' ') + 1;
        builder = builder.createOffset(lastSpaceIndex + builder.getStart());
        if (isCommandArg) {
            if (isNewArg || args.length == 0) {
                for (Map.Entry<String, AbstractCommand> entry : DenizenCore.commandRegistry.instances.entrySet()) {
                    builder.suggest(entry.getKey(), Component.literal(entry.getValue().getUsageHint()));
                }
                return builder.buildFuture();
            }
            String startOfName = CoreUtilities.toLowerCase(args[args.length - 1]);
            for (Map.Entry<String, AbstractCommand> entry : DenizenCore.commandRegistry.instances.entrySet()) {
                if (entry.getKey().startsWith(startOfName)) {
                    builder.suggest(entry.getKey(), Component.literal(entry.getValue().getUsageHint()));
                }
            }
            return builder.buildFuture();
        }
        String lastArg = lastSpaceIndex != 0 ? command.substring(lastSpaceIndex) : command;
        if (!isNewArg) {
            int argStart = 0;
            for (int i = 0; i < lastArg.length(); i++) {
                if (lastArg.charAt(i) == '"' || lastArg.charAt(i) == '\'') {
                    char quote = lastArg.charAt(i++);
                    while (i < lastArg.length() && lastArg.charAt(i) != quote) {
                        i++;
                    }
                }
                else if (lastArg.charAt(i) == ' ') {
                    argStart = i + 1;
                }
            }
            String arg = lastArg.substring(argStart);
            if (CoreUtilities.contains(arg, '<')) {
                int tagBits = 0;
                int relevantTagStart = -1;
                for (int i = arg.length() - 1; i >= 0; i--) {
                    if (arg.charAt(i) == '>') {
                        tagBits++;
                    }
                    else if (arg.charAt(i) == '<') {
                        if (tagBits == 0) {
                            relevantTagStart = i + 1;
                            break;
                        }
                        tagBits--;
                    }
                }
                if (relevantTagStart != -1) {
                    String fullTag = CoreUtilities.toLowerCase(arg.substring(relevantTagStart));
                    int components = 0;
                    int subTags = 0;
                    int squareBrackets = 0;
                    int lastDot = 0;
                    int bracketStart = -1;
                    Collection<Class<? extends ObjectTag>> typesApplicable = null;
                    for (int i = 0; i < fullTag.length(); i++) {
                        char c = fullTag.charAt(i);
                        if (c == '<') {
                            subTags++;
                        }
                        else if (c == '>') {
                            subTags--;
                        }
                        else if (c == '[' && subTags == 0) {
                            squareBrackets++;
                            bracketStart = i;
                        }
                        else if (c == ']' && subTags == 0) {
                            squareBrackets--;
                        }
                        else if (c == '.' && subTags == 0 && squareBrackets == 0) {
                            Class<? extends ObjectTag> type = null;
                            String part = fullTag.substring(lastDot, bracketStart == -1 ? i : bracketStart);
                            if (components == 0) {
                                TagManager.TagBaseData baseType = TagManager.baseTags.get(part);
                                if (baseType != null) {
                                    type = baseType.returnType;
                                }
                            }
                            else if (typesApplicable != null) {
                                for (Class<? extends ObjectTag> possibleType : typesApplicable) {
                                    ObjectType<? extends ObjectTag> typeData = ObjectFetcher.getType(possibleType);
                                    if (typeData != null && typeData.tagProcessor != null) {
                                        ObjectTagProcessor.TagData<?, ?> data = typeData.tagProcessor.registeredObjectTags.get(part);
                                        if (data != null && data.returnType != null) {
                                            type = data.returnType;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (type != null) {
                                typesApplicable = ObjectFetcher.getAllApplicableSubTypesFor(type);
                            }
                            else {
                                typesApplicable = ObjectFetcher.objectsByClass.keySet();
                            }
                            components++;
                            lastDot = i + 1;
                            bracketStart = -1;
                        }
                    }
                    SuggestionsBuilder tagSuggestions = builder.createOffset(relevantTagStart + lastDot + builder.getStart());
                    if (components == 0 && !CoreUtilities.contains(fullTag, '[')) {
                        for (Map.Entry<String, TagManager.TagBaseData> entry : TagManager.baseTags.entrySet()) {
                            if (entry.getKey().startsWith(fullTag)) {
                                Class<?> returnType = entry.getValue().returnType;
                                tagSuggestions.suggest(entry.getKey(), returnType != null ? Component.literal(DebugInternals.getClassNameOpti(returnType)) : null);
                            }
                        }
                        return tagSuggestions.buildFuture();
                    }
                    String subComponent = fullTag.substring(lastDot);
                    if (lastDot > 0 && !CoreUtilities.contains(subComponent, '[')) {
                        for (Class<? extends ObjectTag> possibleType : typesApplicable) {
                            ObjectType<? extends ObjectTag> typeData = ObjectFetcher.getType(possibleType);
                            if (typeData != null && typeData.tagProcessor != null) {
                                for (Map.Entry<String, ? extends ObjectTagProcessor.TagData<? extends ObjectTag, ? extends ObjectTag>> entry : typeData.tagProcessor.registeredObjectTags.entrySet()) {
                                    if (entry.getKey().startsWith(subComponent)) {
                                        tagSuggestions.suggest(entry.getKey(), Component.literal(DebugInternals.getClassNameOpti(entry.getValue().returnType)));
                                    }
                                }
                            }
                        }
                        return tagSuggestions.buildFuture();
                    }
                }
            }
        }
        AbstractCommand dcmd = DenizenCore.commandRegistry.get(args[0]);
        for (int i = args.length - 2; i >= 0; i--) {
            if (args[i].equals("-")) {
                dcmd = DenizenCore.commandRegistry.get(args[i + 1]);
            }
        }
        if (dcmd == null) {
            return null;
        }
        String lowArg = CoreUtilities.toLowerCase(lastArg);
        AbstractCommand.TabCompletionsBuilder completionsBuilder = new AbstractCommand.TabCompletionsBuilder();
        completionsBuilder.arg = lowArg;
        for (String flat : dcmd.docFlagArgs) {
            completionsBuilder.add(flat);
        }
        for (String prefix : dcmd.docPrefixes) {
            completionsBuilder.add(prefix + ":");
        }
        dcmd.addCustomTabCompletions(completionsBuilder);
        completionsBuilder.completions.forEach(builder::suggest);
        return builder.buildFuture();
    }
}
