package com.denizenscript.clientizen.network;

import com.mojang.datafixers.util.Function3;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class Codecs {

    public static final StreamCodec<RegistryFriendlyByteBuf, String> STRING = StreamCodec.of(Codecs::writeString, Codecs::readString);
    public static final StreamCodec<RegistryFriendlyByteBuf, Map<String, String>> STRING_MAP = StreamCodec.of(Codecs::writeStringMap, Codecs::readStringMap);

    public static <T extends ByteBuf, R> StreamCodec<T, R> nullable(StreamCodec<T, R> codec) {
        return StreamCodec.of((buf, value) -> {
            if (value != null) {
                buf.writeBoolean(true);
                codec.encode(buf, value);
            }
            else {
                buf.writeBoolean(false);
            }
        }, buf -> buf.readBoolean() ? codec.decode(buf) : null);
    }

    private static final Function<?, ?> NO_WRITE = param -> {
        throw new UnsupportedOperationException("Trying to write read-only codec.");
    };

    @SuppressWarnings("unchecked")
    private static <T, R> Function<T, R> noWrite() {
        return (Function<T, R>) NO_WRITE;
    }

    public static <P extends PacketIn, CT> StreamCodec<RegistryFriendlyByteBuf, P> readOnly(StreamCodec<RegistryFriendlyByteBuf, CT> codec, Function<CT, P> constructor) {
        return codec.map(constructor, noWrite());
    }

    public static <P extends PacketIn, CT1, CT2> StreamCodec<RegistryFriendlyByteBuf, P> readOnly(StreamCodec<RegistryFriendlyByteBuf, CT1> codec1, StreamCodec<RegistryFriendlyByteBuf, CT2> codec2, BiFunction<CT1, CT2, P> constructor) {
        return StreamCodec.composite(codec1, noWrite(), codec2, noWrite(), constructor);
    }

    public static <P extends PacketIn, CT1, CT2, CT3> StreamCodec<RegistryFriendlyByteBuf, P> readOnly(StreamCodec<RegistryFriendlyByteBuf, CT1> codec1, StreamCodec<RegistryFriendlyByteBuf, CT2> codec2, StreamCodec<RegistryFriendlyByteBuf, CT3> codec3, Function3<CT1, CT2, CT3, P> constructor) {
        return StreamCodec.composite(codec1, noWrite(), codec2, noWrite(), codec3, noWrite(), constructor);
    }

    private static <T> T noRead() {
        throw new UnsupportedOperationException("Trying to read with write-only codec.");
    }

    public static <P extends PacketOut> StreamCodec<RegistryFriendlyByteBuf, P> noData(Supplier<P> constructor) {
        return StreamCodec.of((buf, value) -> constructor.get(), buf -> noRead());
    }

    public static <P extends PacketOut, CT> StreamCodec<RegistryFriendlyByteBuf, P> writeOnly(StreamCodec<RegistryFriendlyByteBuf, CT> codec, Function<P, CT> getter) {
        return codec.map(value -> noRead(), getter);
    }

    public static <P extends PacketOut, CT1, CT2> StreamCodec<RegistryFriendlyByteBuf, P> writeOnly(StreamCodec<RegistryFriendlyByteBuf, CT1> codec1, Function<P, CT1> getter1, StreamCodec<RegistryFriendlyByteBuf, CT2> codec2, Function<P, CT2> getter2) {
        return StreamCodec.composite(codec1, getter1, codec2, getter2, (value1, value2) -> noRead());
    }

    public static <P extends PacketOut, CT1, CT2, CT3> StreamCodec<RegistryFriendlyByteBuf, P> writeOnly(StreamCodec<RegistryFriendlyByteBuf, CT1> codec1, Function<P, CT1> getter1, StreamCodec<RegistryFriendlyByteBuf, CT2> codec2, Function<P, CT2> getter2, StreamCodec<RegistryFriendlyByteBuf, CT3> codec3, Function<P, CT3> getter3) {
        return StreamCodec.composite(codec1, getter1, codec2, getter2, codec3, getter3, (value1, value2, value3) -> noRead());
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
