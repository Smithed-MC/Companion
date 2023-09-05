package dev.smithed.companion.integrations.emi;

import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.smithed.companion.container.BackgroundContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.recipe.CraftingRecipe;

/**
 * Implements EMI's recipes. Handles ingredients & rending the background.
 */
public abstract class EmiRecipeExtender extends BasicEmiRecipe {

    private final BackgroundContainer background;

    public EmiRecipeExtender(BackgroundContainer background, EmiRecipeCategory category , CraftingRecipe recipe, int width, int height) {
        super(category, recipe.getId(), width, height);
        this.background = background;

        final ClientWorld world = MinecraftClient.getInstance().world;
        assert world != null : "Client world is null ?? !!";

        recipe.getIngredients().forEach(ingredient -> this.inputs.add(EmiIngredient.of(ingredient)));
        this.outputs.add(EmiStack.of(recipe.getOutput(world.getRegistryManager())));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        if(background != null) {
            widgets.addTexture(background.icon(), 0, 0, background.width(), background.height(), background.u(), background.v());
        }
    }

}
