package dev.smithed.companion.mixin;

import com.mojang.serialization.Lifecycle;
import dev.smithed.companion.utils.RegistryUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.mutable.Mutable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mixin(SimpleRegistry.class)
public abstract class RegistryMixin<T> implements RegistryUtils.RegistryHelper<T>, Mutable<T> {

	@Shadow private boolean frozen;
	@Shadow <T> void assertNotFrozen(RegistryKey<T> key) {}
	@Shadow @Final private Reference2IntMap<T> entryToRawId;
	@Shadow @Final private ObjectList<RegistryEntry.Reference<T>> rawIdToEntry;
	@Shadow @Final private Map<Identifier, RegistryEntry.Reference<T>> idToEntry;
	@Shadow @Final private Map<RegistryKey<T>, RegistryEntry.Reference<T>> keyToEntry;
	@Shadow @Final private Map<T, RegistryEntry.Reference<T>> valueToEntry;
	@Shadow @Final private Map<T, Lifecycle> entryToLifecycle;
	@Shadow @Final public Optional<RegistryEntry.Reference<T>> getEntry(RegistryKey<T> key) {return Optional.ofNullable(this.keyToEntry.get(key));}
	@Shadow private List<RegistryEntry.Reference<T>> cachedEntries;
	@Shadow private int nextId;

	@Shadow protected abstract void assertNotFrozen();

	@Override
	public void smithed$thawRegistry() {
		this.frozen = false;
	}

	private final IntList smithed$availableIds = new IntArrayList();

	@Override
	public void smithed$removeRegistryEntry(RegistryKey<T> registryKey) {
		assertNotFrozen();

		var registryEntry = getEntry(registryKey).orElseThrow();
		int rawId = entryToRawId.getInt(registryEntry.value());

		// Set the old entry to null
		rawIdToEntry.set(rawId, null);
		entryToRawId.removeInt(registryEntry.value());

		// remove the associated registrykeys
		idToEntry.remove(registryKey.getValue());
		keyToEntry.remove(registryKey);

		// remove registry values
		valueToEntry.remove(registryEntry.value());
		entryToLifecycle.remove(registryEntry.value());
		smithed$availableIds.add(rawId);

		cachedEntries = null;
	}

	@Redirect(method = "add", at = @At(value = "FIELD", target = "Lnet/minecraft/registry/SimpleRegistry;nextId:I"))
	private int smithed$nextID(SimpleRegistry<T> instance) {
		return !smithed$availableIds.isEmpty() ? smithed$availableIds.removeInt(0) : nextId;
	}

}