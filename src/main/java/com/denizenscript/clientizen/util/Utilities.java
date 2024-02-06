package com.denizenscript.clientizen.util;

import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.MapTag;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import net.fabricmc.loader.api.metadata.ContactInformation;
import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.text.OrderedText;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public class Utilities {

    @NotNull
    public static String idToString(Identifier identifier) {
        return identifier.getNamespace().equals(Identifier.DEFAULT_NAMESPACE) ? identifier.getPath() : identifier.toString();
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
}
