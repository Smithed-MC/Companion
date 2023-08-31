package dev.smithed.companion.integrations.jei;

import dev.smithed.companion.SmithedMain;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.ModIds;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@mezz.jei.api.JeiPlugin
public class JeiPlugin implements IModPlugin {

    private static final Identifier ID = new Identifier(SmithedMain.MODID, "smithed");

    @Override
    public Identifier getPluginUid() {
        return ID;
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

        registration.addRecipes(RecipeTypes.CRAFTING, recipes);
    }

}
