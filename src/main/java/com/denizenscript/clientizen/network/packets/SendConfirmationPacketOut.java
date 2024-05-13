package com.denizenscript.clientizen.network.packets;

import com.denizenscript.clientizen.Clientizen;
import com.denizenscript.clientizen.network.Codecs;
import com.denizenscript.clientizen.network.PacketOut;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public class SendConfirmationPacketOut implements PacketOut {

    public static final Id<SendConfirmationPacketOut> ID = new Id<>(Clientizen.id("confirmation"));
    public static final PacketCodec<RegistryByteBuf, SendConfirmationPacketOut> CODEC = Codecs.noData(SendConfirmationPacketOut::new);

    @Override
    public Id<SendConfirmationPacketOut> getId() {
        return ID;
    }
}
