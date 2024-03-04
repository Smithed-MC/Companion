package dev.smithed.companion.integrations.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.*;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.recipe.EmiBrewingRecipe;
import dev.emi.emi.recipe.EmiCookingRecipe;
import dev.emi.emi.recipe.EmiSmithingRecipe;
import dev.smithed.companion.container.BackgroundContainer;
import dev.smithed.companion.registry.ComRecipe;
import dev.smithed.companion.registry.DatapackItem;
import dev.smithed.companion.registry.RecipeCategory;
import dev.smithed.companion.utils.RegistryUtils;
import io.netty.handler.codec.CodecException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Registers datapack items, recipe categories, and recipes to EMI if it is loaded
 */
public class ComEmiPlugin implements EmiPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger("smithed-companion");

    private static final Map<Identifier, EmiRecipeCategory> RECIPECATEGORIES = new HashMap<>();

    /**
     * Main registry function, splits into sub-functions
     */
    @Override
    public void register(EmiRegistry registry) {
        registerComparisions(registry);
        registerCategories(registry);
        registerRecipes(registry);

        // Remove knowledge book crafting recipes
        registry.removeRecipes(emiRecipe -> emiRecipe.getOutputs().contains(EmiStack.of(new ItemStack(Items.KNOWLEDGE_BOOK))));
    }

    private void registerComparisions(EmiRegistry registry) {
        // Get Registries
        final ClientWorld world = MinecraftClient.getInstance().world;
        if(world == null)
            return;

        final Registry<DatapackItem> itemRegistry =  world.getRegistryManager().get(RegistryUtils.DATAPACK_ITEM_REGISTRY);
        final Set<Item> itemSet = new HashSet<>();

        // Collect all DatapackItems into a map of base_item:subtype
        itemRegistry.forEach(datapackItem -> {
            final Item item = datapackItem.stack().getItem();
            if(!itemSet.contains(item)) {
                itemSet.add(item);
                registry.setDefaultComparison(item, Comparison.compareData(stack -> {
                    if(stack.getNbt() == null)
                        return null;

                    final NbtCompound nbt = stack.getNbt();
                    nbt.remove("display");
                    nbt.remove("CustomModelData");
                    nbt.remove("HideFlags");

                    if(nbt.getSize() == 0)
                        return null;

                    return nbt;
                }));
            }
        });
    }

    /**
     * Grabs all smithed recipe categories from datapacks and registers them with EMI
     */
    private void registerCategories(EmiRegistry emiRegistry) {
        // Grab registries
        final ClientWorld world = MinecraftClient.getInstance().world;
        if(world == null)
            return;

        RECIPECATEGORIES.clear();
        final Registry<RecipeCategory> categoryRegistry = world.getRegistryManager().get(RegistryUtils.RECIPE_CATEGORY);
        final Registry<DatapackItem> itemRegistry = world.getRegistryManager().get(RegistryUtils.DATAPACK_ITEM_REGISTRY);

        // Register each category with EMI
        categoryRegistry.getIds().forEach(id -> {
            final RecipeCategory recipeCategory = categoryRegistry.get(id);
            if(recipeCategory == null) return;

            // EMI requires a 'workstation' (the block used to do the crafting), and the category itself
            final EmiStack workstation = EmiStack.of(recipeCategory.icon().getItemStack(itemRegistry));
            final EmiRecipeCategory category = new EmiRecipeCategory(id, workstation, new EmiTexture(new Identifier("emi", "textures/gui/widgets.png"), 0, 200, 16, 16));

            // Store the category for later & register with EMI
            RECIPECATEGORIES.put(id, category);
            emiRegistry.addCategory(category);
            emiRegistry.addWorkstation(category, workstation);
        });

        // Store vanilla categories
        RECIPECATEGORIES.put(new Identifier("minecraft:crafting_table"), VanillaEmiRecipeCategories.CRAFTING);
        RECIPECATEGORIES.put(new Identifier("minecraft:furnace"), VanillaEmiRecipeCategories.SMELTING);
        RECIPECATEGORIES.put(new Identifier("minecraft:blast_furnace"), VanillaEmiRecipeCategories.BLASTING);
        RECIPECATEGORIES.put(new Identifier("minecraft:smoker"), VanillaEmiRecipeCategories.SMOKING);
        RECIPECATEGORIES.put(new Identifier("minecraft:brewing_stand"), VanillaEmiRecipeCategories.BREWING);
        RECIPECATEGORIES.put(new Identifier("minecraft:campfire"), VanillaEmiRecipeCategories.CAMPFIRE_COOKING);
        RECIPECATEGORIES.put(new Identifier("minecraft:smithing_table"), VanillaEmiRecipeCategories.SMITHING);
    }

    /**
     * Grabs all smithed recipes from datapacks and registers them with EMI
     */
    private void registerRecipes(EmiRegistry emiRegistry) {
        // Grab registries
        final ClientWorld world = MinecraftClient.getInstance().world;
        if(world == null)
            return;

        final Registry<ComRecipe> recipeRegistry = world.getRegistryManager().get(RegistryUtils.RECIPES);
        final Registry<DatapackItem> itemRegistry = world.getRegistryManager().get(RegistryUtils.DATAPACK_ITEM_REGISTRY);
        final Registry<RecipeCategory> categoryRegistry = world.getRegistryManager().get(RegistryUtils.RECIPE_CATEGORY);

        // Register each recipe with EMI
        recipeRegistry.getIds().forEach(id -> {
            final ComRecipe recipe = recipeRegistry.get(id);
            if (recipe == null) return;

            // Get recipe category associated with recipe
            if(!RECIPECATEGORIES.containsKey(recipe.category())) {
                LOGGER.warn("Missing smithed recipe category " + recipe.category());
                return;
            }

            final RecipeCategory recipeCategory = categoryRegistry.get(recipe.category());
            final EmiRecipeCategory emiCategory =  RECIPECATEGORIES.get(recipe.category());

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

                // Get recipe items
                final DefaultedList<Ingredient> ingredients = recipe.computeRecipe(itemRegistry, width*height);
                final ItemStack output = recipe.result().getItemStack(itemRegistry);
                final CraftingRecipe craftingRecipe = new ShapedRecipe(id.toString(), CraftingRecipeCategory.MISC, new RawShapedRecipe(width, height, ingredients, Optional.empty()), output);

                final List<EmiIngredient> emiIngredients = new ArrayList<>();
                ingredients.forEach(ingredient -> emiIngredients.add(EmiIngredient.of(ingredient)));

                // Add background if present
                BackgroundContainer background = null;
                if(recipeCategory != null && recipeCategory.background().isPresent())
                    background = recipeCategory.background().get();

                // Create Recipe based on type
                EmiRecipe recipeOut;
                switch (invType) {
                    case "chest", "barrel", "shulker_box" -> recipeOut = new ChestRecipe(background, emiCategory, id, craftingRecipe);
                    case "dispenser", "dropper" -> recipeOut = new DispenserRecipe(background, emiCategory, id, craftingRecipe);
                    case "hopper" -> recipeOut = new HopperRecipe(background, emiCategory, id, craftingRecipe);
                    case "minecraft:crafting_table" -> recipeOut = new EmiCraftingRecipe(emiIngredients, EmiStack.of(output), id, false);
                    case "minecraft:brewing_stand" -> recipeOut = new EmiBrewingRecipe(emiIngredients.get(0).getEmiStacks().get(0), emiIngredients.get(1), EmiStack.of(output), id);
                    case "minecraft:furnace" -> recipeOut = new EmiCookingRecipe(new SmeltingRecipeExtender(RecipeType.SMELTING, id.toString(), CookingRecipeCategory.MISC, ingredients.get(0), output), emiCategory, 1, false);
                    case "minecraft:blast_furnace" -> recipeOut = new EmiCookingRecipe(new SmeltingRecipeExtender(RecipeType.BLASTING, id.toString(), CookingRecipeCategory.MISC, ingredients.get(0), output), emiCategory, 1, false);
                    case "minecraft:smoker" -> recipeOut = new EmiCookingRecipe(new SmeltingRecipeExtender(RecipeType.SMOKING, id.toString(), CookingRecipeCategory.MISC, ingredients.get(0), output), emiCategory, 1, false);
                    case "minecraft:campfire" -> recipeOut = new EmiCookingRecipe(new SmeltingRecipeExtender(RecipeType.CAMPFIRE_COOKING, id.toString(), CookingRecipeCategory.MISC, ingredients.get(0), output), emiCategory, 1, false);
                    case "minecraft:smithing_table" -> recipeOut = new EmiSmithingRecipe(emiIngredients.get(0), emiIngredients.get(1), emiIngredients.get(2), EmiStack.of(output), id);
                    default -> recipeOut = null;
                }
                // Register with EMI
                emiRegistry.addRecipe(recipeOut);
            } catch(Exception e) {
                LOGGER.warn("Failed to parse smithed recipe " + id + "." + e.getLocalizedMessage());
            }
        });
    }

}
