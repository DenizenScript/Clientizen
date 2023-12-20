package com.denizenscript.clientizen.events;

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
        ScriptEvent.registerScriptEvent(KeyPressReleaseScriptEvent.class);
        ScriptEvent.registerScriptEvent(ScreenOpenCloseEvent.class);

        ScriptEventCouldMatcher.knownValidatorTypes.put("entity", ClientizenScriptEventRegistry::couldMatchEntity);
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
}
