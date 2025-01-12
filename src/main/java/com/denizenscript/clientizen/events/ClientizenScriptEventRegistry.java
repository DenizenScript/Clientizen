package com.denizenscript.clientizen.events;

import com.denizenscript.clientizen.util.Utilities;
import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.events.ScriptEventCouldMatcher;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

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
        ScriptEvent.registerScriptEvent(RenderScriptEvent.class);
    }

    public static final Set<String> ENTITY_PLAINTEXT_MATCHERS = new HashSet<>(Arrays.asList(
            "entity", "vehicle", "fish", "projectile", "hanging", "monster", "animal", "mob", "living"));

    public static boolean couldMatchEntity(String matcher) {
        if (ENTITY_PLAINTEXT_MATCHERS.contains(matcher)) {
            return true;
        }
        if (ScriptEvent.isAdvancedMatchable(matcher)) {
            ScriptEvent.MatchHelper matchHelper = ScriptEvent.createMatcher(matcher);
            for (EntityType<?> entityType : Registries.ENTITY_TYPE) {
                if (matchHelper.doesMatch(entityType.getUntranslatedName())) {
                    return true;
                }
            }
            ScriptEvent.addPossibleCouldMatchFailReason("Matcher doesn't match any entity type", matcher);
            return false;
        }
        if (Registries.ENTITY_TYPE.containsId(Identifier.tryParse(matcher))) {
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
            if (registryContainsMatch(Registries.ITEM, matcher)) {
                return true;
            }
            if (registryContainsMatch(Registries.BLOCK, matcher)) {
                return true;
            }
            ScriptEvent.addPossibleCouldMatchFailReason("Matcher doesn't match any block/item", matcher);
            return false;
        }
        Identifier id = Identifier.tryParse(matcher);
        if (Registries.ITEM.containsId(id) || Registries.BLOCK.containsId(id)) {
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
            if (registryContainsMatch(Registries.ITEM, matcher)) {
                return true;
            }
            ScriptEvent.addPossibleCouldMatchFailReason("Matcher doesn't match any item", matcher);
            return false;
        }
        if (Registries.ITEM.containsId(Identifier.tryParse(matcher))) {
            return true;
        }
        ScriptEvent.addPossibleCouldMatchFailReason("Invalid item name", matcher);
        return false;
    }

    private static boolean registryContainsMatch(Registry<?> registry, String matcher) {
        ScriptEvent.MatchHelper matchHelper = ScriptEvent.createMatcher(matcher);
        for (Identifier id : registry.getIds()) {
            if (matchHelper.doesMatch(Utilities.idToString(id))) {
                return true;
            }
        }
        return false;
    }
}
