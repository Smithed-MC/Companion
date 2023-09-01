package dev.smithed.companion.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class DatapackItemUtils {

    /*
    Creates a datapack item bound registry, which serves to act as a stand-in for datapack items and their associated data
     */
    public static class DatapackItem {
        private ItemStack stack;

        public DatapackItem(ItemStack stack) {
            this.stack = stack;
        }

        public ItemStack getStack() { return stack; }

        public static final Codec<DatapackItem> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ItemStack.CODEC.optionalFieldOf("item", ItemStack.EMPTY).forGetter(DatapackItem::getStack)
            ).apply(instance, DatapackItem::new)
        );
    }

    public interface DatapackItemHandler {

        ItemStack smithed$getDatapackItem(Identifier identifier);

        void smithed$registerItem(DatapackItem item);

        void smithed$dumpItems();

    }
}
