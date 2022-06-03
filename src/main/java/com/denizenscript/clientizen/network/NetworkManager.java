package com.denizenscript.clientizen.network;

import com.denizenscript.clientizen.util.debugging.Debug;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

public class NetworkManager {

	public static NetworkManager instance;
	private static final String NAMESPACE = "clientizen";

	public NetworkManager() {
		instance = this;
		registerInChannel(Channel.RECIVE_CONFIRM_REQUEST);
		Debug.log("NetworkManager", "NetworkManager initialized");
	}

	public void registerInChannel(Channel channel) {
		Debug.log("NetworkManager", "Registering in channel " + channel);
		ClientPlayNetworking.registerGlobalReceiver(channel.getIdentifier(), (client, handler, buf, responseSender) -> {
			handlePluginMessage(channel, client, buf);
		});
	}

	public void handlePluginMessage(Channel channel, MinecraftClient client, PacketByteBuf buf) {
		Debug.log("NetworkManager", "Received plugin message on channel '" + channel + "'");
		switch (channel) {
			case RECIVE_CONFIRM_REQUEST:
				send(Channel.SEND_CONFIRM, null);
		}
	}

	public void send(Channel channel, DataSerializer serializer) {
		Identifier identifier = channel.getIdentifier();
		if (!ClientPlayNetworking.canSend(identifier)) {
			Debug.echoError("Cannot send to channel " + channel);
			return;
		}
		Debug.log("NetworkManager", "Sending plugin message on channel '" + channel + "'");
		ClientPlayNetworking.send(identifier, serializer == null ? PacketByteBufs.empty() : serializer.getByteBuf());
	}

	enum Channel {
		RECIVE_CONFIRM_REQUEST("request_confirmation"),
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
