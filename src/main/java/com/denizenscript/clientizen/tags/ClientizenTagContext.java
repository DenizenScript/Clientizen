package com.denizenscript.clientizen.tags;

import com.denizenscript.clientizen.util.impl.ClientizenScriptEntryData;
import com.denizenscript.denizencore.objects.core.ScriptTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import com.denizenscript.denizencore.scripts.containers.ScriptContainer;
import com.denizenscript.denizencore.tags.TagContext;

public class ClientizenTagContext extends TagContext {

    public ClientizenTagContext(ScriptEntry entry) {
        super(entry);
    }

    public ClientizenTagContext(boolean debug, ScriptEntry entry, ScriptTag script) {
        super(debug, entry, script);
    }

    public ClientizenTagContext(ScriptContainer container) {
        super(container == null || container.shouldDebug(), null, container == null ? null : new ScriptTag(container));
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        ClientizenScriptEntryData entryData = new ClientizenScriptEntryData();
        entryData.scriptEntry = entry;
        return entryData;
    }
}
