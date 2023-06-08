package com.denizenscript.clientizen.network;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class PacketIn {

    public abstract void process(ByteBuf data);

    public abstract String getName();

    public static String readString(ByteBuf buf) {
        byte[] bytes = new byte[buf.readInt()];
        buf.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static Map<String, String> readStringMap(ByteBuf buf) {
        int size = buf.readInt();
        Map<String, String> stringMap = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            String key = readString(buf);
            String value = readString(buf);
            stringMap.put(key, value);
        }
        return stringMap;
    }

    public static  <T> T readNullable(ByteBuf buf, Function<ByteBuf, T> readMethod) {
        return buf.readBoolean() ? readMethod.apply(buf) : null;
    }
}
