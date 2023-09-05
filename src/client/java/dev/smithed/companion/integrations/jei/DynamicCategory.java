package dev.smithed.companion.integrations.jei;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.recipe.Recipe;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

/**
 * Archetype for registering categories in JEI.
 * Handles recipes & rendering.
 */
public abstract class DynamicCategory<T extends Recipe<?>> implements IRecipeCategory<T> {

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

}
