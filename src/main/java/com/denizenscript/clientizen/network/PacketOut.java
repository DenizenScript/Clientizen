package com.denizenscript.clientizen.network;

import com.denizenscript.clientizen.Clientizen;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.Identifier;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class PacketOut {

    public final Identifier channel = Clientizen.id(getName());

    public void writeTo(ByteBuf buf) {} // Some packets may not send any data

    public abstract String getName();

    public static void writeString(ByteBuf buf, String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
    }

    public void writeStringMap(ByteBuf buf, Map<String, String> stringMap) {
        buf.writeInt(stringMap.size());
        for (Map.Entry<String, String> entry : stringMap.entrySet()) {
            writeString(buf, entry.getKey());
            writeString(buf, entry.getValue());
        }
    }

    public <T> void writeNullable(ByteBuf buf, T object, BiConsumer<ByteBuf, T> writeMethod) {
        if (object != null) {
            buf.writeBoolean(true);
            writeMethod.accept(buf, object);
        }
        else {
            buf.writeBoolean(false);
        }
    }
}
