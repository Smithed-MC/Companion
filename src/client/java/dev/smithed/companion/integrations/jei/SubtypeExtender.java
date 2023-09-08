package dev.smithed.companion.integrations.jei;

import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

public class SubtypeExtender<T extends ItemStack> implements IIngredientSubtypeInterpreter<T> {

    @Override
    @NotNull
    public String apply(T ingredient, UidContext context) {
        if(ingredient.getNbt() == null)
            return "";

        final NbtCompound nbt = ingredient.getNbt().copy();
        nbt.remove("display");
        nbt.remove("CustomModelData");
        nbt.remove("HideFlags");

        if(nbt.getSize() == 0)
            return "";

        return nbt.toString();
    }
}
