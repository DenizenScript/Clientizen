package com.denizenscript.clientizen.util;

import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.MapTag;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.mojang.serialization.DynamicOps;
import net.fabricmc.loader.api.metadata.ContactInformation;
import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Utilities {

    @NotNull
    public static String idToString(ResourceLocation identifier) {
        return identifier.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE) ? identifier.getPath() : identifier.toString();
    }

    public static List<String> listRegistryKeys(Registry<?> registry) {
        return registry.keySet().stream().map(Utilities::idToString).toList();
    }

    @Nullable
    public static UUID uuidFromString(String uuid) {
        try {
            return UUID.fromString(uuid);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static void echoErrorByContext(TagContext context, String error) {
        if (context == null || context.showErrors()) {
            Debug.echoError(error);
        }
    }

    public static MapTag contactInfoToMap(ContactInformation contactInformation) {
        MapTag contact = new MapTag();
        for (Map.Entry<String, String> entry : contactInformation.asMap().entrySet()) {
            contact.putObject(entry.getKey(), new ElementTag(entry.getValue(), true));
        }
        return contact;
    }

    public static MapTag personsToMap(Iterable<Person> persons) {
        MapTag personsMap = new MapTag();
        for (Person person : persons) {
            personsMap.putObject(person.getName(), contactInfoToMap(person.getContact()));
        }
        return personsMap;
    }

    public static String camelCaseToSnake(String camelCase) {
        StringBuilder snakeCaseBuilder = new StringBuilder(camelCase.length());
        snakeCaseBuilder.append(Character.toLowerCase(camelCase.charAt(0)));
        for (int i = 1; i < camelCase.length(); i++) {
            char character = camelCase.charAt(i);
            if (Character.isUpperCase(character)) {
                snakeCaseBuilder.append('_');
                snakeCaseBuilder.append(Character.toLowerCase(character));
            }
            else {
                snakeCaseBuilder.append(character);
            }
        }
        return snakeCaseBuilder.toString();
    }

    public static String orderedTextToString(FormattedCharSequence text) {
        StringBuilder converted = new StringBuilder();
        text.accept((index, style, codePoint) -> {
            converted.append(Character.toChars(codePoint));
            return true;
        });
        return converted.toString();
    }

    public static <T> RegistryOps<T> registryOps(DynamicOps<T> delegate) {
        return Minecraft.getInstance().level.registryAccess().createSerializationContext(delegate);
    }

    public static boolean checkLocationWithBoundingBox(Vec3 basePos, Entity entity, double theLeeway) {
        if (basePos.distanceToSqr(entity.position()) >= Mth.square(theLeeway + 16)) {
            return false;
        }
        AABB box = entity.getBoundingBox();
        Vec3 minPos = box.getMinPosition();
        Vec3 maxPos = box.getMaxPosition();
        double x = Math.max(minPos.x(), Math.min(basePos.x(), maxPos.x()));
        double y = Math.max(minPos.y(), Math.min(basePos.y(), maxPos.y()));
        double z = Math.max(minPos.z(), Math.min(basePos.z(), maxPos.z()));
        double xOff = x - basePos.x();
        double yOff = y - basePos.y();
        double zOff = z - basePos.z();
        return xOff * xOff + yOff * yOff + zOff * zOff < theLeeway * theLeeway;
    }
}
