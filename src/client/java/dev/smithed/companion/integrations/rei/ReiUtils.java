package dev.smithed.companion.integrations.rei;

import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.List;

public class ReiUtils {

    public static List<EntryIngredient> convertIngredientList(DefaultedList<Ingredient> input) {
        List<EntryIngredient> list = new ArrayList<>();
        input.forEach(item ->  {
            final ItemStack itemStack = item.getMatchingStacks().length > 0 ? item.getMatchingStacks()[0] : ItemStack.EMPTY;
            list.add(EntryIngredient.of(EntryStack.of(VanillaEntryTypes.ITEM, itemStack)));
        });
        return list;
    }

    public static EntryIngredient convertItemStack(ItemStack stack) {
        return EntryIngredient.of(EntryStack.of(VanillaEntryTypes.ITEM, stack));
    }
}
