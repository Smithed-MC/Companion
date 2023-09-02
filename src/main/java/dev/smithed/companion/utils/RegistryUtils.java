package dev.smithed.companion.utils;

import com.mojang.serialization.Lifecycle;
import dev.smithed.companion.registry.ComRecipe;
import dev.smithed.companion.registry.RecipeCategory;
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
    public static final RegistryKey<Registry<DatapackItemUtils.DatapackItem>> DATAPACK_ITEM_REGISTRY = RegistryKey.ofRegistry(modID("datapack_items"));
    public static final RegistryKey<Registry<ItemGroupUtils.Entry.EntryType>> GROUP_ENTRY_TYPE_REGISTRY = RegistryKey.ofRegistry(modID("group_entries"));
    public static final RegistryKey<Registry<RecipeCategory>> RECIPE_CATEGORY = RegistryKey.ofRegistry(modID("recipe_categories"));
    public static final RegistryKey<Registry<ComRecipe>> RECIPES = RegistryKey.ofRegistry(modID("recipes"));

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
        DynamicRegistries.registerSynced(DATAPACK_ITEM_REGISTRY, DatapackItemUtils.DatapackItem.CODEC);
        DynamicRegistries.registerSynced(RECIPE_CATEGORY, RecipeCategory.CODEC);
        DynamicRegistries.registerSynced(RECIPES, ComRecipe.CODEC);
    }

    /*
    Registers handlers for different itemgroup functionalities
     */
    private static void registerItemgroupRegistries() {
        Registry.register(ENTRY_TYPE_REGISTRY, modID("loot_entry"), new ItemGroupUtils.Entry.EntryType(ItemGroupUtils.Entry.LootTableEntry.CODEC));
        Registry.register(ENTRY_TYPE_REGISTRY, modID("item_entry"), new ItemGroupUtils.Entry.EntryType(ItemGroupUtils.Entry.ItemEntry.CODEC));
        Registry.register(ENTRY_TYPE_REGISTRY, modID("datapack_item_entry"), new ItemGroupUtils.Entry.EntryType(ItemGroupUtils.Entry.DatapackItemEntry.CODEC));
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
    public static <T> void removeRegistryEntry(Registry<T> registry, RegistryKey<T> registryKey) {
        ((RegistryHelper)registry).smithed$removeRegistryEntry(registryKey);
    }

    public static interface RegistryHelper<T> {
        void smithed$thawRegistry();

        void smithed$removeRegistryEntry(RegistryKey<T> registryKey);
    }

}
