package com.denizenscript.clientizen.network.packets;

import com.denizenscript.clientizen.Clientizen;
import com.denizenscript.clientizen.network.Codecs;
import com.denizenscript.clientizen.network.PacketIn;
import com.denizenscript.denizencore.DenizenCore;
import com.denizenscript.denizencore.objects.ObjectFetcher;
import com.denizenscript.denizencore.objects.core.ScriptTag;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.CoreConfiguration;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.denizenscript.denizencore.utilities.ScriptUtilities;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Map;

public record RunScriptPacketIn(String scriptName, String scriptPath, Map<String, String> definitions) implements PacketIn {
    public static final Type<RunScriptPacketIn> ID = new Type<>(Clientizen.id("run_script"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RunScriptPacketIn> CODEC = Codecs.readOnly(Codecs.STRING, Codecs.nullable(Codecs.STRING), Codecs.STRING_MAP, RunScriptPacketIn::new);

    @Override
    public void process() {
        ScriptTag script = ScriptTag.valueOf(scriptName, CoreUtilities.noDebugContext);
        if (script == null) {
            if (CoreConfiguration.debugExtraInfo) {
                Debug.echoError("Invalid script name to run received from server: " + scriptName + '.');
            }
            return;
        }
        ScriptUtilities.createAndStartQueue(script.getContainer(), scriptPath, null, null, queue -> {
            TagContext context = DenizenCore.implementation.getTagContext(script.getContainer());
            for (Map.Entry<String, String> entry : definitions.entrySet()) {
                queue.addDefinition(entry.getKey(), ObjectFetcher.pickObjectFor(entry.getValue(), context));
            }
        }, null, "SERVER_RUN:" + script.getContainer().getName(), null, null);
    }

    @Override
    public Type<RunScriptPacketIn> type() {
        return ID;
    }
}
