package dev.smithed.companion.container;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.smithed.companion.registry.DatapackItem;
import io.netty.handler.codec.CodecException;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ItemContainer {

    private final String type;
    private final Identifier id;
    private final ItemStack itemStack;

    public static final Codec<ItemContainer> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.STRING.fieldOf("type").forGetter(ItemContainer::getType),
                Identifier.CODEC.optionalFieldOf("id", new Identifier("")).forGetter(ItemContainer::getId),
                ItemStack.CODEC.optionalFieldOf("item", ItemStack.EMPTY).forGetter(ItemContainer::getItemStack)
        ).apply(instance, ItemContainer::new));

    private ItemContainer(String type, Identifier id, ItemStack itemStack) {
        final boolean isIdEmpty = id.getPath().equals("");
        switch (type) {
            case "smithed:item_entry" -> {
                if(isIdEmpty && itemStack == ItemStack.EMPTY)
                    throw new CodecException("smithed:item_entry requires an 'id' field or an 'item' field");
            }
            case "smithed:datapack_item_entry" -> {
                if(isIdEmpty)
                    throw new CodecException("smithed:datapack_item_entry requires an 'id' field");
            }
            default -> throw new CodecException("Invalid type " + type + ", expected 'smithed:datapack_item_entry' or 'smithed:item_entry'");
        }

        this.type = type;
        this.id = id;
        if(isIdEmpty)
            this.itemStack = itemStack;
        else
            this.itemStack = new ItemStack(Registries.ITEM.get(this.id));
    }

    private String getType() {
        return type;
    }

    @Nullable
    private Identifier getId() {
        return id;
    }

    @Nullable
    private ItemStack getItemStack() {
        return itemStack;
    }

    public ItemStack getItemStack(Registry<DatapackItem> registry) {
        if(type.equals("smithed:item_entry")) {
            return this.itemStack;
        }
        if(type.equals("smithed:datapack_item_entry")) {
                final DatapackItem item = registry.get(this.id);
                if(item != null)
                    return item.stack();
                else
                    throw new CodecException("Unknown DatapackItem " + id);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public String toString() {
        if(type.equals("smithed:datapack_item_entry"))
            return "ItemContainer[type=smithed:datapack_item_entry, id=" + this.id + "]";
        return "ItemContainer[type=smithed:item_entry, itemStack=" + this.itemStack + "]";
    }
}
