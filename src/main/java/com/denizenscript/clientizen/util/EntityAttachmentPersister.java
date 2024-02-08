package com.denizenscript.clientizen.util;

import com.denizenscript.clientizen.Clientizen;
import it.unimi.dsi.fastutil.Pair;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class EntityAttachmentPersister {

    private static final Map<UUID, List<Pair<AttachmentType<Object>, Object>>> persistedData = new HashMap<>();
    private static final List<AttachmentType<Object>> persistedAttachments = new ArrayList<>();

    static {
        ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> loadData(entity));
        ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            // This is called for each entity when the world is unloaded on logout
            if (MinecraftClient.getInstance().getNetworkHandler().isConnectionOpen()) {
                storeData(entity);
            }
        });
        Clientizen.SYNC_DISCONNECT.register(persistedData::clear);
    }

    public static <T> AttachmentType<T> createAttachment(String id) {
        AttachmentType<T> attachment = AttachmentRegistry.create(Clientizen.id(id));
        persistedAttachments.add((AttachmentType<Object>) attachment);
        return attachment;
    }

    public static void storeData(Entity entity) {
        List<Pair<AttachmentType<Object>, Object>> allData = null;
        for (AttachmentType<Object> persisted : persistedAttachments) {
            Object data = entity.getAttached(persisted);
            if (data != null) {
                if (allData == null) {
                    allData = new ArrayList<>();
                }
                allData.add(Pair.of(persisted, data));
            }
        }
        if (allData != null) {
            persistedData.put(entity.getUuid(), allData);
        }
    }

    public static void loadData(Entity entity) {
        List<Pair<AttachmentType<Object>, Object>> data = persistedData.remove(entity.getUuid());
        if (data == null) {
            return;
        }
        for (Pair<AttachmentType<Object>, Object> pair : data) {
            entity.setAttached(pair.key(), pair.value());
        }
    }
}
