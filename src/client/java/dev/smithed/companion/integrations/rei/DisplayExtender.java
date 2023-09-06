package dev.smithed.companion.integrations.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;
import java.util.Optional;

public class DisplayExtender extends BasicDisplay {

    private final CategoryIdentifier<?> category;

    public DisplayExtender(Identifier id, DefaultedList<Ingredient> input, ItemStack output, Identifier category) {
        this(id, input, output, CategoryIdentifier.of(category));
    }

    public DisplayExtender(Identifier id, DefaultedList<Ingredient> input, ItemStack output, CategoryIdentifier<?> category) {
        super(ReiUtils.convertIngredientList(input), List.of(ReiUtils.convertItemStack(output)), Optional.of(id));
        this.category = category;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return category;
    }
}
