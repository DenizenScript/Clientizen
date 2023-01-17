package com.denizenscript.clientizen.objects;

import com.denizenscript.denizencore.objects.Fetchable;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3i;

import java.util.List;

public class LocationTag implements ObjectTag {

    double x, y, z;
    float yaw, pitch;
    String world;

    public LocationTag(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = normalizeYaw(yaw);
        this.pitch = pitch;
    }

    public LocationTag(double x, double y, double z) {
        this(x, y, z, 0, 0);
    }

    public LocationTag(Position position) {
        this(position.getX(), position.getY(), position.getZ());
    }

    public LocationTag(Position position, float yaw, float pitch) {
        this(position.getX(), position.getY(), position.getZ(), yaw, pitch);
    }

    public LocationTag(Vec3i intVector) {
        this(intVector.getX(), intVector.getY(), intVector.getZ());
    }

    public LocationTag(LocationTag toCopy) {
        x = toCopy.x;
        y = toCopy.y;
        z = toCopy.z;
        yaw = toCopy.yaw;
        pitch = toCopy.pitch;
        world = toCopy.world;
    }

    public LocationTag setWorld(String world) {
        this.world = world;
        return this;
    }

    @Fetchable("l")
    public static LocationTag valueOf(String string, TagContext context) {
        if (string == null) {
            return null;
        }
        if (string.startsWith("l@")) {
            string = string.substring("l@".length());
        }
        List<String> split = CoreUtilities.split(string, ',');
        int size = split.size();
        if (size < 2 || size > 6) {
            if (context == null || context.showErrors()) {
                Debug.echoError("valueOf LocationTag returning null, not formatted as a LocationTag: " + string);
            }
            return null;
        }
        try {
            // If 2 values, worldless 2D location format: x,y
            // If 3 values, worldless location format: x,y,z
            // If 4 values, standard dScript location format: x,y,z,world
            // If 5 values, worldless location with pitch/yaw: x,y,z,pitch,yaw
            // If 6 values, location with pitch/yaw: x,y,z,pitch,yaw,world
            double x = Double.parseDouble(split.get(0));
            double y = Double.parseDouble(split.get(1));
            double z = 0;
            float yaw = 0, pitch = 0;
            String world = null;
            if (size > 2) {
                z = Double.parseDouble(split.get(2));
            }
            if (size == 5 || size == 6) {
                pitch = Float.parseFloat(split.get(3));
                yaw = Float.parseFloat(split.get(4));
            }
            if (size == 4 || size == 6) {
                world = split.get(size - 1);
                if (world.startsWith("w@")) {
                    world = world.substring("w@".length());
                }
            }
            return new LocationTag(x, y, z, yaw, pitch).setWorld(world);
        }
        catch (Exception e) {
            if (context == null || context.showErrors()) {
                Debug.echoError("valueOf LocationTag returning null: " + string + " (internal exception:" + e.getMessage() + ")");
            }
            return null;
        }
    }

    public static boolean matches(String string) {
        if (string.startsWith("l@")) {
            return true;
        }
        return valueOf(string, CoreUtilities.noDebugContext) != null;
    }

    public int getBlockX() {
        return MathHelper.floor(x);
    }
    public int getBlockY() {
        return MathHelper.floor(y);
    }

    public int getBlockZ() {
        return MathHelper.floor(z);
    }

