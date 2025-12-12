package com.denizenscript.clientizen.objects.properties.material.internal;

import com.denizenscript.clientizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class MaterialEnumProperty extends MaterialMinecraftProperty {

    public static final Map<EnumProperty<?>, Conversion<?, ?>> CONVERSIONS = new HashMap<>();

    public static class Conversion<IT extends Enum<IT> & StringRepresentable, ET extends Enum<ET> & StringRepresentable> {
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

    public static <IT extends Enum<IT> & StringRepresentable, ET extends Enum<ET> & StringRepresentable> void convertEnum(EnumProperty<IT> internalProperty, Class<ET> externalType) {
        Conversion conversion = CONVERSIONS.computeIfAbsent(internalProperty, k -> new Conversion<IT, ET>());
        conversion.externalType = externalType;
        conversion.externalTypeConstants = externalType.getEnumConstants();
        conversion.internalTypeConstants = internalProperty.getValueClass().getEnumConstants();
    }

    public static <IT extends Enum<IT> & StringRepresentable> void removeValues(EnumProperty<IT> internalProperty, IT... toRemove) {
        CONVERSIONS.computeIfAbsent(internalProperty, k -> new Conversion()).toRemove = Arrays.stream(toRemove).map(StringRepresentable::getSerializedName).collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public Comparable processPropertyValue(Comparable value) {
        //noinspection SuspiciousMethodCalls
        Conversion<?, ?> conversion = CONVERSIONS.get(internalProperty);
        if (conversion == null) {
            return value;
        }
        if (conversion.shouldRemove(((StringRepresentable) value).getSerializedName())) {
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
            StringRepresentable internal = conversion.internalTypeConstants[external.ordinal()];
            return internalProperty.getPossibleValues().contains(internal) && !conversion.shouldRemove(internal.getSerializedName()) ? (Comparable) internal : null;
        }
        if (conversion.shouldRemove(input.asLowerString())) {
            return null;
        }
        return super.parsePropertyValue(input, mechanism);
    }


    public interface EnumStringIdentifiable extends StringRepresentable {

        @Override
        default String getSerializedName() {
            return name();
        }

        String name();
    }

    public static void registerProperty(Class<? extends MaterialEnumProperty> propertyClass, EnumProperty<?>... properties) {
        PropertyParser.registerPropertyGetter(new MaterialMinecraftPropertyGetter(propertyClass, properties), MaterialTag.class, null, null, propertyClass);
    }
}
