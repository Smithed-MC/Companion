package dev.smithed.companion.integrations.jei;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.library.plugins.vanilla.brewing.BrewingRecipeUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Barebones implementation of JEI's BrewingRecipeUtil. This is normally used by JEI to track combined
 * recipe trees (ie. nether wart -> blaze powder -> redstone -> gunpowder), but isn't needed here.
 * This class does just enough to not crash the game.
 */
public class BrewingRecipeExtender extends BrewingRecipeUtil {

    public BrewingRecipeExtender(Identifier id, ItemStack stack) {
        super(new Helper(id, stack));
    }

    private record Helper(Identifier id, ItemStack stack) implements IIngredientHelper<ItemStack> {

        @Override
        @NotNull
        public IIngredientType<ItemStack> getIngredientType() {
            return () -> ItemStack.class;
        }

        @Override
        @NotNull
        public String getDisplayName(ItemStack ingredient) {
            return ingredient.getName().toString();
        }

        @Override
        @NotNull
        public String getUniqueId(ItemStack ingredient, UidContext context) {
            return id.toString();
        }

        @Override
        @NotNull
        public Identifier getResourceLocation(ItemStack ingredient) {
            return new Identifier("minecraft:textures/item/stick.png");
        }

        @Override
        @NotNull
        public ItemStack copyIngredient(ItemStack ingredient) {
            return ingredient.copy();
        }

        @Override
        @NotNull
        public String getErrorInfo(@Nullable ItemStack ingredient) {
            if(ingredient == null)
                return "null";
            else
                return ingredient.toString() + ingredient.getNbt();
        }
    }
}
