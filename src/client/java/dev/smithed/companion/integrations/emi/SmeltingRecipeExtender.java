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

    public SmeltingRecipeExtender(RecipeType<?> type, Identifier id, String group, CookingRecipeCategory category, Ingredient input, ItemStack output, float experience, int cookTime) {
        super(type, id, group, category, input, output, experience, cookTime);
    }

    public SmeltingRecipeExtender(RecipeType<?> type, Identifier id, CookingRecipeCategory category, Ingredient input, ItemStack output) {
        this(type, id, id.toString(), category, input, output, 0, 200);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }
}