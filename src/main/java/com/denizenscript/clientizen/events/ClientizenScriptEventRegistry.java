package com.denizenscript.clientizen.events;

import com.denizenscript.clientizen.util.Utilities;
import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.events.ScriptEventCouldMatcher;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ClientizenScriptEventRegistry {

    public static void registerEvents() {
        ScriptEventCouldMatcher.knownValidatorTypes.put("entity", ClientizenScriptEventRegistry::couldMatchEntity);
        ScriptEventCouldMatcher.knownValidatorTypes.put("material", ClientizenScriptEventRegistry::couldMatchMaterial);

        ScriptEvent.registerScriptEvent(KeyPressReleaseScriptEvent.class);
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
            ScriptEvent.MatchHelper matchHelper = ScriptEvent.createMatcher(matcher);
            for (Identifier item : Registries.ITEM.getIds()) {
                if (matchHelper.doesMatch(Utilities.idToString(item))) {
                    return true;
                }
            }
            for (Identifier block : Registries.BLOCK.getIds()) {
                if (matchHelper.doesMatch(Utilities.idToString(block))) {
                    return true;
                }
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
}
