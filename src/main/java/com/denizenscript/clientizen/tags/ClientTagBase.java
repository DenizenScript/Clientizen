package com.denizenscript.clientizen.tags;

import com.denizenscript.clientizen.objects.EntityTag;
import com.denizenscript.clientizen.objects.LocationTag;
import com.denizenscript.clientizen.objects.MaterialTag;
import com.denizenscript.clientizen.objects.ModTag;
import com.denizenscript.denizencore.DenizenCore;
import com.denizenscript.denizencore.flags.AbstractFlagTracker;
import com.denizenscript.denizencore.flags.FlaggableObject;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.core.MapTag;
import com.denizenscript.denizencore.objects.core.TimeTag;
import com.denizenscript.denizencore.scripts.commands.core.AdjustCommand;
import com.denizenscript.denizencore.tags.PseudoObjectTagBase;
import com.denizenscript.denizencore.tags.TagManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;

public class ClientTagBase extends PseudoObjectTagBase<ClientTagBase> implements FlaggableObject {

    public static ClientTagBase instance;
    public static double climbingSpeed = 0.2; // 0.2 is the vanilla default

    public ClientTagBase() {
        instance = this;
        TagManager.registerStaticTagBaseHandler(ClientTagBase.class, "client", t -> instance);
        AdjustCommand.specialAdjustables.put("client", mechanism -> tagProcessor.processMechanism(instance, mechanism));
    }

