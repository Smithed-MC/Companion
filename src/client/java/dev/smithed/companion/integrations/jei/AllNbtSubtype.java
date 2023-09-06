package dev.smithed.companion.integrations.jei;

import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Subtype container for JEI. Will map all provided NBTCompounds to a subtype.
 */
public class AllNbtSubtype implements IIngredientSubtypeInterpreter<ItemStack> {

    private final Map<NbtCompound, String> subtypes;

    public AllNbtSubtype() {
        this.subtypes = new HashMap<>();
    }

    @Override
    @NotNull
    public String apply(ItemStack ingredient, UidContext context) {
        if(ingredient.getNbt() != null) {
            if(subtypes.containsKey(ingredient.getNbt()))
                return subtypes.get(ingredient.getNbt());
        }
        return "";
    }

    public void putSubtype(NbtCompound nbt, String itemId) {
        subtypes.put(nbt,itemId);
    }

}
