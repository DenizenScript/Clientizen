package com.denizenscript.clientizen.network;

import com.denizenscript.clientizen.network.packets.FireEventPacketOut;
import com.denizenscript.clientizen.network.packets.ReceiveScriptsPacketIn;
import com.denizenscript.clientizen.network.packets.RunScriptPacketIn;
import com.denizenscript.clientizen.network.packets.SendConfirmationPacketOut;
import com.denizenscript.denizencore.utilities.CoreConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class NetworkManager {

    public static void init() {
        Debug.log("Initializing NetworkManager...");
        registerInPacket(ReceiveScriptsPacketIn.ID, ReceiveScriptsPacketIn.CODEC);
        registerInPacket(RunScriptPacketIn.ID, RunScriptPacketIn.CODEC);
        registerOutPacket(FireEventPacketOut.ID, FireEventPacketOut.CODEC);
        registerOutPacket(SendConfirmationPacketOut.ID, SendConfirmationPacketOut.CODEC);
    }

    public static void onConnect() {
        send(new SendConfirmationPacketOut());
    }

    public static <T extends PacketIn> void registerInPacket(CustomPacketPayload.Type<T> packetId, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        PayloadTypeRegistry.playS2C().register(packetId, codec);
        if (!ClientPlayNetworking.registerGlobalReceiver(packetId, (packet, context) -> {
            debugNetwork("Received {} packet.", packet);
            packet.process();
        })) {
            Debug.echoError("Tried registering in packet on channel '" + packetId.id() + "', but a packet is already registered for that channel!");
        }
    }

    public static <T extends PacketOut> void registerOutPacket(CustomPacketPayload.Type<T> packetId, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        PayloadTypeRegistry.playC2S().register(packetId, codec);
    }

    public static void send(PacketOut packet) {
        if (Minecraft.getInstance().isLocalServer()) {
            debugNetwork("Running in single player, not sending {} packet.", packet);
            return;
        }
//        TODO: re-add this check? doesn't work on ClientPlayConnectionEvents.JOIN, might be too early
//        if (!ClientPlayNetworking.canSend(identifier)) {
//            Debug.echoError("Cannot send to channel " + channel);
//            return;
//        }
        debugNetwork("Sending {} packet.", packet);
        ClientPlayNetworking.send(packet);
    }

    public static void debugNetwork(String debug) {
        if (CoreConfiguration.debugExtraInfo) {
            Debug.log(debug);
        }
    }

    public static void debugNetwork(String debug, CustomPacketPayload packet) {
        if (CoreConfiguration.debugExtraInfo) {
            Debug.log(debug.replace("{}", "'<LG>" + packet.type().id() + "<W>'"));
        }
    }
}
