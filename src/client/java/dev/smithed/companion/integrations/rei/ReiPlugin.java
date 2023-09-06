package dev.smithed.companion.integrations.rei;

import dev.smithed.companion.container.BackgroundContainer;
import dev.smithed.companion.registry.ComRecipe;
import dev.smithed.companion.registry.DatapackItem;
import dev.smithed.companion.registry.RecipeCategory;
import dev.smithed.companion.utils.RegistryUtils;
import io.netty.handler.codec.CodecException;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.plugin.common.displays.DefaultCampfireDisplay;
import me.shedaniel.rei.plugin.common.displays.DefaultSmithingDisplay;
import me.shedaniel.rei.plugin.common.displays.brewing.DefaultBrewingDisplay;
import me.shedaniel.rei.plugin.common.displays.cooking.DefaultBlastingDisplay;
import me.shedaniel.rei.plugin.common.displays.cooking.DefaultSmeltingDisplay;
import me.shedaniel.rei.plugin.common.displays.cooking.DefaultSmokingDisplay;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.Registry;
import net.minecraft.util.collection.DefaultedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * REI plugin loader. Registers DatapackItems, smithed recipes, and smithed recipe categories.
 */
public class ReiPlugin implements REIClientPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger("smithed-companion");

    @Override
    public void registerCategories(CategoryRegistry registry) {
        // Grab registries
        final ClientWorld world = MinecraftClient.getInstance().world;
        if(world == null)
            return;

        final Registry<RecipeCategory> recipeRegistry = world.getRegistryManager().get(RegistryUtils.RECIPE_CATEGORY);
        final Registry<DatapackItem> itemRegistry = world.getRegistryManager().get(RegistryUtils.DATAPACK_ITEM_REGISTRY);

        // Register each category
        recipeRegistry.getIds().forEach(id -> {
            final RecipeCategory recipeCategory = recipeRegistry.get(id);
            if (recipeCategory == null) return;
            final ItemStack item = recipeCategory.icon().getItemStack(itemRegistry);

            if (item.getCount() == 0) {
                LOGGER.warn("Failed to load smithed recipe category " + id + " icon " + recipeCategory.icon());
                return;
            }

            final BackgroundContainer background = recipeCategory.background().isPresent() ? recipeCategory.background().get() : null;

            DisplayCategory<Display> category;
            switch (recipeCategory.inventoryType()) {
                case "chest", "barrel", "shulker_box" -> category = new ChestCategory<>(id, recipeCategory.getDisplayText(), item, background);
                case "dispenser", "dropper" -> category = new DispenserCategory<>(id, recipeCategory.getDisplayText(), item, background);
                case "hopper" -> category = new HopperCategory<>(id, recipeCategory.getDisplayText(), item, background);
                default -> {
                    LOGGER.warn("Invalid inventory type " + recipeCategory.inventoryType());
                    return;
                }
            }
            registry.add(category);
        });
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        // Grab registries
        final ClientWorld world = MinecraftClient.getInstance().world;
        if(world == null)
            return;

        final Registry<ComRecipe> recipeRegistry = world.getRegistryManager().get(RegistryUtils.RECIPES);
        final Registry<DatapackItem> itemRegistry = world.getRegistryManager().get(RegistryUtils.DATAPACK_ITEM_REGISTRY);
        final Registry<RecipeCategory> categoryRegistry = world.getRegistryManager().get(RegistryUtils.RECIPE_CATEGORY);

        // REI needs smelting data passed as an NBT component
        final NbtCompound smeltData = new NbtCompound();
        smeltData.putFloat("xp", 0f);
        smeltData.putDouble("cookTime", 200d);

        // Register each recipe
        recipeRegistry.getIds().forEach(id -> {
            final ComRecipe recipe = recipeRegistry.get(id);
            if(recipe == null) return;

            final RecipeCategory recipeCategory = categoryRegistry.get(recipe.category());

            String invType;
            if(recipeCategory != null)
                invType = recipeCategory.inventoryType();
            else
                invType = recipe.category().toString();

            try {
                // Store recipe width & height
                int width;
                int height;
                switch (invType) {
                    case "chest", "barrel", "shulker_box" -> { width = 9; height = 3; }
                    case "dispenser", "dropper", "minecraft:crafting_table" -> { width = 3; height = 3; }
                    case "hopper" -> { width = 5; height = 1; }
                    case "minecraft:smithing_table" -> { width = 3; height = 1; }
                    case "minecraft:brewing_stand" -> { width = 2; height = 1; }
                    case "minecraft:furnace", "minecraft:blast_furnace", "minecraft:smoker", "minecraft:campfire" -> { width = 1; height = 1; }
                    default -> throw new CodecException("Unknown category type.");
                }

                // Compute recipe in/out
                final DefaultedList<Ingredient> ingredients = recipe.computeRecipe(itemRegistry, width*height);
                final ItemStack output = recipe.result().getItemStack(itemRegistry);

                // Init recipe
                final Display reiRecipe;
                switch (invType) {
                    case "minecraft:crafting_table" -> reiRecipe = DefaultCraftingDisplay.of(new ShapedRecipe(id, id.toString(), CraftingRecipeCategory.MISC, width, height, ingredients, output));
                    case "minecraft:brewing_stand" -> reiRecipe = new DefaultBrewingDisplay(ingredients.get(0), ingredients.get(1), output);
                    case "minecraft:furnace" -> reiRecipe = new DefaultSmeltingDisplay(ReiUtils.convertIngredientList(ingredients), List.of(ReiUtils.convertItemStack(output)), smeltData);
                    case "minecraft:blast_furnace" -> reiRecipe = new DefaultBlastingDisplay(ReiUtils.convertIngredientList(ingredients), List.of(ReiUtils.convertItemStack(output)), smeltData);
                    case "minecraft:smoker" -> reiRecipe = new DefaultSmokingDisplay(ReiUtils.convertIngredientList(ingredients), List.of(ReiUtils.convertItemStack(output)), smeltData);
                    case "minecraft:campfire" -> reiRecipe = new DefaultCampfireDisplay(ReiUtils.convertIngredientList(ingredients), List.of(ReiUtils.convertItemStack(output)), Optional.empty(), smeltData);
                    case "minecraft:smithing_table" -> reiRecipe = new DefaultSmithingDisplay(ReiUtils.convertIngredientList(ingredients), List.of(ReiUtils.convertItemStack(output)), Optional.empty());
                    default -> reiRecipe = new DisplayExtender(id, ingredients, output, recipe.category());
                }
                // Register
                registry.add(reiRecipe);
            } catch(Exception e) {
                LOGGER.warn("Failed to parse smithed recipe " + id + ". " + e.getLocalizedMessage());
            }
        });
    }

}
