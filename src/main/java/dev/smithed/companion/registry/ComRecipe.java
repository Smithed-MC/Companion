package dev.smithed.companion.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.smithed.companion.container.ItemContainer;
import io.netty.handler.codec.CodecException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.Arrays;
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
public record ComRecipe(List<String> pattern, Map<String,ItemContainerKey> keys, ItemContainer result, Identifier category) {

    public static final Codec<ComRecipe> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.listOf().fieldOf("pattern").forGetter(ComRecipe::pattern),
                    Codec.unboundedMap(Codec.STRING, ItemContainerKey.CODEC).fieldOf("key").forGetter(ComRecipe::keys),
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
                        final ItemContainerKey entry = this.keys.get(key);
                        if(entry.isIngredientList()) {
                            final List<ItemStack> input = new ArrayList<>();
                            entry.ingredients().forEach(item -> {
                                if (entry.hasItemStackOverride())
                                    input.add(item.getItemStackOverride(registry));
                                else
                                    input.add(item.getItemStack(registry));
                            });
                            ingredients.set(offset + i, Ingredient.ofStacks(input.stream()));
                        } else {
                            if (entry.hasItemStackOverride())
                                ingredients.set(offset + i, Ingredient.ofStacks(entry.getItemStackOverride(registry)));
                            else
                                ingredients.set(offset + i, Ingredient.ofStacks(entry.getItemStack(registry)));
                        }
                    } else {
                        throw new CodecException("Missing recipe key " + key);
                    }
                }
            }
            offset += line.length();
        }
        return ingredients;
    }

    private static class ItemContainerKey extends ItemContainer {

        public static final Codec<ItemContainerKey> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.STRING.fieldOf("type").forGetter(ItemContainerKey::getType),
                        Identifier.CODEC.optionalFieldOf("id", new Identifier("")).forGetter(ItemContainerKey::getId),
                        ItemStack.CODEC.optionalFieldOf("item", ItemStack.EMPTY).forGetter(ItemContainerKey::getItemStack),
                        NbtCompound.CODEC.optionalFieldOf("item_override", new NbtCompound()).forGetter(ItemContainerKey::getItemStackOverride),
                        Codec.BYTE.optionalFieldOf("count", (byte) 1).forGetter(ItemContainerKey::getCount),
                        ItemContainer.CODEC.listOf().optionalFieldOf("items", List.of()).forGetter(ItemContainerKey::ingredients)
                ).apply(instance, ItemContainerKey::new));

        private final List<ItemContainer> ingredients;

        protected ItemContainerKey(String type, Identifier id, ItemStack itemStack, NbtCompound itemStackOverride, byte count, List<ItemContainer> ingredients) {
            super(type, id, itemStack, itemStackOverride, count, true);
            if(type.equals("smithed:item_list") && ingredients.size() == 0)
                throw new CodecException("smithed:item_list requires defined list of items as 'items' field.");
            if(!type.equals("smithed:item_list") && !type.equals("smithed:datapack_item_entry") && !type.equals("smithed:item_entry"))
                throw new CodecException("Invalid type " + type + ", expected 'smithed:datapack_item_entry', 'smithed:item_entry', or 'smithed:item_list'");
            this.ingredients = ingredients;
        }

        protected List<ItemContainer> ingredients() {
            return this.ingredients;
        }

        protected boolean isIngredientList() {
            return this.getType().equals("smithed:item_list");
        }
    }
}
