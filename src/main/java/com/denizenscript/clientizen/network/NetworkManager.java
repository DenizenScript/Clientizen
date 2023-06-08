package com.denizenscript.clientizen.network;

import com.denizenscript.clientizen.Clientizen;
import com.denizenscript.clientizen.network.packets.ReceiveScriptsPacketIn;
import com.denizenscript.clientizen.network.packets.RunScriptPacketIn;
import com.denizenscript.clientizen.network.packets.SendConfirmationPacketOut;
import com.denizenscript.denizencore.utilities.CoreConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class NetworkManager {

    public static void init() {
        Debug.log("Initializing NetworkManager...");
        registerInPacket(new ReceiveScriptsPacketIn());
        registerInPacket(new RunScriptPacketIn());
    }

    public static void onConnect() {
        debugNetwork("Sending join confirmation packet...");
        send(new SendConfirmationPacketOut());
    }

    public static void registerInPacket(PacketIn packet) {
        final Identifier channel = Clientizen.id(packet.getName());
        if (!ClientPlayNetworking.registerGlobalReceiver(channel, (client, handler, buf, responseSender) -> {
            debugNetwork("Received plugin message on channel " + channel);
            packet.process(buf);
        })) {
            Debug.echoError("Tried registering in packet on channel '" + channel + "', but a packet is already registered for that channel!");
        }
    }

    public static void send(PacketOut packet) {
//        TODO: re-add this check? doesn't work on ClientPlayConnectionEvents.JOIN, might be too early
//        if (!ClientPlayNetworking.canSend(identifier)) {
//            Debug.echoError("Cannot send to channel " + channel);
//            return;
//        }
        debugNetwork("Sending message on channel " + packet.channel);
        PacketByteBuf buf = PacketByteBufs.create();
        packet.writeTo(buf);
        ClientPlayNetworking.send(packet.channel, buf);
    }

    public static void debugNetwork(String debug) {
        if (CoreConfiguration.debugExtraInfo) {
            Debug.log(debug);
        }
    }
}
