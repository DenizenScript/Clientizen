package com.denizenscript.clientizen.objects.properties.material.internal;

import com.denizenscript.clientizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class MaterialEnumProperty extends MaterialMinecraftProperty {

    public static final Map<EnumProperty<?>, Conversion<?, ?>> conversions = new HashMap<>();

    record Conversion<IT extends Enum<IT> & StringIdentifiable, ET extends Enum<ET> & StringIdentifiable>(
            Class<ET> externalType, ET[] externalTypeConstants, IT[] internalTypeConstants, // Entire enum conversion
            Set<String> toRemove) {} // Value removal

    public static <IT extends Enum<IT> & StringIdentifiable, ET extends Enum<ET> & StringIdentifiable> void convertEnum(EnumProperty<IT> internalProperty, Class<ET> externalType) {
        conversions.put(internalProperty, new Conversion<>(externalType, externalType.getEnumConstants(), internalProperty.getType().getEnumConstants(), null));
    }

    public static <IT extends Enum<IT> & StringIdentifiable> void removeValues(EnumProperty<IT> internalProperty, IT... toRemove) {
        conversions.put(internalProperty, new Conversion<>(null, null, null, Arrays.stream(toRemove).map(StringIdentifiable::asString).collect(Collectors.toCollection(HashSet::new))));
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
        return conversion.toRemove.contains(((StringIdentifiable) value).asString()) ? null : value;
    }

    @Override
    public Comparable parsePropertyValue(ElementTag input, Mechanism mechanism) {
        Conversion<?, ?> conversion = conversions.get(internalProperty);
        if (conversion == null) {
            return super.parsePropertyValue(input, mechanism);
        }
        if (conversion.externalType != null) {
            Enum<?> external = input.asEnum(conversion.externalType);
            if (external == null) {
                return null;
            }
            Enum<?> internal = conversion.internalTypeConstants[external.ordinal()];
            return internalProperty.getValues().contains(internal) ? internal : null;
        }
        return conversion.toRemove.contains(input.asLowerString()) ? null : super.parsePropertyValue(input, mechanism);
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
