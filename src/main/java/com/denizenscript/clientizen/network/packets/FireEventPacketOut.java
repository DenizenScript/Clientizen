package com.denizenscript.clientizen.network.packets;

import com.denizenscript.clientizen.network.PacketOut;
import io.netty.buffer.ByteBuf;

import java.util.Map;

public class FireEventPacketOut extends PacketOut {

    public FireEventPacketOut(String id, Map<String, String> data) {
        this.id = id;
        this.data = data;
    }

    String id;
    Map<String, String> data;

    @Override
    public void writeTo(ByteBuf buf) {
        writeString(buf, id);
        writeStringMap(buf, data);
    }

    @Override
    public String getName() {
        return "fire_event";
    }
}
