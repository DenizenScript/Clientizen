package com.denizenscript.clientizen.objects;

import com.denizenscript.clientizen.mixin.ClientWorldAccessor;
import com.denizenscript.clientizen.util.Utilities;
import com.denizenscript.denizencore.objects.Fetchable;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.core.MapTag;
import com.denizenscript.denizencore.objects.core.VectorObject;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.ObjectTagProcessor;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Math;

import java.util.List;
import java.util.Objects;

public class LocationTag implements ObjectTag, VectorObject {

    // <--[ObjectType]
    // @name LocationTag
    // @prefix l
    // @base ElementTag
    // @implements VectorObject
    // @ExampleTagBase client.cursor_on
    // @ExampleValues <client.cursor_on>
    // @ExampleForReturns
    // - narrate "The location is %VALUE%!"
    // @format
    // The identity format for locations is <x>,<y>,<z>,<pitch>,<yaw>,<world>
    // Note that you can omit the world value. You can also omit either the pitch and yaw pair or the z value.
    // For example, 'l@1,2.15,3,45,90,space' or 'l@7.5,99,3.2'
    //
    // @description
    // A LocationTag represents a point in the world.
    //
    // The Minecraft client has no concept of worlds other than the one it's currently in, so while including worlds is valid and supported for compatibility with server-side LocationTags,
    // there is no validation that the world actually exists, and worlds are only referenced as simple names.
    //
    // Note that the 'l' prefix is a lowercase 'L', the first letter in 'location'.
    //
    // -->

    double x, y, z;
    final float yaw, pitch;
    final String world;

    public LocationTag(double x, double y, double z, float yaw, float pitch, String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = normalizeYaw(yaw);
        this.pitch = pitch;
        this.world = world;
    }

    public LocationTag(double x, double y, double z, float yaw, float pitch) {
        this(x, y, z, yaw, pitch, null);
    }

    public LocationTag(double x, double y, double z, String world) {
        this(x, y, z, 0, 0, world);
    }

    public LocationTag(double x, double y, double z) {
        this(x, y, z, null);
    }

    public LocationTag(Position position) {
        this(position.x(), position.y(), position.z());
    }

