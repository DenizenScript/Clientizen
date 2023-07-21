package com.denizenscript.clientizen.scripts.commands;

import com.denizenscript.clientizen.network.NetworkManager;
import com.denizenscript.clientizen.network.packets.FireEventPacketOut;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.MapTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.scripts.commands.generator.ArgDefaultNull;
import com.denizenscript.denizencore.scripts.commands.generator.ArgName;
import com.denizenscript.denizencore.scripts.commands.generator.ArgPrefixed;
import com.denizenscript.denizencore.utilities.text.StringHolder;

import java.util.HashMap;
import java.util.Map;

public class ServerEventCommand extends AbstractCommand {

    // <--[command]
    // @Name ServerEvent
    // @Syntax serverevent [id:<id>] (data:<map>)
    // @Required 1
    // @Maximum 2
    // @Short Sends the server an event.
    // @Group core
    //
    // @Description
    // Sends the server the client is currently connected to an event, potentially including extra data.
    //
    // Input is an ID (an identifier to be used by the server to recognize the event, choose a constant name to use), and an optional MapTag of context data.
    //
    // Note that the server must be running Depenizen with the Clientizen Bridge enabled to receive events.
    //
    // @Tags
    // None
    //
    // @Usage
    // Use to send the server an event with the id "something_happened"
    // - serverevent id:something_happened
    //
    // @Usage
    // Use to send the server an event with the id "user" and supply a context map of basic data.
    // - serverevent id:user data:[name=Aya;message=Hello]
    //
    // @Usage
    // Use to send the server an event with the id "something_happened" and supply a context map of more interesting data.
    // - definemap context:
    //     Denizen: scripting
    //     food: waffle
    //     loaded_entities: <client.loaded_entities.size>
    // - customevent id:something_happened context:<[context]>
    //
    // -->

    public ServerEventCommand() {
        setName("serverevent");
        setSyntax("serverevent [id:<id>] (data:<map>)");
        setRequiredArguments(1, 2);
        autoCompile();
    }

    public static void autoExecute(ScriptEntry scriptEntry,
                                   @ArgPrefixed @ArgName("id") String id,
                                   @ArgDefaultNull @ArgPrefixed @ArgName("data") MapTag dataInput) {
        Map<String, String> data = new HashMap<>();
        if (dataInput != null) {
            for (Map.Entry<StringHolder, ObjectTag> entry : dataInput.map.entrySet()) {
                data.put(entry.getKey().str, entry.getValue().savable());
            }
        }
        NetworkManager.send(new FireEventPacketOut(id, data));
    }
}
