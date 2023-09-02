package dev.smithed.companion.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/*
Creates a datapack item bound registry, which serves to act as a stand-in for datapack items and their associated data
 */
public record DatapackItem(ItemStack stack) {

    public static final Codec<DatapackItem> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ItemStack.CODEC.optionalFieldOf("item", ItemStack.EMPTY).forGetter(DatapackItem::stack)
            ).apply(instance, DatapackItem::new)
    );

    public static ItemStack parseItemEntry(Registry<DatapackItem> registry, NbtCompound entry) {
        final String type = entry.getString("type");
        if(type == null) return null;

        if(type.equals("smithed:item_entry")) {
            if(entry.contains("id",8))
                return new ItemStack(Registries.ITEM.get(new Identifier(entry.getString("id"))));
            if(entry.contains("item",10))
                return ItemStack.fromNbt((NbtCompound) entry.get("item"));
        }
        if(type.equals("smithed:datapack_item_entry")) {
            if(entry.contains("id",8)) {
                final DatapackItem item = registry.get(new Identifier(entry.getString("id")));
                if(item != null)
                    return item.stack();
            }
        }
        return null;
    }

    public interface DatapackItemHandler {

        ItemStack smithed$getDatapackItem(Identifier identifier);

        void smithed$registerItem(DatapackItem item);

        void smithed$dumpItems();

    }
}
