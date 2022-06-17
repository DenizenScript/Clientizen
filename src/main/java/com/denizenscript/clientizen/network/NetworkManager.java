package com.denizenscript.clientizen.network;

import com.denizenscript.clientizen.util.Utilities;
import com.denizenscript.denizencore.DenizenCore;
import com.denizenscript.denizencore.scripts.ScriptHelper;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.client.ClientPlayConnectionEvents;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

import java.util.Map;

public class NetworkManager {

	public static NetworkManager instance;
	private static final String NAMESPACE = "clientizen";

	public NetworkManager() {
		Debug.log("NetworkManager", "Initializing NetworkManager...");
		instance = this;
		registerInChannel(Channel.SET_SCRIPTS);
		ClientPlayConnectionEvents.JOIN.register(((handler, sender, client) -> {
			Debug.log("NetworkManager", "Sending confirmation packet...");
			send(Channel.SEND_CONFIRM, null);
		}));
	}

	public void handlePluginMessage(Channel channel, MinecraftClient client, DataDeserializer data) {
		Debug.log("NetworkManager", "Received plugin message on channel '" + channel + "'");
		switch (channel) {
			case SET_SCRIPTS:
				Map<String, String> scripts = data.readStringMap();
				Utilities.runOnRenderThread(() -> {
					ScriptHelper.additionalScripts.clear();
					for (Map.Entry<String, String> entry : scripts.entrySet()) {
						ScriptHelper.additionalScripts.add(YamlConfiguration.load(
								ScriptHelper.clearComments(entry.getKey(), entry.getValue(), true)));
					}
					DenizenCore.reloadScripts();
				});
		}
	}

	public void registerInChannel(Channel channel) {
		Debug.log("NetworkManager", "Registering in channel " + channel);
		ClientPlayNetworking.registerGlobalReceiver(channel.getIdentifier(), (client, handler, buf, responseSender) -> {
			handlePluginMessage(channel, client, new DataDeserializer(buf));
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
		SEND_CONFIRM("receive_confirmation"),
		SET_SCRIPTS("set_scripts");

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
