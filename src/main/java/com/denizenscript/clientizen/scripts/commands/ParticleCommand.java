package com.denizenscript.clientizen.scripts.commands;

import com.denizenscript.clientizen.mixin.ParticleAccessor;
import com.denizenscript.clientizen.mixin.WorldRendererAccessor;
import com.denizenscript.clientizen.objects.EntityTag;
import com.denizenscript.clientizen.objects.ItemTag;
import com.denizenscript.clientizen.objects.LocationTag;
import com.denizenscript.clientizen.objects.MaterialTag;
import com.denizenscript.clientizen.util.Utilities;
import com.denizenscript.denizencore.exceptions.InvalidArgumentsRuntimeException;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ColorTag;
import com.denizenscript.denizencore.objects.core.DurationTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.MapTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.scripts.commands.generator.ArgDefaultNull;
import com.denizenscript.denizencore.scripts.commands.generator.ArgName;
import com.denizenscript.denizencore.scripts.commands.generator.ArgPrefixed;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.denizenscript.denizencore.utilities.debugging.DebugInternals;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.command.argument.ParticleEffectArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.particle.*;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.EntityPositionSource;
import net.minecraft.world.event.PositionSource;

import java.util.function.Predicate;

public class ParticleCommand extends AbstractCommand {

    // <--[command]
    // @Name Particle
    // @Syntax particle [type:<particle>] [at:<location>] (velocity:<velocity>) (color:<color>) (duration:<duration>) (scale_multiplier:<#.#>) (data:<map>/raw_data:<data>)
    // @Required 2
    // @Maximum 7
    // @Short Spawns a particle in the world.
    // @Group world
    //
    // @Description
    // Spawns a particle of the specified type in the world.
    // The type can be any particle type, including ones added by other mods - see <@link url https://minecraft.wiki/w/Particles_(Java_Edition)#Types_of_particles> for all vanilla particle types.
    // The location can be any location to play the particle at.
    // The velocity is a vector location for the particle's movement, which overrides its default movement (if any).
    // The color will override the particle's color or color its texture (depending on the particle), and can be any color.
    // Alpha is supported by some particles, and will change their transparency.
    // The duration is the amount of time the particle will exist for before disappearing.
    // The scale multiplier is a multiplier for the particle's size, which applies on top of any scaling the particle might already have (so particles with varying sizes will still have varying sizes, for example).
    // The data is a map of custom data for the particle (see below).
    // Raw data is an alternative data input that takes in text in the same format as the "/particle" command and parses it into the particle's data. Should generally prefer the "data:" argument.
    //
    // "block", "block_marker", and "falling_dust" take:
    // - <@link ObjectType MaterialTag> "material" key, for the particle's block type.
    // "dust" takes:
    // - <@link ObjectType ColorTag> "color" key, for the dust's color.
    // - <@link ObjectType ElementTag>(Decimal) "scale" key, for the dust's size.
    // "dust_color_transition" takes:
    // - <@link ObjectType ColorTag> "from" key, for the color to transition from.
    // - <@link ObjectType ColorTag> "to" key, for the color to transition to.
    // - <@link ObjectType ElementTag>(Decimal) "scale" key, for the dust's size.
    // "sculk_charge" takes:
    // - <@link ObjectType ElementTag>(Decimal) "roll" key, for the charge's angle in radians (see also <@link tag ElementTag.to_radians>).
    // "item" takes:
    // - <@link ObjectType ItemTag> "item" key, for the item the particle displays.
    // "vibration" takes:
    // - <@link ObjectType DurationTag> "arrival_time" key, for the amount of time it takes the particle to reach its destination.
    // - And a destination, which can be either:
    //   - <@link ObjectType LocationTag> "location" key, for the particle to travel to a specific location.
    //   - <@link ObjectType EntityTag> "entity" key, for the particle to travel to an entity.
    //   - When specifying an entity, optionally include a <@link ObjectType ElementTag>(Decimal) "y_offset" key for the particle to target a position above the entity's location.
    // "shriek" takes:
    // - <@link ObjectType DurationTag> "delay" key, for the amount of time the particle should wait before spawning.
    //
    // @Tags
    // None
    //
    // @Usage
    // Use to spawn a large flame particle above the player.
    // - particle type:flame at:<client.self_entity.location.above[2.5]> scale_multiplier:4
    //
    // @Usage
    // Use to spawn a dust color transition particle that changes color from red to blue.
    // - particle type:dust_color_transition at:<[location]> data:[from=red;to=blue;scale=2]
    //
    // @Usage
    // Use to spawn a block marker particle of a stone block that slowly moves upwards.
    // - particle type:block_marker at:<[location]> data:[material=stone] velocity:0,0.1,0
    //
    // -->

