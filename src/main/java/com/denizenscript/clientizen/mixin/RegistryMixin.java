package com.denizenscript.clientizen.mixin;

import com.denizenscript.clientizen.access.RegistryMixinAccess;
import com.denizenscript.denizencore.exceptions.InvalidArgumentsRuntimeException;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

@Mixin(SimpleRegistry.class)
public abstract class RegistryMixin<T> implements RegistryMixinAccess {

    @Unique
    boolean clientizen$isIntrusive;

    @Shadow
    private boolean frozen;
    @Shadow
    public abstract RegistryKey<? extends Registry<T>> getKey();
    @Shadow
    private @Nullable Map<T, RegistryEntry.Reference<T>> intrusiveValueToEntry;
    @Shadow
    @Final
    private ObjectList<RegistryEntry.Reference<T>> rawIdToEntry;
    @Shadow
    @Final
    private Map<Identifier, RegistryEntry.Reference<T>> idToEntry;
    @Shadow
    @Final
    private Reference2IntMap<T> entryToRawId;
    @Shadow
    @Final
    private Map<RegistryKey<T>, RegistryEntry.Reference<T>> keyToEntry;
    @Shadow
    @Final
    private Map<T, RegistryEntry.Reference<T>> valueToEntry;
    @Shadow
    @Final
    private Map<T, Lifecycle> entryToLifecycle;
    @Shadow
    private @Nullable List<RegistryEntry.Reference<T>> cachedEntries;

    @Override
    public void clientizen$unfreeze() {
        if (!frozen) {
            return;
        }
        frozen = false;
        if (clientizen$isIntrusive) {
            intrusiveValueToEntry = new IdentityHashMap<>();
        }
    }

    @Override
    public void clientizen$remove(Identifier toRemove) {
        RegistryEntry.Reference<T> value = idToEntry.get(toRemove);
        if (value == null) {
            throw new InvalidArgumentsRuntimeException("Unable to remove '" + toRemove + "' from registry '" + getKey() + "': registry has no value by that key.");
        }
        RegistryKey<T> key = RegistryKey.of(getKey(), toRemove);
        keyToEntry.remove(key);
        idToEntry.remove(toRemove);
        valueToEntry.remove(value.value());
        rawIdToEntry.remove(value);
        entryToRawId.removeInt(value.value());
        entryToLifecycle.remove(value.value());
        cachedEntries = null;
    }

    @Inject(method = "<init>(Lnet/minecraft/registry/RegistryKey;Lcom/mojang/serialization/Lifecycle;Z)V", at = @At("TAIL"))
    private void clientizen$saveIsIntrusive(RegistryKey<?> key, Lifecycle lifecycle, boolean intrusive, CallbackInfo ci) {
        clientizen$isIntrusive = intrusive;
    }
}
