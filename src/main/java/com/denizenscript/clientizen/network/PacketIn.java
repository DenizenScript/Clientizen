package com.denizenscript.clientizen.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface PacketIn extends CustomPacketPayload {

    void process();
}