    public ParticleCommand() {
        setName("particle");
        setSyntax("particle [type:<particle>] [at:<location>] (velocity:<velocity>) (color:<color>) (duration:<duration>) (scale_multiplier:<#.#>) (data:<map>/raw_data:<data>)");
        setRequiredArguments(2, 7);
        autoCompile();
    }

    @Override
    public void addCustomTabCompletions(TabCompletionsBuilder tab) {
        tab.addWithPrefix("type:", Utilities.listRegistryKeys(Registries.PARTICLE_TYPE));
    }

    public static void autoExecute(ScriptEntry scriptEntry,
                                   @ArgName("type") @ArgPrefixed String particleName,
                                   @ArgName("at") @ArgPrefixed LocationTag location,
                                   @ArgName("velocity") @ArgPrefixed @ArgDefaultNull LocationTag velocity,
                                   @ArgName("color") @ArgPrefixed @ArgDefaultNull ColorTag color,
                                   @ArgName("duration") @ArgPrefixed @ArgDefaultNull DurationTag duration,
                                   @ArgName("scale_multiplier") @ArgPrefixed @ArgDefaultNull ElementTag scaleMultiplier,
                                   @ArgName("data") @ArgPrefixed @ArgDefaultNull MapTag data,
                                   @ArgName("raw_data") @ArgPrefixed @ArgDefaultNull String rawData) {
        ParticleType<?> type = Registries.PARTICLE_TYPE.get(Identifier.tryParse(particleName));
        if (type == null) {
            Debug.echoError("Invalid particle type specified: " + particleName + '.');
            return;
        }
        ParticleEffect particle;
        if (rawData != null) {
            try {
                NbtCompound particleData = StringNbtReader.readCompound(rawData);
                particle = type.getCodec().codec().parse(Utilities.registryOps(NbtOps.INSTANCE), particleData).getOrThrow(ParticleEffectArgumentType.INVALID_OPTIONS_EXCEPTION::create);
            }
            catch (CommandSyntaxException syntaxException) {
                Debug.echoError("Invalid raw particle data '" + rawData + "' for particle of type '" + particleName + "': " + syntaxException.getMessage());
                return;
            }
        }
        else if (type == ParticleTypes.BLOCK || type == ParticleTypes.BLOCK_MARKER || type == ParticleTypes.FALLING_DUST) {
            MaterialTag material = requireData(data, "material", MaterialTag.class, MaterialTag::isBlock, particleName, scriptEntry);
            particle = new BlockStateParticleEffect((ParticleType<BlockStateParticleEffect>) type, material.state);
        }
        else if (type == ParticleTypes.DUST) {
            ColorTag dustColor = requireData(data, "color", ColorTag.class, particleName, scriptEntry);
            ElementTag dustScale = requireData(data, "scale", ElementTag.class, ElementTag::isFloat, particleName, scriptEntry);
            particle = new DustParticleEffect(dustColor.asRGB(), dustScale.asFloat());
        }
        else if (type == ParticleTypes.DUST_COLOR_TRANSITION) {
            ColorTag fromColor = requireData(data, "from", ColorTag.class, particleName, scriptEntry);
            ColorTag toColor = requireData(data, "to", ColorTag.class, particleName, scriptEntry);
            ElementTag dustScale = requireData(data, "scale", ElementTag.class, ElementTag::isFloat, particleName, scriptEntry);
            particle = new DustColorTransitionParticleEffect(fromColor.asRGB(), toColor.asRGB(), dustScale.asFloat());
        }
        else if (type == ParticleTypes.SCULK_CHARGE) {
            ElementTag roll = requireData(data, "roll", ElementTag.class, ElementTag::isFloat, particleName, scriptEntry);
            particle = new SculkChargeParticleEffect(roll.asFloat());
        }
        else if (type == ParticleTypes.ITEM) {
            ItemTag item = requireData(data, "item", ItemTag.class, particleName, scriptEntry);
            particle = new ItemStackParticleEffect(ParticleTypes.ITEM, item.getStack());
        }
        else if (type == ParticleTypes.VIBRATION) {
            PositionSource destination;
            if (data.containsKey("location")) {
                LocationTag sourceLoc = requireData(data, "location", LocationTag.class, particleName, scriptEntry);
                destination = new BlockPositionSource(sourceLoc.getBlockPos());
            }
            else if (data.containsKey("entity")) {
                EntityTag sourceEntity = requireData(data, "entity", EntityTag.class, particleName, scriptEntry);
                ElementTag yOffset = data.containsKey("y_offset") ? requireData(data, "y_offset", ElementTag.class, ElementTag::isFloat, particleName, scriptEntry) : null;
                destination = new EntityPositionSource(sourceEntity.getEntity(), yOffset != null ? yOffset.asFloat() : 0);
            }
            else {
                throw new InvalidArgumentsRuntimeException("Invalid data '" + data.debuggable() + "<W>' for 'vibration' particle: must have either a block or entity destination.");
            }
            DurationTag travelTime = requireData(data, "arrival_time", DurationTag.class, particleName, scriptEntry);
            particle = new VibrationParticleEffect(destination, travelTime.getTicksAsInt());
        }
        else if (type == ParticleTypes.SHRIEK) {
            DurationTag delay = requireData(data, "delay", DurationTag.class, particleName, scriptEntry);
            particle = new ShriekParticleEffect(delay.getTicksAsInt());
        }
        else {
            particle = (SimpleParticleType) type;
        }
        Particle createdParticle;
        try {
            createdParticle = ((WorldRendererAccessor) MinecraftClient.getInstance().worldRenderer).invokeSpawnParticle(
                    particle, type.shouldAlwaysSpawn(), false, location.getX(), location.getY(), location.getZ(), 0, 0, 0
            );
        }
        catch (Throwable throwable) {
            Debug.echoError("Internal error when spawning particle, see stacktrace below:");
            Debug.echoError(throwable);
            return;
        }
        if (velocity != null) {
            createdParticle.setVelocity(velocity.getX(), velocity.getY(), velocity.getZ());
        }
        if (color != null) {
            createdParticle.setColor(color.red / 255f, color.green / 255f, color.blue / 255f);
            ((ParticleAccessor) createdParticle).invokeSetAlpha(color.alpha / 255f);
        }
        if (duration != null) {
            createdParticle.setMaxAge(duration.getTicksAsInt());
        }
        if (scaleMultiplier != null) {
            if (!scaleMultiplier.isFloat()) {
                throw new InvalidArgumentsRuntimeException("Invalid scale multiplier '" + scaleMultiplier + "' specified: must be a decimal number.");
            }
            createdParticle.scale(scaleMultiplier.asFloat());
        }
    }

