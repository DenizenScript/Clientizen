package com.denizenscript.clientizen.objects.properties.material;

import com.denizenscript.clientizen.objects.properties.material.internal.MaterialEnumProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;

public class MaterialInstrument extends MaterialEnumProperty {

    // <--[property]
    // @object MaterialTag
    // @name instrument
    // @input ElementTag
    // @description
    // The instrument played by a note block, see <@link url https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Instrument.html>
    // -->

    // TODO: material properties - bukkit conversion
    public static EnumProperty<?>[] handledProperties = {Properties.INSTRUMENT};

    public static void register() {
        autoRegister("instrument", MaterialInstrument.class);
    }
}
