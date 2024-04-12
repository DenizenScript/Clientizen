package com.denizenscript.clientizen.scripts.containers;

import com.denizenscript.clientizen.Clientizen;
import com.denizenscript.clientizen.access.RegistryMixinAccess;
import com.denizenscript.clientizen.objects.ParticleTag;
import com.denizenscript.clientizen.scripts.containers.gui.GuiScriptContainer;
import com.denizenscript.denizencore.DenizenCore;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.DurationTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.containers.ScriptContainer;
import com.denizenscript.denizencore.scripts.queues.ContextSource;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import com.denizenscript.denizencore.utilities.ScriptUtilities;
import com.denizenscript.denizencore.utilities.YamlConfiguration;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.denizenscript.denizencore.utilities.text.StringHolder;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParticleScriptContainer extends ScriptContainer {

    public static final List<ParticleScriptContainer> customParticles = new ArrayList<>();

    public static void clearCustomParticles() {
        RegistryMixinAccess particleRegistry = (RegistryMixinAccess) Registries.PARTICLE_TYPE;
        particleRegistry.clientizen$unfreeze();
        for (ParticleScriptContainer particleScript : customParticles) {
            particleRegistry.clientizen$remove(particleScript.getId());
        }
    }

    public static void registerCustomParticles() {
        if (MinecraftClient.getInstance().particleManager == null) {
            ClientLifecycleEvents.CLIENT_STARTED.register(client -> ParticleScriptContainer.registerCustomParticles());
            return;
        }
        Map<Identifier, ParticleManager.SimpleSpriteProvider> spritesMap = ParticleTag.getSpriteProviders();
        for (ParticleScriptContainer particleScript : customParticles) {
            DefaultParticleType type = FabricParticleTypes.simple();
            Identifier particleId = particleScript.getId();
            Registry.register(Registries.PARTICLE_TYPE, particleId, type);
            ParticleFactoryRegistry.getInstance().register(type, spriteProvider -> new ClientizenParticle.Factory(spriteProvider, particleScript));
            spritesMap.get(particleId).setSprites(particleScript.textures);
        }
        Registries.PARTICLE_TYPE.freeze();
    }

    public List<Sprite> textures;
    public final List<ScriptEntry> updateScript;
    public List<Mechanism> mechanisms;
    public long updateRate;

    public ParticleScriptContainer(YamlConfiguration configurationSection, String scriptContainerName) {
        super(configurationSection, scriptContainerName);
        Debug.pushErrorContext(this);
        SpriteAtlasTexture particlesAtlas = ParticleTag.getParticleAtlas();
        List<String> textureInput = getStringList("textures", true);
        if (textureInput == null) {
            Debug.echoError("Missing required 'textures' key.");
            Debug.popErrorContext();
            updateScript = null;
            return;
        }
        textures = new ArrayList<>(textureInput.size());
        for (String texture : textureInput) {
            Identifier textureId = Identifier.tryParse(texture);
            if (textureId == null) {
                Debug.echoError("Invalid texture id specified: " + texture + '.');
                continue;
            }
            Sprite sprite = particlesAtlas.getSprite(textureId);
            if (sprite == null) {
                Debug.echoError("Texture id '" + texture + "' is valid, but a texture with that id cannot be found.");
                continue;
            }
            textures.add(sprite);
        }
        updateScript = getEntries(DenizenCore.implementation.getEmptyScriptEntryData(), "update");
        TagContext scriptContext = DenizenCore.implementation.getTagContext(this);
        DurationTag rateDuration = GuiScriptContainer.getTaggedObject(DurationTag.class, configurationSection, "update_rate", scriptContext);
        if (rateDuration != null) {
            updateRate = rateDuration.getMillis();
        }
        YamlConfiguration mechanismsSection = getConfigurationSection("mechanisms");
        if (mechanismsSection != null) {
            mechanisms = new ArrayList<>(mechanismsSection.contents.size());
            for (Map.Entry<StringHolder, Object> entry : mechanismsSection.contents.entrySet()) {
                ObjectTag value = CoreUtilities.objectToTagForm(entry.getValue(), scriptContext, true, true, true);
                mechanisms.add(new Mechanism(entry.getKey().low, value, scriptContext));
            }
        }
        customParticles.add(this);
        Debug.popErrorContext();
    }

    public Identifier getId() {
        return Clientizen.id(CoreUtilities.toLowerCase(getName()));
    }

    public static class ClientizenParticle extends SpriteBillboardParticle {

        SpriteProvider spriteProvider;
        ParticleScriptContainer particleScript;
        ContextSource.SimpleMap scriptContext;
        long lastUpdateTime;

        protected ClientizenParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ,
                                     SpriteProvider spriteProvider, ParticleScriptContainer particleScript, ParticleType<?> particleType) {
            super(world, x, y, z, velocityX, velocityY, velocityZ);
            this.spriteProvider = spriteProvider;
            this.particleScript = particleScript;
            this.scriptContext = new ContextSource.SimpleMap();
            setSprite(spriteProvider);
            ParticleTag particleTag = new ParticleTag(this);
            // Specifically set the type early, as we apply mechanisms
            particleTag.getMixinAccess().clientizen$setType(particleType);
            scriptContext.contexts = Map.of("particle", particleTag);
            if (particleScript.mechanisms != null) {
                particleScript.mechanisms.forEach(particleTag::safeAdjust);
            }
        }

        @Override
        public void tick() {
            if (age++ >= maxAge) {
                markDead();
                return;
            }
            prevPosX = x;
            prevPosY = y;
            prevPosZ = z;
            prevAngle = angle;
            move(this.velocityX, this.velocityY, this.velocityZ);
            if (particleScript.updateScript == null) {
                return;
            }
            long currentTime = DenizenCore.currentTimeMillis;
            if (currentTime - lastUpdateTime < particleScript.updateRate) {
                return;
            }
            lastUpdateTime = currentTime;
            ScriptUtilities.createAndStartQueueArbitrary(particleScript.getName() + "_UPDATE", particleScript.updateScript, null, scriptContext, null);
        }

        @Override
        public ParticleTextureSheet getType() {
            return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
        }

        public record Factory(SpriteProvider spriteProvider, ParticleScriptContainer particleScript) implements ParticleFactory<DefaultParticleType> {

            @Override
            public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
                return new ClientizenParticle(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider, particleScript, parameters);
            }
        }
    }
}
