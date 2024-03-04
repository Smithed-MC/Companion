package dev.smithed.companion.integrations.emi;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.util.Identifier;

/**
 * Implements MC's 'cooking recipe' for use with EMI.
 * Normally this is for the serializer, but that isn't needed and simply return null;
 */
public class SmeltingRecipeExtender extends AbstractCookingRecipe {

    public SmeltingRecipeExtender(RecipeType<?> type, String group, CookingRecipeCategory category, Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
        super(type, group, category, ingredient, result, experience, cookingTime);
    }

    public SmeltingRecipeExtender(RecipeType<?> type, String group, CookingRecipeCategory category, Ingredient ingredient, ItemStack result) {
        super(type, group, category, ingredient, result, 0, 200);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }
}
