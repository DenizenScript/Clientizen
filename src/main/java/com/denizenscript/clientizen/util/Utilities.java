package com.denizenscript.clientizen.util;

import net.minecraft.util.Identifier;

public class Utilities {

    public static String idToString(Identifier identifier) {
        return identifier.getNamespace().equals(Identifier.DEFAULT_NAMESPACE) ? identifier.getPath() : identifier.toString();
    }
}
