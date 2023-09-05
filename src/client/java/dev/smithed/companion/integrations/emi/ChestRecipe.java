package dev.smithed.companion.integrations.emi;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.smithed.companion.container.BackgroundContainer;
import net.minecraft.recipe.CraftingRecipe;

public class ChestRecipe extends EmiRecipeExtender {

    public ChestRecipe(BackgroundContainer background, EmiRecipeCategory category, CraftingRecipe recipe) {
        super(background, category, recipe, 166, 56);
    }

    public ChestRecipe(EmiRecipeCategory category, CraftingRecipe recipe) {
        this(null, category, recipe);
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        int i = 0;
        for (EmiIngredient ingredient: inputs) {
            widgets.addSlot(ingredient, 18 * (i%9) + 1, 18 * (i/9) + 1);
            i += 1;
        }
        widgets.addSlot(outputs.get(0), -16384, 0).recipeContext(this);

        super.addWidgets(widgets);
    }
}
