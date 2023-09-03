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
        this.type = type;

        if(id.getPath().equals(""))
            this.id = null;
        else
            this.id = id;

        if(itemStack == ItemStack.EMPTY)
            this.itemStack = null;
        else
            this.itemStack = itemStack;

        if(type.equals("smithed:item_entry") && this.id == null && this.itemStack == null)
            throw new CodecException("smithed:item_entry requires an 'id' field or an 'item' field");
        if(type.equals("smithed:datapack_item_entry") && this.id == null)
            throw new CodecException("smithed:datapack_item_entry requires an 'id' field");
    }

    private String getType() {
        return type;
    }

    public boolean hasId() {
        return id == null;
    }

    @Nullable
    public Identifier getId() {
        return id;
    }

    @Nullable
    public ItemStack getItemStack() {
        return itemStack;
    }

    public ItemStack getItemStack(Registry<DatapackItem> registry) {
        if(type.equals("smithed:item_entry")) {
            if(id != null)
                return new ItemStack(Registries.ITEM.get(this.id));
            else
                return this.itemStack;
        }
        if(type.equals("smithed:datapack_item_entry")) {
                final DatapackItem item = registry.get(this.id);
                if(item != null)
                    return item.stack();
        }
        return null;
    }

}
