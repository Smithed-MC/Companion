package dev.smithed.companion.integrations.jei;

import dev.smithed.companion.SmithedMain;
import dev.smithed.companion.utils.DatapackItemUtils;
import dev.smithed.companion.utils.RegistryUtils;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.Registries;

import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@mezz.jei.api.JeiPlugin
public class JeiPlugin implements IModPlugin {

    private static final Identifier ID = new Identifier(SmithedMain.MODID, "smithed");

    @Override
    @NotNull
    public Identifier getPluginUid() {
        return ID;
    }

    public static final RecipeType<CraftingRecipe> HEAVYWORKBENCH = RecipeType.create(SmithedMain.MODID, "heavy_workbench", CraftingRecipe.class);
    private static final Map<Item,AllNbtSubtype> SUBTYPES = new HashMap<>();

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        SUBTYPES.clear();

        final ClientWorld world = MinecraftClient.getInstance().world;
        if(world != null) {
            Registry<DatapackItemUtils.DatapackItem> registry =  world.getRegistryManager().get(RegistryUtils.DATAPACK_ITEM_REGISTRY);
            registry.forEach(datapackItem -> {
                final ItemStack item = datapackItem.getStack();
                SUBTYPES.putIfAbsent(item.getItem(), new AllNbtSubtype());
                SUBTYPES.get(item.getItem()).putSubtype(item.getNbt(), datapackItem.getIdentifier().toString());
            });
            for(Map.Entry<Item,AllNbtSubtype> entry: SUBTYPES.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
                registration.registerSubtypeInterpreter(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        final IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        final Identifier iconLocation = new Identifier(SmithedMain.MODID, "icon.png");

        final IRecipeCategory<CraftingRecipe> category = new DynamicCategory<>(
                HEAVYWORKBENCH,
                Text.of("Heavy Workbench"),
                guiHelper.drawableBuilder(iconLocation, 0, 0, 64, 64).addPadding(0, 0, 0, 16).build(),
                guiHelper.createDrawable(iconLocation, 0, 0, 16, 16)
        );
        registration.addRecipeCategories(category);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        final List<CraftingRecipe> recipes = new ArrayList<>();

        final DefaultedList<Ingredient> input = DefaultedList.ofSize(0);
        final NbtCompound inData = new NbtCompound();
        inData.putString("id", "minecraft:stick");
        inData.putByte("Count", (byte)2);
        final NbtCompound inDataTag = new NbtCompound();
        inDataTag.putBoolean("test",true);
        inData.put("tag",inDataTag);
        input.add(Ingredient.ofStacks(ItemStack.fromNbt(inData)));
        final ItemStack out = new ItemStack(Registries.ITEM.get(new Identifier("stick")));
        recipes.add(new ShapedRecipe(ID, "test", CraftingRecipeCategory.MISC, 1, 1, input, out));

        registration.addRecipes(HEAVYWORKBENCH, recipes);
    }

}
