package dev.smithed.companion.integrations.jei;

import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AllNbtSubtype implements IIngredientSubtypeInterpreter<ItemStack> {

    private final String subtypeId;

    public AllNbtSubtype(String subtypeId) {
        this.subtypeId = subtypeId;
    }

    @Override
    @NotNull
    public String apply(ItemStack ingredient, UidContext context) {
        if(ingredient.getNbt() != null && ingredient.getNbt().getSize() > 0) {
            return subtypeId;
        } else {
            return "";
        }
    }

}
