package com.denizenscript.clientizen.network;

import com.denizenscript.clientizen.Clientizen;
import com.denizenscript.clientizen.util.Utilities;
import com.denizenscript.denizencore.DenizenCore;
import com.denizenscript.denizencore.scripts.ScriptHelper;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import java.util.Map;

public class NetworkManager {

	public static NetworkManager instance;

	public static void log(String message) {
		Debug.log("NetworkManager", message);
	}

	public NetworkManager() {
		log("Initializing NetworkManager...");
		registerReceivers();
		ClientPlayConnectionEvents.JOIN.register(((handler, sender, client) -> {
			log("Sending join confirmation packet...");
			send(Channel.SEND_CONFIRM, null);
		}));
	}

	public void registerReceivers() {
		Channel.SET_SCRIPTS.register((client, data) -> {
			Map<String, String> scripts = data.readStringMap();
			Utilities.runOnRenderThread(() -> {
				ScriptHelper.additionalScripts.clear();
				for (Map.Entry<String, String> entry : scripts.entrySet()) {
					ScriptHelper.additionalScripts.add(YamlConfiguration.load(
							ScriptHelper.clearComments(entry.getKey(), entry.getValue(), true)
					));
				}
				DenizenCore.reloadScripts();
			});
		});
	}

	public void send(Channel channel, DataSerializer serializer) {
//		 TODO: Re-add this check? returns false on ClientPlayConnectionEvents.JOIN, might be too early
//		if (!ClientPlayNetworking.canSend(identifier)) {
//			Debug.echoError("Cannot send to channel " + channel);
//			return;
//		}
		log("Sending message on channel " + channel);
		ClientPlayNetworking.send(channel.id, serializer == null ? PacketByteBufs.empty() : serializer.getByteBuf());
	}

	public enum Channel {

		SEND_CONFIRM("receive_confirmation"),
		SET_SCRIPTS("set_scripts");

		public final Identifier id;

		Channel(String id) {
			this.id = Clientizen.id(id);
		}

		public void register(ClientizenReceiver receiver) {
			log("Registering receiver on channel " + this);
			ClientPlayNetworking.registerGlobalReceiver(id, (client, handler, buf, responseSender) -> {
				log("Received plugin message on channel " + this);
				receiver.receive(client, new DataDeserializer(buf));
			});
		}

		@Override
		public String toString() {
			return "'" + id.toString() + "'";
		}
	}

	@FunctionalInterface
	public interface ClientizenReceiver {

		void receive(MinecraftClient client, DataDeserializer data);
	}
}