    @Override
    public void register() {

        // <--[tag]
        // @attribute <client.loaded_entities[(<matcher>)]>
        // @returns ListTag(EntityTag)
        // @description
        // Returns a list of all entities currently loaded by the client.
        // Optionally specify an EntityTag matcher to filter by.
        // -->
        tagProcessor.registerTag(ListTag.class, "loaded_entities", (attribute, object) -> {
            String matcher = attribute.hasParam() ? attribute.getParam() : null;
            ListTag entities = new ListTag();
            for (Entity entity : LocationTag.getWorld().entitiesForRendering()) {
                EntityTag entityTag = new EntityTag(entity);
                if (matcher == null || entityTag.advancedMatches(matcher, attribute.context)) {
                    entities.addObject(entityTag);
                }
            }
            return entities;
        });

        // <--[tag]
        // @attribute <client.mods>
        // @returns ListTag(ModTag)
        // @description
        // Returns a list of all currently loaded Fabric mods (this doesn't include things like mods-within-mods or built-in mods).
        // -->
        tagProcessor.registerStaticTag(ListTag.class, "mods", (attribute, object) -> {
            return new ListTag(FabricLoader.getInstance().getAllMods(),
                    modContainer -> modContainer.getContainingMod().isEmpty() && modContainer.getMetadata().getType().equals("fabric")
                            && !modContainer.getMetadata().getId().equals("fabricloader"),
                    ModTag::new);
        });

        // <--[tag]
        // @attribute <client.all_mods>
        // @returns ListTag(ModTag)
        // @description
        // Returns a list of all currently loaded Fabric mods, including mods-within-mods and built-in mods.
        // -->
        tagProcessor.registerStaticTag(ListTag.class, "all_mods", (attribute, object) -> {
            return new ListTag(FabricLoader.getInstance().getAllMods(), ModTag::new);
        });

        // <--[tag]
        // @attribute <client.target>
        // @returns EntityTag
        // @description
        // Returns the entity the client is currently looking at, if any.
        // -->
        // TODO: do our own ray tracing to have full control
        tagProcessor.registerTag(EntityTag.class, "target", (attribute, object) -> {
            Entity target = Minecraft.getInstance().crosshairPickEntity;
            return target != null ? new EntityTag(target) : null;
        });

        // <--[tag]
        // @attribute <client.cursor_on>
        // @returns LocationTag
        // @description
        // Returns the location of the block the client is currently looking at, if any.
        // -->
        tagProcessor.registerTag(LocationTag.class, "cursor_on", (attribute, object) -> {
            return Minecraft.getInstance().hitResult instanceof BlockHitResult blockHit ? new LocationTag(blockHit.getBlockPos()) : null;
        });

        // <--[tag]
        // @attribute <client.cursor_on_precise>
        // @returns LocationTag
        // @description
        // Returns the precise location the client is currently looking at, if any.
        // -->
        tagProcessor.registerTag(LocationTag.class, "cursor_on_precise", (attribute, object) -> {
            return Minecraft.getInstance().hitResult instanceof BlockHitResult blockHit ? new LocationTag(blockHit.getLocation()) : null;
        });

        // <--[tag]
        // @attribute <client.self_entity>
        // @returns EntityTag
        // @description
        // Returns an EntityTag of the client's own player entity.
        // -->
        tagProcessor.registerTag(EntityTag.class, "self_entity", (attribute, object) -> {
            return new EntityTag(Minecraft.getInstance().player);
        });

        // <--[tag]
        // @attribute <client.has_flag[<flag>]>
        // @returns ElementTag(Boolean)
        // @description
        // See <@link tag FlaggableObject.has_flag>.
        // -->
        tagProcessor.registerTag(ElementTag.class, ElementTag.class, "has_flag", (attribute, object, param) -> {
            return new ElementTag(DenizenCore.serverFlagMap.hasFlag(param.asString()));
        });

        // <--[tag]
        // @attribute <client.flag[<flag_name>]>
        // @returns ObjectTag
        // @description
        // See <@link tag FlaggableObject.flag>
        // -->
        tagProcessor.registerTag(ObjectTag.class, ElementTag.class, "flag", (attribute, object, param) -> {
            return DenizenCore.serverFlagMap.doFlagTag(attribute);
        });

        // <--[tag]
        // @attribute <client.flag_expiration[<flag_name>]>
        // @returns TimeTag
        // @description
        // See <@link tag FlaggableObject.flag_expiration>
        // -->
        tagProcessor.registerTag(TimeTag.class, ElementTag.class, "flag_expiration", (attribute, object, param) -> {
            return DenizenCore.serverFlagMap.doFlagExpirationTag(attribute);
        });

        // <--[tag]
        // @attribute <client.list_flags>
        // @returns ListTag
        // @description
        // See <@link tag FlaggableObject.list_flags>
        // -->
        tagProcessor.registerTag(ListTag.class, "list_flags", (attribute, object) -> {
            return DenizenCore.serverFlagMap.doListFlagsTag(attribute);
        });

        // <--[tag]
        // @attribute <client.flag_map[<name>|...]>
        // @returns MapTag
        // @description
        // See <@link tag FlaggableObject.flag_map>
        // -->
        tagProcessor.registerTag(MapTag.class, "flag_map", (attribute, object) -> {
            return DenizenCore.serverFlagMap.doFlagMapTag(attribute);
        });

        // <--[tag]
        // @attribute <client.climbing_speed>
        // @returns ElementTag(Decimal)
        // @mechanism client.climbing_speed
        // @description
        // Returns the client's climbing speed.
        // -->
        tagProcessor.registerTag(ElementTag.class, "climbing_speed", (attribute, object) -> {
            return new ElementTag(climbingSpeed);
        });

        // <--[mechanism]
        // @object client
        // @name climbing_speed
        // @input ElementTag(Decimal)
        // @description
        // Sets the client's climbing speed.
        // @tags
        // <client.climbing_speed>
        // -->
        tagProcessor.registerMechanism("climbing_speed", false, ElementTag.class, (object, mechanism, input) -> {
            if (mechanism.requireDouble()) {
                climbingSpeed = input.asDouble();
            }
        });

        // <--[tag]
        // @attribute <client.chat_width>
        // @returns ElementTag(Number)
        // @description
        // Returns the width of the client's chat HUD, in pixels.
        // @example
        // # Use to check if a line of text can fit in the chat HUD without splitting into multiple lines.
        // - if <[text].text_width.mul[<client.chat_scale>]> <= <client.chat_width>:
        //   - narrate <[text]>
        // - else:
        //   - narrate "Too long!"
        // -->
        tagProcessor.registerTag(ElementTag.class, "chat_width", (attribute, object) -> {
            return new ElementTag(Minecraft.getInstance().gui.getChat().getWidth());
        });

        // <--[tag]
        // @attribute <client.chat_scale>
        // @returns ElementTag(Number)
        // @description
        // Returns the client's chat scale, which is a multiplier for text's size.
        // @example
        // # Use to get the width text would have if displayed in the chat HUD.
        // - narrate "The text would be <[text].text_width.mul[<client.chat_scale>]> pixels wide."
        // -->
        tagProcessor.registerTag(ElementTag.class, "chat_scale", (attribute, object) -> {
            return new ElementTag(Minecraft.getInstance().gui.getChat().getScale());
        });

        // TODO this is temporary and is meant for testing only, should be replaced by a proper modifyblock command
        tagProcessor.registerMechanism("modifyblock", false, MaterialTag.class, (object, mechanism, input) -> {
            Minecraft client = Minecraft.getInstance();
            if (client.hitResult instanceof BlockHitResult blockHitResult) {
                client.level.setBlockAndUpdate(blockHitResult.getBlockPos(), input.state);
            }
        });
    }

    @Override
    public AbstractFlagTracker getFlagTracker() {
        return DenizenCore.serverFlagMap;
    }

    @Override
    public void reapplyTracker(AbstractFlagTracker tracker) {}
}
