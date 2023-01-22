package com.denizenscript.clientizen.network;

import com.denizenscript.denizencore.utilities.debugging.Debug;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.util.Identifier;

public class NetworkManager {

	public static void init() {
		Debug.log("Initializing NetworkManager...");
		ClientPlayConnectionEvents.JOIN.register(((handler, sender, client) -> {
			Debug.log("Sending join confirmation packet...");
			send(Channels.SEND_CONFIRM, null);
		}));
		ScriptNetworking.init();
	}

	public static void registerInChannel(Identifier channel, ClientizenReceiver receiver) {
		if (!ClientPlayNetworking.registerGlobalReceiver(channel, (client, handler, buf, responseSender) -> {
			Debug.log("Received plugin message on channel " + channel);
			receiver.receive(new DataDeserializer(buf));
		})) {
			Debug.echoError("Tried registering plugin channel '" + channel + "', but it is already registered!");
		}
	}

	public static void send(Identifier channel, DataSerializer serializer) {
//		TODO: re-add this check? doesn't work on ClientPlayConnectionEvents.JOIN, might be too early
//		if (!ClientPlayNetworking.canSend(identifier)) {
//			Debug.echoError("Cannot send to channel " + channel);
//			return;
//		}
		Debug.log("Sending message on channel " + channel);
		ClientPlayNetworking.send(channel, serializer != null ? serializer.byteBuf : PacketByteBufs.empty());
	}

	@FunctionalInterface
	public interface ClientizenReceiver {
		void receive(DataDeserializer message);
	}
}
