package com.denizenscript.clientizen.network.packets;

import com.denizenscript.clientizen.network.PacketOut;

public class SendConfirmationPacketOut extends PacketOut {

    @Override
    public String getName() {
        return "receive_confirmation";
    }
}
