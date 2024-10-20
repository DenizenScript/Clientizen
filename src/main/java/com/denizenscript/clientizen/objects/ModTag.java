package com.denizenscript.clientizen.objects;

import com.denizenscript.clientizen.util.Utilities;
import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.Fetchable;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.core.MapTag;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

import java.util.Optional;

public class ModTag implements ObjectTag {

    // <--[ObjectType]
    // @name ModTag
    // @prefix mod
    // @base ElementTag
    // @ExampleTagBase mod[clientizen]
    // @ExampleValues <mod[clientizen]>
    // @ExampleForReturns
    // - narrate "The mod is %VALUE%!"
    // @format
    // The identity format for mods is the mod id.
    // For example, 'mod@clientizen' or 'mod@theprinter'.
    //
    // @description
    // A ModTag represents a currently loaded Fabric mod.
    //
    // This can be either a mod that's been downloaded and installed, a built-in mod, or a mod within another mod (a library mod, for example)
    //
    // @Matchable
    // ModTag matchers:
    // "mod" plaintext: always matches.
    // Mod id: matches if the mod id matches the input, using advanced matchers.
    // -->

    @Fetchable("mod")
    public static ModTag valueOf(String input, TagContext context) {
        if (input.startsWith("mod@")) {
            input = input.substring("mod@".length());
        }
        Optional<ModTag> modTag = FabricLoader.getInstance().getModContainer(CoreUtilities.toLowerCase(input)).map(ModTag::new);
        if (modTag.isEmpty()) {
            Utilities.echoErrorByContext(context, "valueOf ModTag returning null: '" + input + "' isn't a valid mod id.");
            return null;
        }
        return modTag.get();
    }

    public static boolean matches(String string) {
        if (string.startsWith("mod@")) {
            return true;
        }
        return FabricLoader.getInstance().isModLoaded(CoreUtilities.toLowerCase(string));
    }

    public final ModContainer modContainer;

    public ModTag(ModContainer modContainer) {
        this.modContainer = modContainer;
    }

    public ModMetadata getMetadata() {
        return modContainer.getMetadata();
    }

    public static void register() {

        // <--[tag]
        // @attribute <ModTag.id>
        // @returns ElementTag
        // @description
        // Returns a mod's id.
        // -->
        tagProcessor.registerStaticTag(ElementTag.class, "id", (attribute, object) -> {
            return new ElementTag(object.getMetadata().getId(), true);
        });

        // <--[tag]
        // @attribute <ModTag.display_name>
        // @returns ElementTag
        // @description
        // Returns a mod's display name.
        // -->
        tagProcessor.registerStaticTag(ElementTag.class, "display_name", (attribute, object) -> {
            return new ElementTag(object.getMetadata().getName(), true);
        });

        // <--[tag]
        // @attribute <ModTag.description>
        // @returns ElementTag
        // @description
        // Returns a mod's description.
        // -->
        tagProcessor.registerStaticTag(ElementTag.class, "description", (attribute, object) -> {
            return new ElementTag(object.getMetadata().getDescription(), true);
        });

        // <--[tag]
        // @attribute <ModTag.version>
        // @returns ElementTag
        // @description
        // Returns a mod's version.
        // -->
        tagProcessor.registerStaticTag(ElementTag.class, "version", (attribute, object) -> {
            return new ElementTag(object.getMetadata().getVersion().getFriendlyString(), true);
        });

        // <--[tag]
        // @attribute <ModTag.authors>
        // @returns MapTag
        // @description
        // Returns a mod's authors, as a map of author name to their contact information.
        // The contact information is a map of contact platforms to identification on that platform.
        // Note that mods can provide anything here, although most mods will obviously provide valid info.
        // -->
        tagProcessor.registerStaticTag(MapTag.class, "authors", (attribute, object) -> {
            return Utilities.personsToMap(object.getMetadata().getAuthors());
        });

        // <--[tag]
        // @attribute <ModTag.contributors>
        // @returns MapTag
        // @description
        // Returns a mod's contributors, as a map of contributor name to their contact information.
        // The contact information is a map of contact platforms to identification on that platform.
        // Note that mods can provide anything here, although most mods will obviously provide valid info.
        // -->
        tagProcessor.registerStaticTag(MapTag.class, "contributors", (attribute, object) -> {
            return Utilities.personsToMap(object.getMetadata().getContributors());
        });

        // <--[tag]
        // @attribute <ModTag.contact_info>
        // @returns MapTag
        // @description
        // Returns a mod's contact information, as a map of contact platforms to identification on that platform.
        // Some common examples are: "repo", "website", "issues", etc.
        // Note that mods can provide anything here, although most mods will obviously provide valid info.
        // -->
        tagProcessor.registerStaticTag(MapTag.class, "contact_info", (attribute, object) -> {
            return Utilities.contactInfoToMap(object.getMetadata().getContact());
        });

        // <--[tag]
        // @attribute <ModTag.licenses>
        // @returns ListTag
        // @description
        // Returns a list of a mod's licenses.
        // -->
        tagProcessor.registerStaticTag(ListTag.class, "licenses", (attribute, object) -> {
            return new ListTag(object.getMetadata().getLicense(), true);
        });

        // <--[tag]
        // @attribute <ModTag.type>
        // @returns ElementTag
        // @description
        // Returns a mod's type, either:
        //  'fabric' - a regular Fabric mod, either directly installed or included by another mod.
        //  'builtin' - a built-in mod, generally used for internal mods included by Fabric itself.
        // -->
        tagProcessor.registerStaticTag(ElementTag.class, "type", (attribute, object) -> {
            return new ElementTag(object.getMetadata().getType(), true);
        });

        // <--[tag]
        // @attribute <ModTag.containing_mod>
        // @returns ModTag
        // @description
        // Returns the mod that contains this mod, if any (for things like library mods included by other mods).
        // -->
        tagProcessor.registerStaticTag(ModTag.class, "containing_mod", (attribute, object) -> {
            return object.modContainer.getContainingMod().map(ModTag::new).orElse(null);
        });

        // <--[tag]
        // @attribute <ModTag.contained_mods>
        // @returns ListTag(ModTag)
        // @description
        // Returns a list of mods contained by this mod (for mods that include libraries, for example).
        // -->
        tagProcessor.registerStaticTag(ListTag.class, "contained_mods", (attribute, object) -> {
            return new ListTag(object.modContainer.getContainedMods(), ModTag::new);
        });
    }

    public static final ObjectTagProcessor<ModTag> tagProcessor = new ObjectTagProcessor<>();

    @Override
    public ObjectTag getObjectAttribute(Attribute attribute) {
        return tagProcessor.getObjectAttribute(this, attribute);
    }

    @Override
    public String identify() {
        return "mod@" + getMetadata().getId();
    }

    @Override
    public String debuggable() {
        return "<LG>mod@<Y>" + getMetadata().getId();
    }

    @Override
    public String identifySimple() {
        return identify();
    }

    @Override
    public String toString() {
        return identify();
    }

    @Override
    public ModContainer getJavaObject() {
        return modContainer;
    }

    @Override
    public boolean advancedMatches(String matcher, TagContext context) {
        String matcherLower = CoreUtilities.toLowerCase(matcher);
        if (matcherLower.equals("mod")) {
            return true;
        }
        return ScriptEvent.runGenericCheck(matcherLower, getMetadata().getId());
    }

    @Override
    public boolean isUnique() {
        return true;
    }

    String prefix = "Mod";

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public ObjectTag setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }
}
