package dev.smithed.companion.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.smithed.companion.container.ItemContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registry;
import net.minecraft.util.collection.DefaultedList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record ComRecipe(List<String> pattern, NbtCompound key, ItemContainer result, String category) {

    public static final Codec<ComRecipe> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.listOf().fieldOf("pattern").forGetter(ComRecipe::pattern),
                    NbtCompound.CODEC.fieldOf("key").forGetter(ComRecipe::key),
                    ItemContainer.CODEC.fieldOf("result").forGetter(ComRecipe::result),
                    Codec.STRING.fieldOf("category").forGetter(ComRecipe::category)
            ).apply(instance, ComRecipe::new)
    );

    public static DefaultedList<Ingredient> computeRecipe(Registry<DatapackItem> registry, ComRecipe recipe) {
        final Map<Character,Ingredient> keys = new HashMap<>();
        final DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(27, Ingredient.empty());

        recipe.key().getKeys().forEach(key -> {
            if(key.length() != 1) throw new NullPointerException();
            final NbtCompound entry = (NbtCompound) recipe.key().get(key);
            final ItemStack input = DatapackItem.parseItemEntry(registry, entry);
            keys.put(key.charAt(0), Ingredient.ofStacks(input));
        });

        int offset = 0;
        for(String line: recipe.pattern()) {
            for(int i = 0; i < line.length(); i++) {
                if(line.charAt(i) != ' ')
                    ingredients.set(offset+i, keys.get(line.charAt(i)));
            }
            offset += line.length();
        }

        return ingredients;
    }
}
