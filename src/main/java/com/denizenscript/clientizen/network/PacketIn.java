package com.denizenscript.clientizen.network;

import net.minecraft.network.packet.CustomPayload;

public interface PacketIn extends CustomPayload {

    void process();
}
