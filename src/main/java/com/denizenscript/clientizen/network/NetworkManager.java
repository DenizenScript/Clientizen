package com.denizenscript.clientizen.network;

import com.denizenscript.denizencore.utilities.debugging.Debug;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.client.ClientPlayConnectionEvents;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

public class NetworkManager {

	public static NetworkManager instance;
	private static final String NAMESPACE = "clientizen";

	public NetworkManager() {
		Debug.log("NetworkManager", "Initializing NetworkManager...");
		instance = this;
		ClientPlayConnectionEvents.JOIN.register(((handler, sender, client) -> {
			Debug.log("NetworkManager", "Sending confirmation packet...");
			send(Channel.SEND_CONFIRM, null);
		}));
	}

	public void handlePluginMessage(Channel channel, MinecraftClient client, PacketByteBuf buf) {
		Debug.log("NetworkManager", "Received plugin message on channel '" + channel + "'");
	}

	public void registerInChannel(Channel channel) {
		Debug.log("NetworkManager", "Registering in channel " + channel);
		ClientPlayNetworking.registerGlobalReceiver(channel.getIdentifier(), (client, handler, buf, responseSender) -> {
			handlePluginMessage(channel, client, buf);
		});
	}

	public void send(Channel channel, DataSerializer serializer) {
		Identifier identifier = channel.getIdentifier();
//		 TODO: Re-add this check? returns false on ClientPlayConnectionEvents.JOIN, might be too early
//		if (!ClientPlayNetworking.canSend(identifier)) {
//			Debug.echoError("Cannot send to channel " + channel);
//			return;
//		}
		Debug.log("NetworkManager", "Sending plugin message on channel '" + channel + "'");
		ClientPlayNetworking.send(identifier, serializer == null ? PacketByteBufs.empty() : serializer.getByteBuf());
	}

	enum Channel {
		SEND_CONFIRM("receive_confirmation");

		private final String channel;

		Channel(String channel) {
			this.channel = NAMESPACE + ":" + channel;
		}

		public String getChannel() {
			return channel;
		}

		public Identifier getIdentifier() {
			return new Identifier(channel);
		}

		@Override
		public String toString() {
			return channel;
		}
	}
}
