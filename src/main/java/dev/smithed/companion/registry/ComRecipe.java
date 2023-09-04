package dev.smithed.companion.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.smithed.companion.container.ItemContainer;
import io.netty.handler.codec.CodecException;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registry;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;
import java.util.Map;

public record ComRecipe(List<String> pattern, Map<String,ItemContainer> keys, ItemContainer result, String category) {

    public static final Codec<ComRecipe> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.listOf().fieldOf("pattern").forGetter(ComRecipe::pattern),
                    Codec.unboundedMap(Codec.STRING, ItemContainer.CODEC).fieldOf("key").forGetter(ComRecipe::keys),
                    ItemContainer.CODEC.fieldOf("result").forGetter(ComRecipe::result),
                    Codec.STRING.fieldOf("category").forGetter(ComRecipe::category)
            ).apply(instance, ComRecipe::new)
    );

    public static DefaultedList<Ingredient> computeRecipe(Registry<DatapackItem> registry, ComRecipe recipe) {
        final DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(27, Ingredient.empty());

        int offset = 0;
        for(String line: recipe.pattern()) {
            for(int i = 0; i < line.length(); i++) {
                if(line.charAt(i) != ' ') {
                    final String key = line.substring(i, i + 1);
                    if(recipe.keys.containsKey(key))
                        ingredients.set(offset + i, Ingredient.ofStacks(recipe.keys().get(key).getItemStack(registry)));
                    else
                        throw new CodecException("Missing recipe key " + key);
                }
            }
            offset += line.length();
        }

        return ingredients;
    }
}
