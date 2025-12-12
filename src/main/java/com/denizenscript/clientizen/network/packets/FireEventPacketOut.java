package com.denizenscript.clientizen.network.packets;

import com.denizenscript.clientizen.Clientizen;
import com.denizenscript.clientizen.network.Codecs;
import com.denizenscript.clientizen.network.PacketOut;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Map;

public record FireEventPacketOut(String id, Map<String, String> data) implements PacketOut {

    public static final Type<FireEventPacketOut> ID = new Type<>(Clientizen.id("fire_event"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FireEventPacketOut> CODEC = Codecs.writeOnly(Codecs.STRING, FireEventPacketOut::id, Codecs.STRING_MAP, FireEventPacketOut::data);

    @Override
    public Type<FireEventPacketOut> type() {
        return ID;
    }
}
