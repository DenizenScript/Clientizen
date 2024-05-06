package com.denizenscript.clientizen.network.packets;

import com.denizenscript.clientizen.Clientizen;
import com.denizenscript.clientizen.network.Codecs;
import com.denizenscript.clientizen.network.PacketOut;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Map;

public record FireEventPacketOut(String id, Map<String, String> data) implements PacketOut {

    public static final Id<FireEventPacketOut> ID = new Id<>(Clientizen.id("fire_event"));
    public static final PacketCodec<RegistryByteBuf, FireEventPacketOut> CODEC = Codecs.writeOnly(Codecs.STRING, FireEventPacketOut::id, Codecs.STRING_MAP, FireEventPacketOut::data);

    @Override
    public Id<FireEventPacketOut> getId() {
        return ID;
    }
}
