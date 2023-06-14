package com.denizenscript.clientizen.objects.properties.material.internal;

import com.denizenscript.clientizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class MaterialEnumProperty extends MaterialMinecraftProperty {

    public static final Map<EnumProperty<?>, Conversion<?, ?>> conversions = new HashMap<>();

    record Conversion<IT extends Enum<IT> & StringIdentifiable, ET extends Enum<ET> & StringIdentifiable>(Class<ET> externalType, ET[] externalTypeConstants, IT[] internalTypeConstants,
                                                                                                          IT toRemove) {}

    public static <IT extends Enum<IT> & StringIdentifiable, ET extends Enum<ET> & StringIdentifiable> void convertEnum(EnumProperty<IT> internalProperty, Class<ET> externalType) {
        conversions.put(internalProperty, new Conversion<>(externalType, externalType.getEnumConstants(), internalProperty.getType().getEnumConstants(), null));
    }

    public static <IT extends Enum<IT> & StringIdentifiable> void removeSingleInstance(EnumProperty<IT> internalProperty, IT toRemove) {
        conversions.put(internalProperty, new Conversion<>(null, null, null, toRemove));
    }

    @Override
    public Comparable processPropertyValue(Comparable value) {
        Conversion<?, ?> conversion = conversions.get(internalProperty);
        if (conversion == null) {
            return value;
        }
        if (conversion.externalType != null) {
            return conversion.externalTypeConstants[((Enum<?>) value).ordinal()];
        }
        return value == conversion.toRemove ? null : value;
    }

    @Override
    public Comparable parsePropertyValue(ElementTag input) {
        Conversion<?, ?> conversion = conversions.get(internalProperty);
        if (conversion == null) {
            return super.parsePropertyValue(input);
        }
        if (conversion.externalType != null) {
            Enum<?> external = input.asEnum(conversion.externalType);
            return external == null ? null : conversion.internalTypeConstants[external.ordinal()];
        }
        return input.asLowerString().equals(conversion.toRemove.asString()) ? null : super.parsePropertyValue(input);
    }


    public interface EnumStringIdentifiable extends StringIdentifiable {

        @Override
        default String asString() {
            return name();
        }

        String name();
    }

    public static void registerProperty(Class<? extends MaterialEnumProperty> propertyClass, EnumProperty<?>... properties) {
        PropertyParser.registerPropertyGetter(new MaterialMinecraftPropertyGetter(propertyClass, properties), MaterialTag.class, null, null, propertyClass);
    }
}
