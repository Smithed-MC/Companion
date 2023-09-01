package dev.smithed.companion.integrations.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class DynamicCategory<T extends Recipe<?>> implements IRecipeCategory<T> {

    private final RecipeType<T> recipeType;
    private final Text title;
    private final IDrawable background;
    private final IDrawable icon;

    public DynamicCategory(RecipeType<T> recipeType, Text title, IDrawable background, IDrawable icon) {
        this.recipeType = recipeType;
        this.title = title;
        this.background = background;
        this.icon = icon;
    }

    @Override
    @NotNull
    public RecipeType<T> getRecipeType() {
        return recipeType;
    }

    @Override
    @NotNull
    public Text getTitle() {
        return title;
    }

    @Override
    @NotNull
    public IDrawable getBackground() {
        return background;
    }

    @Override
    @NotNull
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses) {
        final ClientWorld world = MinecraftClient.getInstance().world;
        if(world != null) {
            int i = 0;
            for (Ingredient ingredient : recipe.getIngredients()) {
                builder.addSlot(RecipeIngredientRole.INPUT, 0, i).addIngredients(ingredient);
                i += 8;
            }
            builder.addSlot(RecipeIngredientRole.OUTPUT, 15, 15).addItemStack(recipe.getOutput(world.getRegistryManager()));
        }

    }
}
