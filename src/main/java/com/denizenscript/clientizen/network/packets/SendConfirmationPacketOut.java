package com.denizenscript.clientizen.network.packets;

import com.denizenscript.clientizen.Clientizen;
import com.denizenscript.clientizen.network.Codecs;
import com.denizenscript.clientizen.network.PacketOut;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class SendConfirmationPacketOut implements PacketOut {

    public static final Type<SendConfirmationPacketOut> ID = new Type<>(Clientizen.id("confirmation"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SendConfirmationPacketOut> CODEC = Codecs.noData(SendConfirmationPacketOut::new);

    @Override
    public Type<SendConfirmationPacketOut> type() {
        return ID;
    }
}
