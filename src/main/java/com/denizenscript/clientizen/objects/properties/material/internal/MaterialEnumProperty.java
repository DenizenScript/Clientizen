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

    public static final Map<EnumProperty<?>, Conversion<?, ?>> CONVERSIONS = new HashMap<>();

    public static class Conversion<IT extends Enum<IT> & StringIdentifiable, ET extends Enum<ET> & StringIdentifiable> {
        // Entire enum conversion
        Class<ET> externalType;
        ET[] externalTypeConstants;
        IT[] internalTypeConstants;
        // Value removal
        Set<String> toRemove;

        public boolean shouldRemove(String value) {
            return toRemove != null && toRemove.contains(value);
        }
    }

    public static <IT extends Enum<IT> & StringIdentifiable, ET extends Enum<ET> & StringIdentifiable> void convertEnum(EnumProperty<IT> internalProperty, Class<ET> externalType) {
        Conversion conversion = CONVERSIONS.computeIfAbsent(internalProperty, k -> new Conversion<IT, ET>());
        conversion.externalType = externalType;
        conversion.externalTypeConstants = externalType.getEnumConstants();
        conversion.internalTypeConstants = internalProperty.getType().getEnumConstants();
    }

    public static <IT extends Enum<IT> & StringIdentifiable> void removeValues(EnumProperty<IT> internalProperty, IT... toRemove) {
        CONVERSIONS.computeIfAbsent(internalProperty, k -> new Conversion()).toRemove = Arrays.stream(toRemove).map(StringIdentifiable::asString).collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public Comparable processPropertyValue(Comparable value) {
        //noinspection SuspiciousMethodCalls
        Conversion<?, ?> conversion = CONVERSIONS.get(internalProperty);
        if (conversion == null) {
            return value;
        }
        if (conversion.shouldRemove(((StringIdentifiable) value).asString())) {
            return null;
        }
        if (conversion.externalType != null) {
            return conversion.externalTypeConstants[((Enum<?>) value).ordinal()];
        }
        return value;
    }

    @Override
    public Comparable parsePropertyValue(ElementTag input, Mechanism mechanism) {
        //noinspection SuspiciousMethodCalls
        Conversion<?, ?> conversion = CONVERSIONS.get(internalProperty);
        if (conversion == null) {
            return super.parsePropertyValue(input, mechanism);
        }
        if (conversion.externalType != null) {
            Enum<?> external = input.asEnum(conversion.externalType);
            if (external == null) {
                return null;
            }
            StringIdentifiable internal = conversion.internalTypeConstants[external.ordinal()];
            return internalProperty.getValues().contains(internal) && !conversion.shouldRemove(internal.asString()) ? (Comparable) internal : null;
        }
        if (conversion.shouldRemove(input.asLowerString())) {
            return null;
        }
        return super.parsePropertyValue(input, mechanism);
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
