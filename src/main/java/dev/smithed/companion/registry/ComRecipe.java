package dev.smithed.companion.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.smithed.companion.container.ItemContainer;
import io.netty.handler.codec.CodecException;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;
import java.util.Map;

/**
 * Representation of a smithed recipe, primarily used as a Codec to load information from a datapack.
 * @param pattern Array of strings representing the recipe
 * @param keys Map of character:item for the pattern
 * @param result Item to output from recipe
 * @param category Category of recipe. References a custom category, or one of the supported vanilla categories:
 *                 - minecraft:crafting_table
 *                 - minecraft:furnace
 *                 - minecraft:blast_furnace
 *                 - minecraft:smoker
 *                 - minecraft:campfire
 *                 - minecraft:brewing_stand
 *                 - minecraft:smithing_table
 */
public record ComRecipe(List<String> pattern, Map<String,ItemContainer> keys, ItemContainer result, Identifier category) {

    public static final Codec<ComRecipe> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.listOf().fieldOf("pattern").forGetter(ComRecipe::pattern),
                    Codec.unboundedMap(Codec.STRING, ItemContainer.CODEC).fieldOf("key").forGetter(ComRecipe::keys),
                    ItemContainer.CODEC.fieldOf("result").forGetter(ComRecipe::result),
                    Identifier.CODEC.fieldOf("category").forGetter(ComRecipe::category)
            ).apply(instance, ComRecipe::new)
    );

    /**
     * Transforms recipe representation into a list of ingredients, which can be passed into a recipe constructor.
     * @param registry DatapackItem registry, for item lookup
     * @param size number of ingredients in recipe, used to initialize size of DefaultedList
     * @return List of ingredients in recipe
     */
    public DefaultedList<Ingredient> computeRecipe(Registry<DatapackItem> registry, int size) {
        final DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(size, Ingredient.empty());

        int offset = 0;
        for(String line: this.pattern()) {
            for(int i = 0; i < line.length(); i++) {
                if(line.charAt(i) != ' ') {
                    final String key = line.substring(i, i + 1);
                    if(this.keys.containsKey(key)) {
                        if(this.keys.get(key).hasItemStackOverride())
                            ingredients.set(offset + i, Ingredient.ofStacks(this.keys().get(key).getItemStackOverride(registry)));
                        else
                            ingredients.set(offset + i, Ingredient.ofStacks(this.keys().get(key).getItemStack(registry)));
                    } else {
                        throw new CodecException("Missing recipe key " + key);
                    }
                }
            }
            offset += line.length();
        }

        return ingredients;
    }
}
