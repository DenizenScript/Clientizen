package com.denizenscript.clientizen.events;

import com.denizenscript.clientizen.util.Utilities;
import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.events.ScriptEventCouldMatcher;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ClientizenScriptEventRegistry {

    public static void registerEvents() {
        ScriptEventCouldMatcher.knownValidatorTypes.put("entity", ClientizenScriptEventRegistry::couldMatchEntity);
        ScriptEventCouldMatcher.knownValidatorTypes.put("material", ClientizenScriptEventRegistry::couldMatchMaterial);
        ScriptEventCouldMatcher.knownValidatorTypes.put("item", ClientizenScriptEventRegistry::couldMatchItem);

        // <--[data]
        // @name not_switches
        // @values item_enchanted, server_script
        // -->
        ScriptEvent.ScriptPath.notSwitches.addAll(ITEM_MATCHER_PREFIXES);

        ScriptEvent.registerScriptEvent(EntityStartsStopsRenderingScriptEvent.class);
        ScriptEvent.registerScriptEvent(KeyPressReleaseScriptEvent.class);
        ScriptEvent.registerScriptEvent(PlayerSprintScriptEvent.class);
        ScriptEvent.registerScriptEvent(ScreenOpenCloseEvent.class);
    }

    public static final Set<String> ENTITY_PLAINTEXT_MATCHERS = new HashSet<>(Arrays.asList(
            "entity", "vehicle", "fish", "projectile", "hanging", "monster", "animal", "mob", "living"));

    public static boolean couldMatchEntity(String matcher) {
        if (ENTITY_PLAINTEXT_MATCHERS.contains(matcher)) {
            return true;
        }
        if (ScriptEvent.isAdvancedMatchable(matcher)) {
            ScriptEvent.MatchHelper matchHelper = ScriptEvent.createMatcher(matcher);
            for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
                if (matchHelper.doesMatch(entityType.toShortString())) {
                    return true;
                }
            }
            ScriptEvent.addPossibleCouldMatchFailReason("Matcher doesn't match any entity type", matcher);
            return false;
        }
        if (BuiltInRegistries.ENTITY_TYPE.containsKey(Identifier.tryParse(matcher))) {
            return true;
        }
        ScriptEvent.addPossibleCouldMatchFailReason("Invalid entity type", matcher);
        return false;
    }

    public static final Set<String> MATERIAL_PLAINTEXT_MATCHERS = new HashSet<>(Arrays.asList("material", "block", "item"));

    public static boolean couldMatchMaterial(String matcher) {
        if (MATERIAL_PLAINTEXT_MATCHERS.contains(matcher)) {
            return true;
        }
        if (ScriptEvent.isAdvancedMatchable(matcher)) {
            if (registryContainsMatch(BuiltInRegistries.ITEM, matcher)) {
                return true;
            }
            if (registryContainsMatch(BuiltInRegistries.BLOCK, matcher)) {
                return true;
            }
            ScriptEvent.addPossibleCouldMatchFailReason("Matcher doesn't match any block/item", matcher);
            return false;
        }
        Identifier id = Identifier.tryParse(matcher);
        if (BuiltInRegistries.ITEM.containsKey(id) || BuiltInRegistries.BLOCK.containsKey(id)) {
            return true;
        }
        ScriptEvent.addPossibleCouldMatchFailReason("Invalid block/item name", matcher);
        return false;
    }

    public static final Set<String> ITEM_MATCHER_PREFIXES = new HashSet<>(Arrays.asList("item_enchanted", "server_script"));
    public static final Set<String> ITEM_PLAINTEXT_MATCHERS = Set.of("item");

    public static boolean couldMatchItem(String matcher) {
        if (ITEM_PLAINTEXT_MATCHERS.contains(matcher)) {
            return true;
        }
        int colonIndex = matcher.indexOf(':');
        if (colonIndex != -1 && ITEM_MATCHER_PREFIXES.contains(matcher.substring(0, colonIndex))) {
            return true;
        }
        if (ScriptEvent.isAdvancedMatchable(matcher)) {
            if (registryContainsMatch(BuiltInRegistries.ITEM, matcher)) {
                return true;
            }
            ScriptEvent.addPossibleCouldMatchFailReason("Matcher doesn't match any item", matcher);
            return false;
        }
        if (BuiltInRegistries.ITEM.containsKey(Identifier.tryParse(matcher))) {
            return true;
        }
        ScriptEvent.addPossibleCouldMatchFailReason("Invalid item name", matcher);
        return false;
    }

    private static boolean registryContainsMatch(Registry<?> registry, String matcher) {
        ScriptEvent.MatchHelper matchHelper = ScriptEvent.createMatcher(matcher);
        for (Identifier id : registry.keySet()) {
            if (matchHelper.doesMatch(Utilities.idToString(id))) {
                return true;
            }
        }
        return false;
    }
}
