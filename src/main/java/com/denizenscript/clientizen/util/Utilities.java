package com.denizenscript.clientizen.util;

import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
}
