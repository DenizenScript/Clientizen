package com.denizenscript.clientizen.network;

import net.minecraft.util.Identifier;

public class Channels {
	public static final String CHANNEL_NAMESPACE = "clientizen";
	public static final Identifier SEND_CONFIRM = new Identifier(CHANNEL_NAMESPACE, "receive_confirmation");
	public static final Identifier SET_SCRIPTS = new Identifier(CHANNEL_NAMESPACE, "set_scripts");
}
