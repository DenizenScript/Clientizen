package com.denizenscript.clientizen.tags;

import com.denizenscript.clientizen.objects.EntityTag;
import com.denizenscript.clientizen.objects.LocationTag;
import com.denizenscript.clientizen.objects.MaterialTag;
import com.denizenscript.denizencore.DenizenCore;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.core.MapTag;
import com.denizenscript.denizencore.objects.core.TimeTag;
import com.denizenscript.denizencore.scripts.commands.core.AdjustCommand;
import com.denizenscript.denizencore.tags.PseudoObjectTagBase;
import com.denizenscript.denizencore.tags.TagManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;

public class ClientTagBase extends PseudoObjectTagBase<ClientTagBase> {

    public static ClientTagBase instance;

    public ClientTagBase() {
        instance = this;
        TagManager.registerStaticTagBaseHandler(ClientTagBase.class, "client", t -> instance);
        AdjustCommand.specialAdjustables.put("client", mechanism -> tagProcessor.processMechanism(instance, mechanism));
    }

    @Override
    public void register() {
        tagProcessor.registerTag(ListTag.class, "loaded_entities", (attribute, object) -> {
            ListTag list = new ListTag();
            for (Entity entity : MinecraftClient.getInstance().world.getEntities()) {
                list.addObject(new EntityTag(entity));
            }
            return list;
        });
        tagProcessor.registerTag(EntityTag.class, "target", (attribute, object) -> {
            Entity target = MinecraftClient.getInstance().targetedEntity;
            return target != null ? new EntityTag(target) : null;
        });
        tagProcessor.registerTag(LocationTag.class, "cursor_on", (attribute, object) -> {
            return MinecraftClient.getInstance().crosshairTarget instanceof BlockHitResult blockHit ? new LocationTag(blockHit.getBlockPos()) : null;
        });
        tagProcessor.registerTag(LocationTag.class, "cursor_on_precise", (attribute, object) -> {
            return MinecraftClient.getInstance().crosshairTarget instanceof BlockHitResult blockHit ? new LocationTag(blockHit.getPos()) : null;
        });
        tagProcessor.registerTag(EntityTag.class, "self_entity", (attribute, object) -> {
            return new EntityTag(MinecraftClient.getInstance().player);
        });
        tagProcessor.registerTag(ElementTag.class, ElementTag.class, "has_flag", (attribute, object, param) -> {
            return new ElementTag(DenizenCore.serverFlagMap.hasFlag(param.asString()));
        });
        tagProcessor.registerTag(ObjectTag.class, ElementTag.class, "flag", (attribute, object, param) -> {
            return DenizenCore.serverFlagMap.doFlagTag(attribute);
        });
        tagProcessor.registerTag(TimeTag.class, ElementTag.class, "flag_expiration", (attribute, object, param) -> {
            return DenizenCore.serverFlagMap.doFlagExpirationTag(attribute);
        });
        tagProcessor.registerTag(ListTag.class, "list_flags", (attribute, object) -> {
            return DenizenCore.serverFlagMap.doListFlagsTag(attribute);
        });
        tagProcessor.registerTag(MapTag.class, "flag_map", (attribute, object) -> {
            return DenizenCore.serverFlagMap.doFlagMapTag(attribute);
        });
        // TODO this is temporary and is meant for testing only, should be replaced by a proper modifyblock command
        tagProcessor.registerMechanism("modifyblock", false, MaterialTag.class, (object, mechanism, input) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.crosshairTarget instanceof BlockHitResult blockHitResult) {
                client.world.setBlockState(blockHitResult.getBlockPos(), input.state);
            }
        });
    }
}
