package com.denizenscript.clientizen.objects.properties.item;

import com.denizenscript.clientizen.objects.ItemTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.PropertyParser;

public class ItemServerScript extends ItemProperty<ElementTag> {

    // <--[tag]
    // @attribute <ItemTag.server_script>
    // @returns ElementTag
    // @group scripts
    // @description
    // Returns the name of an item's server-side item script, if it has one.
    // -->

    public static boolean describes(ItemTag item) {
        return true;
    }

    @Override
    public ElementTag getPropertyValue() {
        return object.script != null ? new ElementTag(object.script) : null;
    }

    @Override
    public String getPropertyId() {
        return "script";
    }

    @Override
    public void setPropertyValue(ElementTag value, Mechanism mechanism) {
        if (object.script != null) {
            mechanism.echoError("Cannot set a script on an item that already has one.");
            return;
        }
        object.script = value.asString();
    }

    public static void register() {
        PropertyParser.registerTag(ItemServerScript.class, ElementTag.class, "server_script", (attribute, prop) -> prop.getPropertyValue());
        // Intentionally undocumented, internal purpose only
        PropertyParser.registerMechanism(ItemServerScript.class, ElementTag.class, "script", (prop, mechanism, input) -> prop.setPropertyValue(input, mechanism));
    }
}
