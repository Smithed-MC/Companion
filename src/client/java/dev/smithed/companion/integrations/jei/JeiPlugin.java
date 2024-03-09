package dev.smithed.companion.integrations.jei;

import dev.smithed.companion.SmithedMain;
import dev.smithed.companion.integrations.TextureLocations;
import dev.smithed.companion.registry.ComRecipe;
import dev.smithed.companion.registry.DatapackItem;
import dev.smithed.companion.utils.RegistryUtils;
import dev.smithed.companion.registry.RecipeCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.library.load.registration.SubtypeRegistration;
import mezz.jei.library.plugins.vanilla.brewing.JeiBrewingRecipe;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.CraftingRecipeCategory;

import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * JEI plugin loader. Registers DatapackItems, smithed recipes, and smithed recipe categories.
 */
@mezz.jei.api.JeiPlugin
public class JeiPlugin implements IModPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger("smithed-companion");
    private static final Identifier ID = SmithedMain.modID("jei-plugin");

    @Override
    @NotNull
    public Identifier getPluginUid() {
        return ID;
    }

    private static final Map<Identifier,RecipeType<CraftingRecipe>> RECIPETYPES = new HashMap<>();

    /**
     * JEI uses 'subtypes' to distinguish between item variants- ie. knowing that tag.smithed.id is a new item
     * but tag.Damage is just durability.
     * Registers all DatapackItems as a subtype
     * @param registration JEI subtype registry
     */
    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        // Get Registries
        final ClientWorld world = MinecraftClient.getInstance().world;
        if(world == null)
            return;

        final Registry<DatapackItem> registry =  world.getRegistryManager().get(RegistryUtils.DATAPACK_ITEM_REGISTRY);
        final Set<Item> itemSet = new HashSet<>();

        // Register subtype for each unique item instance
        registry.forEach(datapackItem -> {
            final Item item = datapackItem.stack().getItem();
            if(!itemSet.contains(item)) {
                itemSet.add(item);
                if(registration instanceof SubtypeRegistration subtype) {
                    Optional<IIngredientSubtypeInterpreter<ItemStack>> interpreter = subtype.getInterpreters().get(VanillaTypes.ITEM_STACK, new ItemStack(item));
                    if(interpreter.isPresent()) {
                        LOGGER.warn("Failed to register subtype for " + item + ". JEI probably registers its own subtype for this item.");
                        return;
                    }
                }
                registration.registerSubtypeInterpreter(item, new SubtypeExtender<>());
            }
        });
    }

    /**
     * Registers smithed Recipe Categories, which indicates what block the recipe is made with
     * @param registration JEI recipe category registry
     */
    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        // Grab registries
        final ClientWorld world = MinecraftClient.getInstance().world;
        if(world == null)
            return;

        RECIPETYPES.clear();
        final Registry<RecipeCategory> registry = world.getRegistryManager().get(RegistryUtils.RECIPE_CATEGORY);
        final Registry<DatapackItem> itemRegistry = world.getRegistryManager().get(RegistryUtils.DATAPACK_ITEM_REGISTRY);

        // Construct basic block UIs
        final IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        final IDrawableStatic chest = guiHelper.drawableBuilder(TextureLocations.CHEST, 6, 16, 164, 56).build();
        final IDrawableStatic dispenser = guiHelper.drawableBuilder(TextureLocations.DISPENSER, 6, 16, 164, 56).build();
        final IDrawableStatic hopper = guiHelper.drawableBuilder(TextureLocations.HOPPER, 6, 16, 164, 24).build();

        // Register each category
        registry.getIds().forEach(id -> {
            final RecipeCategory recipeCategory = registry.get(id);
            if(recipeCategory == null) return;
            final RecipeType<CraftingRecipe> recipeType = RecipeType.create(id.getNamespace(), id.getPath(), CraftingRecipe.class);
            final ItemStack item = recipeCategory.icon().getItemStack(itemRegistry);

            if(item.getCount() == 0) {
                LOGGER.warn("Failed to load smithed recipe category " + id + " icon " + recipeCategory.icon());
                return;
            }

            // Construct correct GUI and Category object for category type
            IRecipeCategory<CraftingRecipe> category;
            switch (recipeCategory.inventoryType()) {
                case "chest", "barrel", "shulker_box" -> {
                    IDrawable gui = chest;
                    if(recipeCategory.background().isPresent())
                        gui = new LayeredDrawable(gui, recipeCategory.background().get());
                    category = new ChestSizeCategory<>(recipeType, recipeCategory.getDisplayText(), gui, guiHelper.createDrawableItemStack(item));
                }
                case "dispenser", "dropper" -> {
                    IDrawable gui = dispenser;
                    if(recipeCategory.background().isPresent())
                        gui = new LayeredDrawable(gui, recipeCategory.background().get());
                    category = new DispenserSizeCategory<>(recipeType, recipeCategory.getDisplayText(), gui, guiHelper.createDrawableItemStack(item));
                }
                case "hopper" -> {
                    IDrawable gui = hopper;
                    if(recipeCategory.background().isPresent())
                        gui = new LayeredDrawable(gui, recipeCategory.background().get());
                    category = new HopperSizeCategory<>(recipeType, recipeCategory.getDisplayText(), gui, guiHelper.createDrawableItemStack(item));
                }
                default -> {
                    LOGGER.warn("Invalid inventory type " + recipeCategory.inventoryType());
                    return;
                }
            }

            // Register category with JEI and save for later
            RECIPETYPES.put(id, recipeType);
            registration.addRecipeCategories(category);
        });
    }

    /**
     * Registeres smithed Recipes with JEI
     * @param registration JEI recipe registry
     */
    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // Grab registries
        final ClientWorld world = MinecraftClient.getInstance().world;
        if(world == null)
            return;

        final Registry<ComRecipe> registry = world.getRegistryManager().get(RegistryUtils.RECIPES);
        final Registry<DatapackItem> itemRegistry = world.getRegistryManager().get(RegistryUtils.DATAPACK_ITEM_REGISTRY);

        // Create & Collect all recipes into a category:recipes map
        registry.getIds().forEach(id -> {
            final ComRecipe recipe = registry.get(id);
            if(recipe == null) return;
            try {
                int width = 9;
                int height = 3;
                if(recipe.category().equals(new Identifier("minecraft:crafting_table"))) {
                    width = 3;
                }

                final DefaultedList<Ingredient> ingredients = recipe.computeRecipe(itemRegistry, width*height);
                final ItemStack output = recipe.result().getItemStack(itemRegistry);
                final RawShapedRecipe rawRecipe = new RawShapedRecipe(width, height, ingredients, Optional.empty());

                switch (recipe.category().toString()) {
                    case "minecraft:crafting_table" -> {
                        final RecipeEntry<CraftingRecipe> entry = new RecipeEntry<>(id, new ShapedRecipe(id.toString(), CraftingRecipeCategory.MISC, rawRecipe, output));
                        registration.addRecipes(RecipeTypes.CRAFTING, List.of(entry));
                    }
                    case "minecraft:furnace" -> {
                        final RecipeEntry<SmeltingRecipe> entry = new RecipeEntry<>(id, new SmeltingRecipe(id.toString(), CookingRecipeCategory.MISC, ingredients.get(0), output, 0, 200));
                        registration.addRecipes(RecipeTypes.SMELTING, List.of(entry));
                    }
                    case "minecraft:blast_furnace" -> {
                        final RecipeEntry<BlastingRecipe> entry = new RecipeEntry<>(id, new BlastingRecipe(id.toString(), CookingRecipeCategory.MISC, ingredients.get(0), output, 0, 200));
                        registration.addRecipes(RecipeTypes.BLASTING, List.of(entry));
                    }
                    case "minecraft:smoker" -> {
                        final RecipeEntry<SmokingRecipe> entry = new RecipeEntry<>(id, new SmokingRecipe(id.toString(), CookingRecipeCategory.MISC, ingredients.get(0), output, 0, 200));
                        registration.addRecipes(RecipeTypes.SMOKING, List.of(entry));
                    }
                    case "minecraft:campfire" -> {
                        final RecipeEntry<CampfireCookingRecipe> entry = new RecipeEntry<>(id, new CampfireCookingRecipe(id.toString(), CookingRecipeCategory.MISC, ingredients.get(0), output, 0, 200));
                        registration.addRecipes(RecipeTypes.CAMPFIRE_COOKING, List.of(entry));
                    }
                    case "minecraft:smithing_table" -> {
                        final RecipeEntry<SmithingRecipe> entry = new RecipeEntry<>(id, new SmithingTransformRecipe(ingredients.get(0), ingredients.get(1), ingredients.get(2), output));
                        registration.addRecipes(RecipeTypes.SMITHING, List.of(entry));
                    }
                    case "minecraft:brewing_stand" -> {
                        final JeiBrewingRecipe entry = new JeiBrewingRecipe(List.of(ingredients.get(1).getMatchingStacks()), List.of(ingredients.get(0).getMatchingStacks()), output, new BrewingRecipeExtender(id, output));
                        registration.addRecipes(RecipeTypes.BREWING, List.of(entry));
                    }
                    default -> registration.addRecipes(RECIPETYPES.get(recipe.category()), List.of(new ShapedRecipe(id.toString(), CraftingRecipeCategory.MISC, rawRecipe, output)));
                }
            } catch(Exception e) {
                LOGGER.warn("Failed to parse smithed recipe " + id + ". " + e.getLocalizedMessage());
            }
        });
    }

    /**
     * Modifies JEI runtime directly after all mods are initialized.
     * Currently, removes all recipes from JEI that result in a knowledge book.
     * @param jeiRuntime JEI runtime
     */
    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        final ClientWorld world = MinecraftClient.getInstance().world;
        if(world == null)
            return;

        final List<RecipeEntry<CraftingRecipe>> recipes = new ArrayList<>();
        // For every recipe registered in JEI, remove it if the output is a knowledge book.
        jeiRuntime.getRecipeManager().createRecipeLookup(RecipeTypes.CRAFTING).get().forEach(recipe -> {
            if(recipe.value().getResult(world.getRegistryManager()).getItem() == Items.KNOWLEDGE_BOOK)
                recipes.add(recipe);
        });
        jeiRuntime.getRecipeManager().hideRecipes(RecipeTypes.CRAFTING, recipes);
    }
}
