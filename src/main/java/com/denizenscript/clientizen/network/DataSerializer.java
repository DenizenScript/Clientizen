package com.denizenscript.clientizen.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

public class DataSerializer {

    public final PacketByteBuf byteBuf;

    public DataSerializer() {
        byteBuf = PacketByteBufs.create();
    }

    public DataSerializer writeInt(int i) {
        byteBuf.writeInt(i);
        return this;
    }

    public DataSerializer writeBoolean(boolean bool) {
        byteBuf.writeBoolean(bool);
        return this;
    }

    public DataSerializer writeBytes(@NotNull byte[] bytes) {
        byteBuf.writeBytes(bytes);
        return this;
    }

    public DataSerializer writeByteArray(@NotNull byte[] bytes) {
        return writeInt(bytes.length).writeBytes(bytes);
    }

    public DataSerializer writeString(@NotNull String s) {
        return writeByteArray(s.getBytes(StandardCharsets.UTF_8));
    }

    public DataSerializer writeStringList(@NotNull Collection<String> strings) {
        writeInt(strings.size());
        for (String s : strings) {
            writeString(s);
        }
        return this;
    }

    public DataSerializer writeStringListMap(@NotNull Map<String, Collection<String>> map) {
        writeInt(map.size());
        for (Map.Entry<String, Collection<String>> entry : map.entrySet()) {
            writeString(entry.getKey());
            writeStringList(entry.getValue());
        }
        return this;
    }

    public DataSerializer writeStringMap(Map<String, String> stringMap) {
        writeInt(stringMap.size());
        for (Map.Entry<String, String> entry : stringMap.entrySet()) {
            writeString(entry.getKey());
            writeString(entry.getValue());
        }
        return this;
    }
}
