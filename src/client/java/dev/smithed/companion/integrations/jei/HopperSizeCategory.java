package dev.smithed.companion.integrations.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class HopperSizeCategory<T extends Recipe<?>> extends DynamicCategory<T> {

    public HopperSizeCategory(RecipeType<T> recipeType, Text title, IDrawable background, IDrawable icon) {
        super(recipeType, title, background, icon);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses) {
        int i = 0;
        for (Ingredient ingredient : recipe.getIngredients()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 18 * i + 38, 4).addIngredients(ingredient);
            i += 1;
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, -16384, 0).addItemStack(new ItemStack(Registries.ITEM.get(new Identifier("stick"))));
    }
}
