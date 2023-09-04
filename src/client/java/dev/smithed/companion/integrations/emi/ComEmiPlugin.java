package dev.smithed.companion.integrations.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.smithed.companion.container.BackgroundContainer;
import dev.smithed.companion.registry.ComRecipe;
import dev.smithed.companion.registry.DatapackItem;
import dev.smithed.companion.registry.RecipeCategory;
import dev.smithed.companion.utils.RegistryUtils;
import io.netty.handler.codec.CodecException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.Registry;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.collection.DefaultedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        registerCategories(registry);
        registerRecipes(registry);
    }

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

    }

    private void registerRecipes(EmiRegistry emiRegistry) {
        final ClientWorld world = MinecraftClient.getInstance().world;
        if(world == null)
            return;

        final Registry<ComRecipe> recipeRegistry = world.getRegistryManager().get(RegistryUtils.RECIPES);
        final Registry<DatapackItem> itemRegistry = world.getRegistryManager().get(RegistryUtils.DATAPACK_ITEM_REGISTRY);
        final Registry<RecipeCategory> categoryRegistry = world.getRegistryManager().get(RegistryUtils.RECIPE_CATEGORY);

        recipeRegistry.getIds().forEach(id -> {
            final ComRecipe recipe = recipeRegistry.get(id);
            if (recipe == null) return;

            if(!RECIPECATEGORIES.containsKey(recipe.category()) && !recipe.category().equals(new Identifier("minecraft:crafting_table"))) {
                LOGGER.warn("Missing smithed recipe category " + recipe.category());
                return;
            }

            final RecipeCategory recipeCategory = categoryRegistry.get(recipe.category());

            String invType;
            if(recipeCategory != null)
                invType = recipeCategory.inventoryType();
            else
                invType = "crafting_table";

            try {
                int width;
                int height;

                switch (invType) {
                    case "chest", "barrel", "shulker_box" -> { width = 9; height = 3; }
                    case "dispenser", "dropper", "crafting_table" -> { width = 3; height = 3; }
                    case "hopper" -> { width = 5; height = 1; }
                    default -> throw new CodecException("Unknown category type.");
                }

                final DefaultedList<Ingredient> ingredients = ComRecipe.computeRecipe(itemRegistry, recipe, width*height);
                final ItemStack output = recipe.result().getItemStack(itemRegistry);
                final CraftingRecipe craftingRecipe = new ShapedRecipe(id, id.toString(), CraftingRecipeCategory.MISC, width, height, ingredients, output);

                BackgroundContainer background = null;
                if(recipeCategory != null && recipeCategory.background().isPresent())
                    background = recipeCategory.background().get();

                EmiRecipe recipeOut;
                switch (invType) {
                    case "chest", "barrel", "shulker_box" ->
                            recipeOut = new ChestRecipe(background, RECIPECATEGORIES.get(recipe.category()), craftingRecipe);
                    case "dispenser", "dropper" ->
                            recipeOut = new DispenserRecipe(background, RECIPECATEGORIES.get(recipe.category()), craftingRecipe);
                    case "hopper" ->
                            recipeOut = new HopperRecipe(background, RECIPECATEGORIES.get(recipe.category()), craftingRecipe);
                    case "crafting_table" -> {
                        final List<EmiIngredient> input = new ArrayList<>();
                        ingredients.forEach(ingredient -> input.add(EmiIngredient.of(ingredient)));
                        recipeOut = new EmiCraftingRecipe(input, EmiStack.of(output), id, false);
                    }
                    default -> recipeOut = null;
                }
                if(recipeOut != null)
                    emiRegistry.addRecipe(recipeOut);
            } catch(Exception e) {
                LOGGER.warn("Failed to parse smithed recipe " + id + "." + e.getLocalizedMessage());
            }
        });
    }

}
