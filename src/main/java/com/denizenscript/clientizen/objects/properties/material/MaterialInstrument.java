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

    public static final EnumProperty<?>[] handledProperties = {Properties.INSTRUMENT};

    enum BukkitInstruments implements EnumStringIdentifiable {
        PIANO, BASS_DRUM, SNARE_DRUM, STICKS, BASS_GUITAR, FLUTE, BELL, GUITAR, CHIME, XYLOPHONE, IRON_XYLOPHONE, COW_BELL, DIDGERIDOO, BIT, BANJO, PLING, ZOMBIE, SKELETON, CREEPER, DRAGON, WITHER_SKELETON, PIGLIN, CUSTOM_HEAD;
    }

    public static void register() {
        convertEnum(Properties.INSTRUMENT, BukkitInstruments.class);
        autoRegister("instrument", MaterialInstrument.class);
    }
}
