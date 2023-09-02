package dev.smithed.companion.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.smithed.companion.registry.DatapackItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.List;

import static dev.smithed.companion.utils.RegistryUtils.ENTRY_TYPE_REGISTRY;

public class ItemGroupUtils {

    public static final Codec<Entry.EntryType> ENTRY_TYPE_CODEC = ENTRY_TYPE_REGISTRY.getCodec();
    public static final Codec<Entry> ENTRY_CODEC = ENTRY_TYPE_CODEC.dispatch("type", Entry::getType, Entry.EntryType::getCodec);

    /*
    Generic Data holder class for itemgroups
    This class has three parameters within its constructor, and they are:
    - Identifier identifier: The Itemgroups identifier like `smithed:foo`
    - ItemStack Icon: The item that will display on the group tab, like stone
     */
    public static class ItemGroupData {

        private ItemStack icon;
        private Text display_name;
        private String texture;
        private List<Entry> entries;

        public ItemGroupData(ItemStack icon, Text display_name, String texture, List<Entry> entries) {
            this.icon = icon;
            this.display_name = display_name;
            this.texture = texture;
            this.entries = entries;
        }

        public ItemStack getIcon() { return this.icon; }
        public Text getDisplayName() { return this.display_name.copy(); }
        public String getTexture() { return this.texture; }
        public List<Entry> getEntries() { return this.entries; }

        /*
        Codec breakdown: The identifier will be the identifying data
         */
        public static Codec<ItemGroupData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ItemStack.CODEC.fieldOf("icon").forGetter(ItemGroupData::getIcon),
                    Codecs.TEXT.fieldOf("display_name").forGetter(ItemGroupData::getDisplayName),
                    Codecs.NON_EMPTY_STRING.optionalFieldOf("texture","items.png").forGetter(ItemGroupData::getTexture),
                    ENTRY_CODEC.listOf().fieldOf("entries").forGetter(ItemGroupData::getEntries)
            ).apply(instance, ItemGroupData::new)
        );
    }

    /*
    Contains all the different possible Itemgroup entries and their CODEC's
    Class is static because a default entry should never be registered or instantiated
     */
    public abstract static class Entry {
        public EntryType getType() {
            return null;
        }

        public Collection<ItemStack> getCollection(World world) {
            return null;
        }

        /*
        Stores a loot table which will eventually be used to generate a list of items to append to a group
        This class has one parameter:
        - Identifier identifier: the identifier of the loot table to be used.
         */
        public static class LootTableEntry extends Entry {

            private final Identifier identifier;

            public LootTableEntry(Identifier identifier) {
                this.identifier = identifier;
            }

            public Identifier getIdentifier() { return this.identifier; }

            @Override
            public EntryType getType() {
                return new EntryType(CODEC);
            }

            public static final Codec<LootTableEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        Identifier.CODEC.fieldOf("identifier").forGetter(LootTableEntry::getIdentifier)
                    ).apply(instance, LootTableEntry::new)
            );
        }

        /*
        Stores an itemstack only.
        This class has one parameter:
        - ItemStack item_stack: The item that will be put in at this entry
         */
        public static class ItemEntry extends Entry {

            private final ItemStack item_stack;

            public ItemEntry(ItemStack item_stack) {
                this.item_stack = item_stack;
            }

            public ItemStack getItemStack() { return this.item_stack; }

            @Override
            public EntryType getType() { return new EntryType(CODEC); }

            @Override
            public Collection<ItemStack> getCollection(World world) {
                return List.of(item_stack);
            }

            public static final Codec<ItemEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                            ItemStack.CODEC.fieldOf("item").forGetter(ItemEntry::getItemStack)
                    ).apply(instance, ItemEntry::new)
            );
        }

        /*
        This entry type doesn't provide any entries of its own rather it provides a means of sorting included entries.
         */
        public static class SortedEntry extends Entry {

        }

        public static class DatapackItemEntry extends Entry {
            private final Identifier identifier;

            public DatapackItemEntry(Identifier identifier) {this.identifier = identifier; }

            public Identifier getIdentifier() { return identifier; }

            @Override
            public EntryType getType() {
                return new EntryType(CODEC);
            }

            public static final Codec<DatapackItemEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Identifier.CODEC.fieldOf("id").forGetter(DatapackItemEntry::getIdentifier)
            ).apply(instance, DatapackItemEntry::new));

            @Override
            public Collection<ItemStack> getCollection(World world) {
                return List.of(((DatapackItem.DatapackItemHandler)world).smithed$getDatapackItem(identifier));
            }
        }

        public static class EntryType {

            private final Codec<? extends Entry> codec;

            <T extends Entry> EntryType(Codec<T> codec) {
                this.codec = codec;
            }

            public Codec<? extends Entry> getCodec() {
                return codec;
            }
        }
    }

}
