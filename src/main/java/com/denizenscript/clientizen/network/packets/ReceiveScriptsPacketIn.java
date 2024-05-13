package com.denizenscript.clientizen.network.packets;

import com.denizenscript.clientizen.Clientizen;
import com.denizenscript.clientizen.network.Codecs;
import com.denizenscript.clientizen.network.PacketIn;
import com.denizenscript.denizencore.DenizenCore;
import com.denizenscript.denizencore.scripts.ScriptHelper;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.Map;

public record ReceiveScriptsPacketIn(Map<String, String> scripts) implements PacketIn {

    public static final CustomPayload.Id<ReceiveScriptsPacketIn> ID = new Id<>(Clientizen.id("set_scripts"));
    public static final PacketCodec<RegistryByteBuf, ReceiveScriptsPacketIn> CODEC = Codecs.readOnly(Codecs.STRING_MAP, ReceiveScriptsPacketIn::new);

    @Override
    public void process() {
        ScriptHelper.buildAdditionalScripts.clear();
        for (Map.Entry<String, String> entry : scripts.entrySet()) {
            ScriptHelper.buildAdditionalScripts.add(scripts -> scripts.add(YamlConfiguration.load(ScriptHelper.clearComments(entry.getKey(), entry.getValue(), true))));
        }
        DenizenCore.reloadScripts(true, null);
    }

    @Override
    public Id<ReceiveScriptsPacketIn> getId() {
        return ID;
    }
}
