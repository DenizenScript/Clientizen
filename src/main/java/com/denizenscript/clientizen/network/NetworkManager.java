package com.denizenscript.clientizen.network;

import com.denizenscript.denizencore.DenizenCore;
import com.denizenscript.denizencore.scripts.ScriptHelper;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.util.Identifier;

import java.util.Map;

public class NetworkManager {

	public static void init() {
		Debug.log("Initializing NetworkManager...");
		ClientPlayConnectionEvents.JOIN.register(((handler, sender, client) -> {
			Debug.log("Sending join confirmation packet...");
			send(Channels.SEND_CONFIRM, null);
		}));

		// Register receivers
		registerInChannel(Channels.SET_SCRIPTS, (message) -> {
			Map<String, String> scripts = message.readStringMap();
			DenizenCore.runOnMainThread(() -> {
				ScriptHelper.additionalScripts.clear();
				for (Map.Entry<String, String> entry : scripts.entrySet()) {
					ScriptHelper.additionalScripts.add(YamlConfiguration.load(ScriptHelper.clearComments(entry.getKey(), entry.getValue(), true)));
				}
				DenizenCore.reloadScripts();
			});
		});
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
