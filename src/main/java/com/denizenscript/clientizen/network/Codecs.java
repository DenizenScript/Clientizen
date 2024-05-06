package com.denizenscript.clientizen.network;

import com.mojang.datafixers.util.Function3;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class Codecs {

    public static final PacketCodec<RegistryByteBuf, String> STRING = PacketCodec.ofStatic(Codecs::writeString, Codecs::readString);
    public static final PacketCodec<RegistryByteBuf, Map<String, String>> STRING_MAP = PacketCodec.ofStatic(Codecs::writeStringMap, Codecs::readStringMap);

    public static <T extends ByteBuf, R> PacketCodec<T, R> nullable(PacketCodec<T, R> codec) {
        return PacketCodec.ofStatic((buf, value) -> {
            if (value != null) {
                buf.writeBoolean(true);
                codec.encode(buf, value);
            }
            else {
                buf.writeBoolean(false);
            }
        }, buf -> buf.readBoolean() ? codec.decode(buf) : null);
    }

    private static <T, R> Function<T, R> noWriteFunction() {
        return param -> {
            throw new UnsupportedOperationException("Trying to write read-only codec.");
        };
    }

    public static <P extends PacketIn, CT> PacketCodec<RegistryByteBuf, P> readOnly(PacketCodec<RegistryByteBuf, CT> codec, Function<CT, P> constructor) {
        return codec.xmap(constructor, noWriteFunction());
    }

    public static <P extends PacketIn, CT1, CT2> PacketCodec<RegistryByteBuf, P> readOnly(PacketCodec<RegistryByteBuf, CT1> codec1, PacketCodec<RegistryByteBuf, CT2> codec2, BiFunction<CT1, CT2, P> constructor) {
        return PacketCodec.tuple(codec1, noWriteFunction(), codec2, noWriteFunction(), constructor);
    }

    public static <P extends PacketIn, CT1, CT2, CT3> PacketCodec<RegistryByteBuf, P> readOnly(PacketCodec<RegistryByteBuf, CT1> codec1, PacketCodec<RegistryByteBuf, CT2> codec2, PacketCodec<RegistryByteBuf, CT3> codec3, Function3<CT1, CT2, CT3, P> constructor) {
        return PacketCodec.tuple(codec1, noWriteFunction(), codec2, noWriteFunction(), codec3, noWriteFunction(), constructor);
    }

    private static <T> T noRead() {
        throw new UnsupportedOperationException("Trying to read with write-only codec.");
    }

    public static <P extends PacketOut> PacketCodec<RegistryByteBuf, P> noData(Supplier<P> constructor) {
        return PacketCodec.ofStatic((buf, value) -> constructor.get(), buf -> noRead());
    }

    public static <P extends PacketOut, CT> PacketCodec<RegistryByteBuf, P> writeOnly(PacketCodec<RegistryByteBuf, CT> codec, Function<P, CT> getter) {
        return codec.xmap(value -> noRead(), getter);
    }

    public static <P extends PacketOut, CT1, CT2> PacketCodec<RegistryByteBuf, P> writeOnly(PacketCodec<RegistryByteBuf, CT1> codec1, Function<P, CT1> getter1, PacketCodec<RegistryByteBuf, CT2> codec2, Function<P, CT2> getter2) {
        return PacketCodec.tuple(codec1, getter1, codec2, getter2, (value1, value2) -> noRead());
    }

    public static <P extends PacketOut, CT1, CT2, CT3> PacketCodec<RegistryByteBuf, P> writeOnly(PacketCodec<RegistryByteBuf, CT1> codec1, Function<P, CT1> getter1, PacketCodec<RegistryByteBuf, CT2> codec2, Function<P, CT2> getter2, PacketCodec<RegistryByteBuf, CT3> codec3, Function<P, CT3> getter3) {
        return PacketCodec.tuple(codec1, getter1, codec2, getter2, codec3, getter3, (value1, value2, value3) -> noRead());
    }

    public static void writeString(ByteBuf buf, String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
    }

    public static String readString(ByteBuf buf) {
        byte[] bytes = new byte[buf.readInt()];
        buf.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static void writeStringMap(ByteBuf buf, Map<String, String> stringMap) {
        buf.writeInt(stringMap.size());
        for (Map.Entry<String, String> entry : stringMap.entrySet()) {
            writeString(buf, entry.getKey());
            writeString(buf, entry.getValue());
        }
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
}
