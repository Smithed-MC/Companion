package dev.smithed.companion.integrations.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.text.Text;

public class ChestSizeCategory<T extends Recipe<?>> extends DynamicCategory<T> {

    public ChestSizeCategory(RecipeType<T> recipeType, Text title, IDrawable background, IDrawable icon) {
        super(recipeType, title, background, icon);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses) {
        final ClientWorld world = MinecraftClient.getInstance().world;
        if(world == null)
            return;

        int i = 0;
        for (Ingredient ingredient : recipe.getIngredients()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 18 * (i%9) + 2, 18 * (i/9) + 2).addIngredients(ingredient);
            i += 1;
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, -16384, 0).addItemStack(recipe.getResult(world.getRegistryManager()));
    }

}