    public static void register() {

        // <--[tag]
        // @attribute <LocationTag.x>
        // @returns ElementTag(Decimal)
        // @group identity
        // @description
        // Returns the X coordinate of the location.
        // -->
        tagProcessor.registerStaticTag(ElementTag.class, "x", (attribute, object) -> {
            return new ElementTag(object.x);
        });

        // <--[tag]
        // @attribute <LocationTag.y>
        // @returns ElementTag(Decimal)
        // @group identity
        // @description
        // Returns the Y coordinate of the location.
        // -->
        tagProcessor.registerStaticTag(ElementTag.class, "y", (attribute, object) -> {
            return new ElementTag(object.y);
        });

        // <--[tag]
        // @attribute <LocationTag.z>
        // @returns ElementTag(Decimal)
        // @group identity
        // @description
        // Returns the Z coordinate of the location.
        // -->
        tagProcessor.registerStaticTag(ElementTag.class, "z", (attribute, object) -> {
            return new ElementTag(object.z);
        });

        // <--[tag]
        // @attribute <LocationTag.yaw>
        // @returns ElementTag(Decimal)
        // @group identity
        // @description
        // Returns the location's normalized yaw.
        // -->
        tagProcessor.registerStaticTag(ElementTag.class, "yaw", (attribute, object) -> {
            return new ElementTag(normalizeYaw(object.yaw));
        });

        // <--[tag]
        // @attribute <LocationTag.yaw.raw>
        // @returns ElementTag(Decimal)
        // @group identity
        // @description
        // Returns the location's raw (un-normalized) yaw.
        // -->
        tagProcessor.registerStaticTag(ElementTag.class, "raw_yaw", (attribute, object) -> {
            return new ElementTag(object.yaw);
        });

        // <--[tag]
        // @attribute <LocationTag.pitch>
        // @returns ElementTag(Decimal)
        // @group identity
        // @description
        // Returns the location's pitch.
        // -->
        tagProcessor.registerStaticTag(ElementTag.class, "pitch", (attribute, object) -> {
            return new ElementTag(object.pitch);
        });

        // <--[tag]
        // @attribute <LocationTag.world>
        // @returns ElementTag
        // @group identity
        // @description
        // Returns the name of the world that the location is in.
        // -->
        tagProcessor.registerStaticTag(ElementTag.class, "world", (attribute, object) -> {
            return new ElementTag(object.world);
        });

        // <--[tag]
        // @attribute <LocationTag.block>
        // @returns LocationTag
        // @group math
        // @description
        // Returns the location of the block this location is on,
        // i.e. returns a location without decimals or direction.
        // Note that you almost never actually need this tag. This does not "get the block", this just rounds coordinates down.
        // If you have this in a script, it is more likely to be a mistake than actually needed.
        // Consider using <@link tag LocationTag.round_down> instead.
        // -->
        tagProcessor.registerStaticTag(LocationTag.class, "block", (attribute, object) -> {
            return new LocationTag(object.getBlockX(), object.getBlockY(), object.getBlockZ()).setWorld(object.world);
        });

        // <--[tag]
        // @attribute <LocationTag.round_down>
        // @returns LocationTag
        // @group math
        // @description
        // Returns a rounded-downward version of the LocationTag's coordinates.
        // That is, each component (X, Y, Z, Yaw, Pitch) is rounded downward
        // (eg, 0.1 becomes 0.0, 0.5 becomes 0.0, 0.9 becomes 0.0).
        // This is equivalent to the block coordinates of the location.
        // -->
        tagProcessor.registerTag(LocationTag.class, "round_down", (attribute, object) -> {
            LocationTag result = object.duplicate();
            result.x = Math.floor(result.x);
            result.y = Math.floor(result.y);
            result.z = Math.floor(result.z);
            result.yaw = (float) Math.floor((result.yaw));
            result.pitch = (float) Math.floor(result.pitch);
            return result;
        });
    }

    public static final ObjectTagProcessor<LocationTag> tagProcessor = new ObjectTagProcessor<>();

    @Override
    public ObjectTag getObjectAttribute(Attribute attribute) {
        return tagProcessor.getObjectAttribute(this, attribute);
    }

    @Override
    public LocationTag duplicate() {
        return new LocationTag(this);
    }

    public String identify(String separator) {
        String output = CoreUtilities.doubleToString(x) + separator
                + CoreUtilities.doubleToString(y) + separator
                + CoreUtilities.doubleToString(z);
        if (yaw != 0 || pitch != 0) {
            output += separator + CoreUtilities.floatToCleanString(pitch)
                    + separator + CoreUtilities.floatToCleanString(yaw);
        }
        if (world != null) {
            output += separator + world;
        }
        return output;
    }

    @Override
    public String identify() {
        return "l@" + identify(",");
    }

    @Override
    public String debuggable() {
        return "<LG>l@<Y>" + identify("<LG>,<Y>");
    }

    @Override
    public String identifySimple() {
        return "l@" + getBlockX() + "," + getBlockY() + "," + getBlockZ() + (world != null ? "," + world : "");
    }

    @Override
    public String toString() {
        return identify();
    }

    @Override
    public boolean isUnique() {
        return false;
    }

    public static float normalizeYaw(float yaw) {
        yaw = yaw % 360;
        if (yaw < 0) {
            yaw += 360.0;
        }
        return yaw;
    }

    private String prefix = "Location";

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public ObjectTag setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }
}
