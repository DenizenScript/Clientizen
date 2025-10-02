package com.denizenscript.clientizen.util;

import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.MapTag;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.mojang.serialization.DynamicOps;
import net.fabricmc.loader.api.metadata.ContactInformation;
import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryOps;
import net.minecraft.text.OrderedText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Utilities {

    @NotNull
    public static String idToString(Identifier identifier) {
        return identifier.getNamespace().equals(Identifier.DEFAULT_NAMESPACE) ? identifier.getPath() : identifier.toString();
    }

    public static List<String> listRegistryKeys(Registry<?> registry) {
        return registry.getIds().stream().map(Utilities::idToString).toList();
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

    public static String orderedTextToString(OrderedText text) {
        StringBuilder converted = new StringBuilder();
        text.accept((index, style, codePoint) -> {
            converted.append(Character.toChars(codePoint));
            return true;
        });
        return converted.toString();
    }

    public static <T> RegistryOps<T> registryOps(DynamicOps<T> delegate) {
        return MinecraftClient.getInstance().world.getRegistryManager().getOps(delegate);
    }

    public static boolean checkLocationWithBoundingBox(Vec3d basePos, Entity entity, double theLeeway) {
        if (basePos.squaredDistanceTo(entity.getEntityPos()) >= MathHelper.square(theLeeway + 16)) {
            return false;
        }
        Box box = entity.getBoundingBox();
        Vec3d minPos = box.getMinPos();
        Vec3d maxPos = box.getMaxPos();
        double x = Math.max(minPos.getX(), Math.min(basePos.getX(), maxPos.getX()));
        double y = Math.max(minPos.getY(), Math.min(basePos.getY(), maxPos.getY()));
        double z = Math.max(minPos.getZ(), Math.min(basePos.getZ(), maxPos.getZ()));
        double xOff = x - basePos.getX();
        double yOff = y - basePos.getY();
        double zOff = z - basePos.getZ();
        return xOff * xOff + yOff * yOff + zOff * zOff < theLeeway * theLeeway;
    }
}
