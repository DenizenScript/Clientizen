package com.denizenscript.clientizen.util;

public class Utilities {

    public static float normalizeYaw(float yaw) {
        yaw = yaw % 360;
        if (yaw < 0) {
            yaw += 360.0;
        }
        return yaw;
    }
}
