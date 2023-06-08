package com.denizenscript.clientizen.network.packets;

import com.denizenscript.clientizen.network.PacketIn;
import com.denizenscript.denizencore.DenizenCore;
import com.denizenscript.denizencore.objects.ObjectFetcher;
import com.denizenscript.denizencore.objects.core.ScriptTag;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.CoreConfiguration;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.denizenscript.denizencore.utilities.ScriptUtilities;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import io.netty.buffer.ByteBuf;

import java.util.Map;

public class RunScriptPacketIn extends PacketIn {

    @Override
    public void process(ByteBuf data) {
        String scriptStr = readString(data);
        String path = readNullable(data, PacketIn::readString);
        Map<String, String> defMap = readStringMap(data);
        DenizenCore.runOnMainThread(() -> {
            ScriptTag script = ScriptTag.valueOf(scriptStr, CoreUtilities.noDebugContext);
            if (script == null) {
                if (CoreConfiguration.debugExtraInfo) {
                    Debug.echoError("Invalid script name to run received from server: " + scriptStr + ".");
                }
                return;
            }
            ScriptUtilities.createAndStartQueue(script.getContainer(), path, null, null, queue -> {
                TagContext context = DenizenCore.implementation.getTagContext(script.getContainer());
                for (Map.Entry<String, String> entry : defMap.entrySet()) {
                    queue.addDefinition(entry.getKey(), ObjectFetcher.pickObjectFor(entry.getValue(), context));
                }
            }, null, "SERVER_RUN:" + script.getContainer().getName(), null, null);
        });
    }

    @Override
    public String getName() {
        return "run_script";
    }
}
