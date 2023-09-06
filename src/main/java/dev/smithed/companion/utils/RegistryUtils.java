package dev.smithed.companion.utils;

import com.mojang.serialization.Codec;
import dev.smithed.companion.registry.ComRecipe;
import dev.smithed.companion.registry.DatapackItem;
import dev.smithed.companion.registry.ItemGroupData;
import dev.smithed.companion.registry.RecipeCategory;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

import java.util.HashSet;
import java.util.Set;

import static dev.smithed.companion.SmithedMain.modID;

public class RegistryUtils {

    /**
     * Set containing smithed specific registry keys.
     */
    public static final Set<RegistryKey<?>> SMITHED_REGISTRY_KEYS = new HashSet<>();

    /**
     * Smithed companion registry keys
     */
    public static final RegistryKey<Registry<ShortcutUtils.ShortcutData>> SHORTCUT_REGISTRY = RegistryKey.ofRegistry(modID("shortcuts"));
    public static final RegistryKey<Registry<ItemGroupData>> ITEMGROUP_REGISTRY = RegistryKey.ofRegistry(modID("item_groups"));
    public static final RegistryKey<Registry<DatapackItem>> DATAPACK_ITEM_REGISTRY = RegistryKey.ofRegistry(modID("datapack_items"));
    public static final RegistryKey<Registry<RecipeCategory>> RECIPE_CATEGORY = RegistryKey.ofRegistry(modID("recipe_categories"));
    public static final RegistryKey<Registry<ComRecipe>> RECIPES = RegistryKey.ofRegistry(modID("recipes"));

    /**
     * Central method for organized registration
     */
    public static void registerAll() {
        registerDynamicRegistries();
    }

    /**
     * Registers dynamic registries like shortcuts, itemgroups etc...
     */
    private static void registerDynamicRegistries() {
        registerSynced(SHORTCUT_REGISTRY, ShortcutUtils.ShortcutData.CODEC, DynamicRegistries.SyncOption.SKIP_WHEN_EMPTY);
        registerSynced(ITEMGROUP_REGISTRY, ItemGroupData.CODEC, DynamicRegistries.SyncOption.SKIP_WHEN_EMPTY);
        registerSynced(DATAPACK_ITEM_REGISTRY, DatapackItem.CODEC, DynamicRegistries.SyncOption.SKIP_WHEN_EMPTY);
        registerSynced(RECIPE_CATEGORY, RecipeCategory.CODEC);
        registerSynced(RECIPES, ComRecipe.CODEC);
    }

    /**
     * Central method to thaw out registries so that they can be added to post runtime.
     * @param registry the registry to thaw
     */
    public static void thawRegistry(Registry<?> registry) {
        if(registry instanceof RegistryHelper<?> mixin)
            mixin.smithed$thawRegistry();
    }

    /**
     * Central method to remove registry entries so that they can be added to post runtime.
     * @param registry the registry to remove a value from
     * @param registryKey the key to remove
     * @param <T> the registry type
     */
    public static <T> void removeRegistryEntry(Registry<T> registry, RegistryKey<T> registryKey) {
        if(registry instanceof RegistryHelper mixin)
            mixin.smithed$removeRegistryEntry(registryKey);
    }

    /**
     * Smithed standin for <code>DynamicRegistries.registerSynced();</code>
     * @param key key to bind registry to
     * @param codec codec for network communication
     * @param options options for network syncing
     * @param <T> typeof registry
     */
    private static <T> void registerSynced(RegistryKey<? extends Registry<T>> key, Codec<T> codec, DynamicRegistries.SyncOption... options) {
        SMITHED_REGISTRY_KEYS.add(key);
        DynamicRegistries.registerSynced(key, codec, codec, options);
    }

    /**
     * Simple helper interface for registry functions
     * @param <T> typeof registry, goes unused typically
     */
    public interface RegistryHelper<T> {
        void smithed$thawRegistry();

        void smithed$removeRegistryEntry(RegistryKey<T> registryKey);
    }

    /**
     * Event for finalization of registries, called when all registries have been loaded
     */
    //TODO implement this event completely
    @FunctionalInterface
    public interface RegistryFinalizedCallback {
        /**
         *
         */
        void invoke();

        Event<RegistryFinalizedCallback> EVENT = EventFactory.createArrayBacked(RegistryFinalizedCallback.class, listeners -> () -> {
            for(RegistryFinalizedCallback listener : listeners) {
                listener.invoke();
            }
        });
    }

}