    private static <T extends ObjectTag> T requireData(MapTag data, String key, Class<T> objectType, String particleType, ScriptEntry scriptEntry) {
        return requireData(data, key, objectType, object -> true, particleType, scriptEntry);
    }

    private static <T extends ObjectTag> T requireData(MapTag data, String key, Class<T> objectType, Predicate<T> additionalCheck, String particleType, ScriptEntry scriptEntry) {
        if (data == null) {
            throw new InvalidArgumentsRuntimeException("Missing required data input for particle of type '" + particleType + "', see meta docs for more information.");
        }
        ObjectTag rawObject = data.getObject(key);
        if (rawObject == null) {
            throw new InvalidArgumentsRuntimeException("Missing required data key '" + key + "'" + " for particle of type '" + particleType + "'.");
        }
        T convertedObject = rawObject.asType(objectType, scriptEntry.getContext());
        if (convertedObject == null) {
            throw new InvalidArgumentsRuntimeException("Invalid " + DebugInternals.getClassNameOpti(objectType) + " specified under data key '" + key + "': " + rawObject.debuggable() + "<W>.");
        }
        if (!additionalCheck.test(convertedObject)) {
            throw new InvalidArgumentsRuntimeException("Data key '" + key + "' has a valid " + DebugInternals.getClassNameOpti(objectType) + ", but '" + convertedObject.debuggable() + "<W>' isn't valid for the key (see meta docs for more information).");
        }
        return convertedObject;
    }
}
