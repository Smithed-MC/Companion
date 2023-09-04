package dev.smithed.companion.integrations.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class DispenserSizeCategory<T extends Recipe<?>> extends DynamicCategory<T> {

    public DispenserSizeCategory(RecipeType<T> recipeType, Text title, IDrawable background, IDrawable icon) {
        super(recipeType, title, background, icon);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses) {
        final ClientWorld world = MinecraftClient.getInstance().world;
        if(world == null)
            return;

        int i = 0;
        for (Ingredient ingredient : recipe.getIngredients()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 18 * (i%3) + 56, 18 * (i/3) + 1).addIngredients(ingredient);
            i += 1;
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, -16384, 0).addItemStack(recipe.getOutput(world.getRegistryManager()));
    }

    public int[] getSize() {
        return new int[]{3,3};
    }
}
