package dev.smithed.companion.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class DatapackItemUtils {

    /*
    Creates a datapack item bound registry, which serves to act as a stand-in for datapack items and their associated data
     */
    public static class DatapackItem {
        private Identifier identifier;
        private ItemStack stack;

        public DatapackItem(Identifier identifier, ItemStack stack) {
            this.identifier = identifier;
            this.stack = stack;
        }

        public Identifier getIdentifier() { return identifier; }
        public ItemStack getStack() { return stack; }

        public static final Codec<DatapackItem> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Identifier.CODEC.fieldOf("id").forGetter(DatapackItem::getIdentifier),
                    ItemStack.CODEC.optionalFieldOf("item", ItemStack.EMPTY).forGetter(DatapackItem::getStack)
            ).apply(instance, DatapackItem::new)
        );
    }

    public interface DatapackItemHandler {

        ItemStack getDatapackItem(Identifier identifier);

        void registerItem(DatapackItem item);

    }
}
