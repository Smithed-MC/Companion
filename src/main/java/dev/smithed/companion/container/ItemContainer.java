package dev.smithed.companion.container;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.smithed.companion.registry.DatapackItem;
import dev.smithed.companion.utils.NBTUtils;
import io.netty.handler.codec.CodecException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * Generalized container for holding a DatapackItem, or an ItemStack.
 * Primarily used as a Codec to load data from a datapack.
 */
public class ItemContainer {

    private final String type;
    private final Identifier id;
    private final ItemStack itemStack;
    private final NbtCompound itemStackOverride;
    private final byte count;

    public static final Codec<ItemContainer> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.STRING.fieldOf("type").forGetter(ItemContainer::getType),
                Identifier.CODEC.optionalFieldOf("id", new Identifier("")).forGetter(ItemContainer::getId),
                ItemStack.CODEC.optionalFieldOf("item", ItemStack.EMPTY).forGetter(ItemContainer::getItemStack),
                NbtCompound.CODEC.optionalFieldOf("item_override", new NbtCompound()).forGetter(ItemContainer::getItemStackOverride),
                Codec.BYTE.optionalFieldOf("count", (byte) 1).forGetter(ItemContainer::getCount)
        ).apply(instance, ItemContainer::new));

    /**
     * Init container if type 'item_entry' and an ID or ItemStack is present,
     * or if type 'datapack_item_entry' and an ID is present. Fail otherwise.
     * @param type String type, must be 'smithed:item_entry' or 'smithed:datapack_item_entry'
     * @param id ID of item for 'item_entry', where it is converted to an ItemStack. ID of DatapackItem for 'datapack_item_entry'
     * @param itemStack If type is 'item_entry,' and ID is ommited, ItemStack is the item to return from this container
     */
    private ItemContainer(String type, Identifier id, ItemStack itemStack, NbtCompound itemStackOverride, byte count) {
        final boolean isIdEmpty = id.getPath().equals("");
        switch (type) {
            // Fail if 'item_entry' and ID & itemStack are omitted
            case "smithed:item_entry" -> {
                if(isIdEmpty && itemStack == ItemStack.EMPTY)
                    throw new CodecException("smithed:item_entry requires an 'id' field or an 'item' field");
            }
            // Fail it 'datapack_item_entry' and ID is omitted
            case "smithed:datapack_item_entry" -> {
                if(isIdEmpty)
                    throw new CodecException("smithed:datapack_item_entry requires an 'id' field");
            }
            // Fail if type is invalid
            default -> throw new CodecException("Invalid type " + type + ", expected 'smithed:datapack_item_entry' or 'smithed:item_entry'");
        }

        this.type = type;
        this.id = id;
        this.count = count;
        this.itemStackOverride = itemStackOverride;
        // If no ID, save raw itemStack. Otherwise, convert item ID to an ItemStack
        // ItemStack is air if item is 'invalid,' ie. is a DatapackItem
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

    private byte getCount() {
        return count;
    }

    private NbtCompound getItemStackOverride() {
        return this.itemStackOverride;
    }

    /**
     * Returns ItemStack this container holds. Returns ItemStack.EMPTY if itemStack doesn't exist (ie. DatapackItem
     * isn't registered).
     * Verify is actually returned using itemStack.count() > 0
     * @param registry Current registry of DatapackItems, for retrieval if needed.
     * @return ItemStack this class represents, or ItemStack.EMPTY if the stack doesn't exist.
     */
    public ItemStack getItemStack(Registry<DatapackItem> registry) {
        if(type.equals("smithed:item_entry")) {
            return this.itemStack.copyWithCount(this.count);
        }
        if(type.equals("smithed:datapack_item_entry")) {
                final DatapackItem item = registry.get(this.id);
                if(item != null)
                    return item.stack().copyWithCount(this.count);
                else
                    throw new CodecException("Unknown DatapackItem " + id);
        }
        return ItemStack.EMPTY;
    }

    public ItemStack getItemStackOverride(Registry<DatapackItem> registry) {
        if(!hasItemStackOverride())
            return this.getItemStack();
        final ItemStack base = this.getItemStack(registry);
        final ItemStack out = this.itemStackOverride.contains("id", NbtCompound.STRING_TYPE)
                ? new ItemStack(Registries.ITEM.get(new Identifier(this.itemStackOverride.getString("id"))))
                : base;
        out.setCount(base.getCount());
        if(base.getNbt() != null)
            out.setNbt(base.getNbt().copyFrom(this.itemStackOverride.getCompound("tag")));
        else
            out.setNbt(this.itemStackOverride.getCompound("tag"));
        return out;
    }

    public boolean hasItemStackOverride() {
        return this.itemStackOverride.getSize() > 0 && this.itemStackOverride.contains("tag", NbtCompound.COMPOUND_TYPE);
    }

    @Override
    public String toString() {
        if(type.equals("smithed:datapack_item_entry"))
            return "ItemContainer[type=smithed:datapack_item_entry, id=" + this.id + "]";
        return "ItemContainer[type=smithed:item_entry, itemStack=" + this.itemStack + "]";
    }
}
