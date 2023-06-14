package com.denizenscript.clientizen.objects.properties.material.internal;

import com.denizenscript.clientizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class MaterialEnumProperty extends MaterialMinecraftProperty {

    public static final Map<EnumProperty<?>, Conversion<?, ?>> conversions = new HashMap<>();

    record Conversion<IT extends Enum<IT> & StringIdentifiable, ET extends Enum<ET> & StringIdentifiable>(Class<ET> externalTypeClass, ET[] externalTypeConstants, IT[] internalTypeConstants,
                                                                                                          IT toRemove) {}

    public static <IT extends Enum<IT> & StringIdentifiable, ET extends Enum<ET> & StringIdentifiable> void convertEnum(EnumProperty<IT> internalProperty, Class<ET> externalType) {
        conversions.put(internalProperty, new Conversion<>(externalType, externalType.getEnumConstants(), internalProperty.getType().getEnumConstants(), null));
    }

    public static <IT extends Enum<IT> & StringIdentifiable, ET extends Enum<ET> & StringIdentifiable> void removeSingleInstance(EnumProperty<IT> internalProperty, IT toRemove) {
        conversions.put(internalProperty, new Conversion<>(null, null, null, toRemove));
    }

    @Override
    public Comparable processValue(Comparable value) {
        Conversion<?, ?> conversion = conversions.get(internalProperty);
        if (conversion == null) {
            return value;
        }
        if (conversion.externalTypeClass != null) {
            return conversion.externalTypeConstants[((Enum<?>) value).ordinal()];
        }
        return value == conversion.toRemove ? null : value;
    }

    @Override
    public Comparable parseInput(ElementTag input) {
        Conversion<?, ?> conversion = conversions.get(internalProperty);
        if (conversion == null) {
            return super.parseInput(input);
        }
        if (conversion.externalTypeClass != null) {
            Enum<?> external = input.asEnum(conversion.externalTypeClass);
            return external == null ? null : conversion.internalTypeConstants[external.ordinal()];
        }
        return input.asLowerString().equals(conversion.toRemove.asString()) ? null : super.parseInput(input);
    }


    public interface EnumStringIdentifiable extends StringIdentifiable {

        @Override
        default String asString() {
//            return CoreUtilities.toLowerCase(name());
            return name();
        }

        String name();
    }

    public static void registerProperty(Class<? extends MaterialEnumProperty> propertyClass, EnumProperty<?>... properties) {
        PropertyParser.registerPropertyGetter(new MaterialMinecraftPropertyGetter(propertyClass, properties), MaterialTag.class, null, null, propertyClass);
    }
}
