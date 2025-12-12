package com.denizenscript.clientizen.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

// Technically redundant, but exists for parity with ingoing packets & to limit relevant systems to Clientizen packets
public interface PacketOut extends CustomPacketPayload {}
