package com.denizenscript.clientizen.network.packets;

import com.denizenscript.clientizen.network.PacketIn;
import com.denizenscript.denizencore.DenizenCore;
import com.denizenscript.denizencore.scripts.ScriptHelper;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import io.netty.buffer.ByteBuf;

import java.util.Map;

public class ReceiveScriptsPacketIn extends PacketIn {

    @Override
    public void process(ByteBuf data) {
        Map<String, String> scriptsMap = readStringMap(data);
        DenizenCore.runOnMainThread(() -> {
            ScriptHelper.buildAdditionalScripts.clear();
            for (Map.Entry<String, String> entry : scriptsMap.entrySet()) {
                ScriptHelper.buildAdditionalScripts.add(scripts -> scripts.add(YamlConfiguration.load(ScriptHelper.clearComments(entry.getKey(), entry.getValue(), true))));
            }
            DenizenCore.reloadScripts(true, null);
        });
    }

    @Override
    public String getName() {
        return "set_scripts";
    }
}
