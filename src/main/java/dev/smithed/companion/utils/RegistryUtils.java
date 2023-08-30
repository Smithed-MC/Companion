package dev.smithed.companion.utils;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;

import static dev.smithed.companion.SmithedMain.modID;

public class RegistryUtils {

    /*
    Smithed companion registry keys
     */
    public static final RegistryKey<Registry<ShortcutUtils.ShortcutData>> SHORTCUT_REGISTRY = RegistryKey.ofRegistry(modID("shortcuts"));
    public static final RegistryKey<Registry<ItemGroupUtils.ItemGroupData>> ITEMGROUP_REGISTRY = RegistryKey.ofRegistry(modID("item_groups"));
    public static final RegistryKey<Registry<ItemGroupUtils.Entry.EntryType>> GROUP_ENTRY_TYPE_REGISTRY = RegistryKey.ofRegistry(modID("group_entries"));

    /*
    Simple registries
     */
    public static final Registry<ItemGroupUtils.Entry.EntryType> ENTRY_TYPE_REGISTRY = new SimpleRegistry<>(GROUP_ENTRY_TYPE_REGISTRY, Lifecycle.stable());

    /*
    Central method for organized registration
     */
    public static void registerAll() {
        registerDynamicRegistries();
        registerItemgroupRegistries();
    }

    /*
    Registers dynamic registries like shortcuts, itemgroups etc...
     */
    private static void registerDynamicRegistries() {
        DynamicRegistries.registerSynced(SHORTCUT_REGISTRY, ShortcutUtils.ShortcutData.CODEC);
        DynamicRegistries.registerSynced(ITEMGROUP_REGISTRY, ItemGroupUtils.ItemGroupData.CODEC);
    }

    /*
    Registers handlers for different itemgroup functionalities
     */
    private static void registerItemgroupRegistries() {
        Registry.register(ENTRY_TYPE_REGISTRY, modID("loot_entry"), new ItemGroupUtils.Entry.EntryType(ItemGroupUtils.Entry.LootTableEntry.CODEC));
        Registry.register(ENTRY_TYPE_REGISTRY, modID("item_entry"), new ItemGroupUtils.Entry.EntryType(ItemGroupUtils.Entry.ItemEntry.CODEC));
    }

    /*
    Central method to thaw out registries so that they can be added to post runtime.
    This method has one parameter
    - Registry registry: the registry to thaw
     */
    public static void thawRegistry(Registry registry) {
        ((RegistryHelper)registry).smithed$thawRegistry();
    }

    /*
    Central method to remove registry entries so that they can be added to post runtime.
    This method has one parameter
    - RegistryKey registryKey: the registryKey to remove
     */
    public static <T> void removeRegistryEntry(Registry registry, RegistryKey<T> registryKey) {
        ((RegistryHelper)registry).smithed$removeRegistryEntry(registryKey);
    }

    public static interface RegistryHelper<T> {
        void smithed$thawRegistry();

        void smithed$removeRegistryEntry(RegistryKey<T> registryKey);
    }

}