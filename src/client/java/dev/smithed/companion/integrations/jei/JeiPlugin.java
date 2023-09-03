package dev.smithed.companion.integrations.jei;

import dev.smithed.companion.SmithedMain;
import dev.smithed.companion.registry.ComRecipe;
import dev.smithed.companion.registry.DatapackItem;
import dev.smithed.companion.utils.RegistryUtils;
import dev.smithed.companion.registry.RecipeCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;

import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@mezz.jei.api.JeiPlugin
public class JeiPlugin implements IModPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger("smithed-companion");
    private static final Identifier ID = new Identifier(SmithedMain.MODID, "smithed");

    @Override
    @NotNull
    public Identifier getPluginUid() {
        return ID;
    }

    private static final Map<Item,AllNbtSubtype> SUBTYPES = new HashMap<>();
    private static final Map<Identifier,RecipeType<CraftingRecipe>> RECIPETYPES = new HashMap<>();

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        SUBTYPES.clear();

        final ClientWorld world = MinecraftClient.getInstance().world;
        if(world == null)
            return;

        final Registry<DatapackItem> registry =  world.getRegistryManager().get(RegistryUtils.DATAPACK_ITEM_REGISTRY);
        registry.forEach(datapackItem -> {
            final ItemStack item = datapackItem.stack();
            SUBTYPES.putIfAbsent(item.getItem(), new AllNbtSubtype());
            final Identifier id = registry.getId(datapackItem);
            if(id == null) return;
            SUBTYPES.get(item.getItem()).putSubtype(item.getNbt(), id.toString());
        });
        for(Map.Entry<Item,AllNbtSubtype> entry: SUBTYPES.entrySet()) {
            registration.registerSubtypeInterpreter(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        final ClientWorld world = MinecraftClient.getInstance().world;
        if(world == null)
            return;

        RECIPETYPES.clear();
        final IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();

        final Identifier chestLocation = new Identifier(Identifier.DEFAULT_NAMESPACE, "textures/gui/container/shulker_box.png");
        final IDrawableStatic chest = guiHelper.drawableBuilder(chestLocation, 6, 16, 164, 56).build();
        final Identifier dispenserLocation = new Identifier(Identifier.DEFAULT_NAMESPACE, "textures/gui/container/dispenser.png");
        final IDrawableStatic dispenser = guiHelper.drawableBuilder(dispenserLocation, 6, 16, 164, 56).build();
        final Identifier hopperLocation = new Identifier(Identifier.DEFAULT_NAMESPACE, "textures/gui/container/hopper.png");
        final IDrawableStatic hopper = guiHelper.drawableBuilder(hopperLocation, 6, 16, 164, 24).build();

        final Registry<RecipeCategory> registry = world.getRegistryManager().get(RegistryUtils.RECIPE_CATEGORY);
        registry.getIds().forEach(id -> {
            final RecipeCategory recipeCategory = registry.get(id);
            if(recipeCategory == null) return;
            final RecipeType<CraftingRecipe> recipeType = RecipeType.create(id.getNamespace(), id.getPath(), CraftingRecipe.class);
            final DatapackItem item = world.getRegistryManager().get(RegistryUtils.DATAPACK_ITEM_REGISTRY).get(recipeCategory.icon());

            if(item == null) {
                LOGGER.warn("Failed to load icon " + recipeCategory.icon());
                return;
            }

            IRecipeCategory<CraftingRecipe> category;
            switch (recipeCategory.inventoryType()) {
                case "chest", "barrel", "shulker_box" -> {
                    IDrawable gui = chest;
                    System.out.println("Flag 1 " + recipeCategory);
                    if(recipeCategory.background().isPresent()) {
                        gui = new LayeredDrawable(gui, recipeCategory.background().get());
                        System.out.println("Flag 2");
                    }
                    category = new ChestSizeCategory<>(recipeType, recipeCategory.getDisplayText(), gui, guiHelper.createDrawableItemStack(item.stack()));
                }
                case "dispenser", "dropper" -> {
                    IDrawable gui = dispenser;
                    if(recipeCategory.background().isPresent())
                        gui = new LayeredDrawable(gui, recipeCategory.background().get());
                    category = new DispenserSizeCategory<>(recipeType, recipeCategory.getDisplayText(), gui, guiHelper.createDrawableItemStack(item.stack()));
                }
                case "hopper" -> {
                    IDrawable gui = hopper;
                    if(recipeCategory.background().isPresent())
                        gui = new LayeredDrawable(gui, recipeCategory.background().get());
                    category = new HopperSizeCategory<>(recipeType, recipeCategory.getDisplayText(), gui, guiHelper.createDrawableItemStack(item.stack()));
                }
                default -> {
                    LOGGER.warn("Invalid inventory type " + recipeCategory.inventoryType());
                    return;
                }
            }
            RECIPETYPES.put(id, recipeType);
            registration.addRecipeCategories(category);
        });
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        final ClientWorld world = MinecraftClient.getInstance().world;
        if(world == null)
            return;

        final Map<String,List<CraftingRecipe>> recipes = new HashMap<>();
        final Registry<ComRecipe> registry = world.getRegistryManager().get(RegistryUtils.RECIPES);
        final Registry<DatapackItem> itemRegistry = world.getRegistryManager().get(RegistryUtils.DATAPACK_ITEM_REGISTRY);

        registry.getIds().forEach(id -> {
            final ComRecipe recipe = registry.get(id);
            if(recipe == null) return;
            try {
                final DefaultedList<Ingredient> ingredients = ComRecipe.computeRecipe(itemRegistry, recipe);
                final ItemStack output = recipe.result().getItemStack(itemRegistry);
                recipes.putIfAbsent(recipe.category(),new ArrayList<>());
                recipes.get(recipe.category()).add(new ShapedRecipe(ID, "test", CraftingRecipeCategory.MISC, 3, 3, ingredients, output));
            } catch(Exception e) {
                LOGGER.warn("Failed to parse smithed recipe " + id);
                LOGGER.warn(String.valueOf(e));
            }
        });

        for(Map.Entry<String,List<CraftingRecipe>> entry: recipes.entrySet()) {
            if(RECIPETYPES.containsKey(new Identifier(entry.getKey()))) {
                final RecipeType<CraftingRecipe> type = RECIPETYPES.get(new Identifier(entry.getKey()));
                registration.addRecipes(type, entry.getValue());
            } else {
                LOGGER.warn("Missing recipe category " + entry.getKey());
            }
        }
    }

}