    public LocationTag(Position position, float yaw, float pitch) {
        this(position.x(), position.y(), position.z(), yaw, pitch);
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
            return new LocationTag(x, y, z, yaw, pitch, world);
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

    public static ClientLevel getWorld() {
        return Objects.requireNonNull(Minecraft.getInstance().level, "Missing world! this should never happen, please report to developers.");
    }

    public static void register() {
        VectorObject.register(LocationTag.class, tagProcessor);

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
        // @attribute <LocationTag.raw_yaw>
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
        // Returns the name of the world the location is in, if any.
        // -->
        tagProcessor.registerStaticTag(ElementTag.class, "world", (attribute, object) -> {
            return object.world != null ? new ElementTag(object.world, true) : null;
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
            return new LocationTag(object.getBlockX(), object.getBlockY(), object.getBlockZ(), object.world);
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
            return new LocationTag(Math.floor(object.x), Math.floor(object.y), Math.floor(object.z), Math.floor(object.yaw), Math.floor(object.pitch), object.world);
        });

        // <--[tag]
        // @attribute <LocationTag.above[(<#.#>)]>
        // @returns LocationTag
        // @group math
        // @description
        // Returns the location above this location. Optionally specify a number of blocks to go up.
        // This just moves straight along the Y axis, equivalent to <@link tag VectorObject.add> with input 0,1,0 (or the input value instead of '1').
        // -->
        tagProcessor.registerTag(LocationTag.class, "above", (attribute, object) -> {
            return object.duplicate().add(0, attribute.hasParam() ? attribute.getDoubleParam() : 1, 0);
        });

        // <--[tag]
        // @attribute <LocationTag.below[(<#.#>)]>
        // @returns LocationTag
        // @group math
        // @description
        // Returns the location below this location. Optionally specify a number of blocks to go down.
        // This just moves straight along the Y axis, equivalent to <@link tag VectorObject.sub> with input 0,1,0 (or the input value instead of '1').
        // -->
        tagProcessor.registerTag(LocationTag.class, "below", (attribute, object) -> {
            return object.duplicate().subtract(0, attribute.hasParam() ? attribute.getDoubleParam() : 1, 0);
        });

        // <--[tag]
        // @attribute <LocationTag.forward[(<#.#>)]>
        // @returns LocationTag
        // @group math
        // @description
        // Returns the location in front of this location based on yaw and pitch. Optionally specify a number of blocks to go forward.
        // This is equivalent to <@link tag LocationTag.backward> in the opposite direction.
        // -->
        tagProcessor.registerTag(LocationTag.class, "forward", (attribute, object) -> {
            Vec3 vector = object.getDirection().scale(attribute.hasParam() ? attribute.getDoubleParam() : 1);
            return object.duplicate().add(vector);
        });

        // <--[tag]
        // @attribute <LocationTag.backward[(<#.#>)]>
        // @returns LocationTag
        // @group math
        // @description
        // Returns the location behind this location based on yaw and pitch. Optionally specify a number of blocks to go backward.
        // This is equivalent to <@link tag LocationTag.forward> in the opposite direction.
        // -->
        tagProcessor.registerTag(LocationTag.class, "backward", (attribute, object) -> {
            Vec3 vector = object.getDirection().scale(attribute.hasParam() ? attribute.getDoubleParam() : 1);
            return object.duplicate().subtract(vector);
        });

        // <--[tag]
        // @attribute <LocationTag.left[(<#.#>)]>
        // @returns LocationTag
        // @group math
        // @description
        // Returns the location to the left of this location based on pitch and yaw. Optionally specify a number of blocks to go left.
        // This is equivalent to <@link tag LocationTag.forward> with a +90 degree rotation to the yaw and the pitch set to 0.
        // -->
        tagProcessor.registerTag(LocationTag.class, "left", (attribute, object) -> {
            Vec3 vector = Vec3.directionFromRotation(0, object.yaw).yRot((float) (Math.PI / 2)).scale(attribute.hasParam() ? attribute.getDoubleParam() : 1);
            return object.duplicate().add(vector);
        });

        // <--[tag]
        // @attribute <LocationTag.right[(<#.#>)]>
        // @returns LocationTag
        // @group math
        // @description
        // Returns the location to the right of this location based on pitch and yaw. Optionally specify a number of blocks to go right.
        // This is equivalent to <@link tag LocationTag.forward> with a -90 degree rotation to the yaw and the pitch set to 0.
        // -->
        tagProcessor.registerTag(LocationTag.class, "right", (attribute, object) -> {
            Vec3 vector = Vec3.directionFromRotation(0, object.yaw).yRot((float) (Math.PI / 2)).scale(attribute.hasParam() ? attribute.getDoubleParam() : 1);
            return object.duplicate().subtract(vector);
        });

        // <--[tag]
        // @attribute <LocationTag.up[(<#.#>)]>
        // @returns LocationTag
        // @group math
        // @description
        // Returns the location above this location based on pitch and yaw. Optionally specify a number of blocks to go up.
        // This is equivalent to <@link tag LocationTag.forward> with a +90 degree rotation to the pitch.
        // To just get the location above this location, use <@link tag LocationTag.above> instead.
        // -->
        tagProcessor.registerTag(LocationTag.class, "up", (attribute, object) -> {
            Vec3 vector = Vec3.directionFromRotation(object.pitch - 90, object.yaw).scale(attribute.hasParam() ? attribute.getDoubleParam() : 1);
            return object.duplicate().add(vector);
        });

        // <--[tag]
        // @attribute <LocationTag.down[(<#.#>)]>
        // @returns LocationTag
        // @group math
        // @description
        // Returns the location below this location based on pitch and yaw. Optionally specify a number of blocks to go down.
        // This is equivalent to <@link tag LocationTag.forward> with a -90 degree rotation to the pitch.
        // To just get the location above this location, use <@link tag LocationTag.below> instead.
        // -->
        tagProcessor.registerTag(LocationTag.class, "down", (attribute, object) -> {
            Vec3 vector = Vec3.directionFromRotation(object.pitch - 90, object.yaw).scale(attribute.hasParam() ? attribute.getDoubleParam() : 1);
            return object.duplicate().subtract(vector);
        });


        // <--[tag]
        // @attribute <LocationTag.material>
        // @returns MaterialTag
        // @group world
        // @description
        // Returns the material of the block at the location.
        // -->
        tagProcessor.registerTag(MaterialTag.class, "material", (attribute, object) ->  {
            return object.isChunkLoaded() ? new MaterialTag(getWorld().getBlockState(object.getBlockPos())) : null;
        });

        // <--[tag]
        // @attribute <LocationTag.find_entities[within=<#>;(match=<matcher>)]>
        // @returns ListTag(EntityTag)
        // @description
        // Returns a list of all entities within range, sorted by closeness (first entity is the closest, last entity is the farthest).
        // Optionally specify an EntityTag matcher to filter by.
        // -->
        tagProcessor.registerTag(ListTag.class, MapTag.class, "find_entities", (attribute, object, param) -> {
            ElementTag rangeElement = param.getRequiredObjectAs("within", ElementTag.class, attribute);
            if (rangeElement == null) {
                return null;
            }
            if (!rangeElement.isInt()) {
                attribute.echoError("Invalid 'within' value '" + rangeElement + "' specified: must be a number.");
                return null;
            }
            int range = rangeElement.asInt();
            if (range < 0) {
                attribute.echoError("Invalid 'within' value '" + rangeElement + "': cannot be lower than 0.");
                return null;
            }
            ElementTag matcherElement = param.getElement("match");
            String matcher = matcherElement != null ? matcherElement.asString() : null;
            Vec3 originPos = object.getPosVector();
            ListTag entities = new ListTag();
            int doubleRange = range * 2;
            ((ClientWorldAccessor) getWorld()).invokeGetEntities().get(AABB.ofSize(originPos, doubleRange, doubleRange, doubleRange), entity -> {
                if (!Utilities.checkLocationWithBoundingBox(originPos, entity, range)) {
                    return;
                }
                EntityTag entityTag = new EntityTag(entity);
                if (matcher == null || entityTag.advancedMatches(matcher, attribute.context)) {
                    entities.addObject(entityTag);
                }
            });
            entities.objectForms.sort((firstEnt, secondEnt) -> object.compare(((EntityTag) firstEnt).getEntity().position(), ((EntityTag) secondEnt).getEntity().position()));
            return entities;
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
        yaw %= 360;
        if (yaw < 0) {
            yaw += 360;
        }
        return yaw;
    }

    public boolean isChunkLoaded() {
        Level world = Minecraft.getInstance().level;
        return world != null && world.hasChunk(SectionPos.posToSectionCoord(getX()), SectionPos.posToSectionCoord(getZ()));
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getZ() {
        return z;
    }

    @Override
    public void setX(double x) {
        this.x = x;
    }

    @Override
    public void setY(double y) {
        this.y = y;
    }

    @Override
    public void setZ(double z) {
        this.z = z;
    }

    public LocationTag add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public LocationTag add(Vec3 toAdd) {
        return add(toAdd.x(), toAdd.y(), toAdd.z());
    }

    public LocationTag subtract(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public LocationTag subtract(Vec3 toSub) {
        return subtract(toSub.x(), toSub.y(), toSub.z());
    }

    public int getBlockX() {
        return Mth.floor(getX());
    }
    public int getBlockY() {
        return Mth.floor(getY());
    }

    public int getBlockZ() {
        return Mth.floor(getZ());
    }

    public BlockPos getBlockPos() {
        return BlockPos.containing(getX(), getY(), getZ());
    }

    public Vec3 getDirection() {
        return Vec3.directionFromRotation(pitch, yaw);
    }

    public Vec3 getPosVector() {
        return new Vec3(getX(), getY(), getZ());
    }

    public int compare(Vec3 pos1, Vec3 pos2) {
        if (pos1 == pos2) {
            return 0;
        }
        if (pos1 == null) {
            return 1;
        }
        if (pos2 == null) {
            return -1;
        }
        if (pos1.equals(pos2)) {
            return 0;
        }
        return Double.compare(pos1.distanceToSqr(getX(), getY(), getZ()), pos2.distanceToSqr(getX(), getY(), getZ()));
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
