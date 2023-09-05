package dev.smithed.companion.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.smithed.companion.utils.ServerEventUtils;
import io.netty.handler.codec.CodecException;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Creates a datapack item bound registry, which serves to act as a stand-in for datapack items and their associated data
 * @author dragoncommands, ImCoolYeah105
 */
public class DatapackItem {

    private static final Logger LOGGER = LoggerFactory.getLogger("smithed-companion");

    private ItemStack stack;
    private final Identifier id;

    public static final Codec<DatapackItem> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ItemStack.CODEC.optionalFieldOf("item", ItemStack.EMPTY).forGetter(DatapackItem::stack),
                    Identifier.CODEC.optionalFieldOf("loot_table_id", new Identifier("")).forGetter(DatapackItem::id)
            ).apply(instance, DatapackItem::new)
    );

    /**
     * DatapackItem constructor. Takes an ItemStack, or a loot table Identifier.
     *
     * Note: server does not start in time for loot tables to be process on initialization. Delaying resolution until
     * the first time stack() is called seems to work, but may create problems in rare cases.
     *
     * @param stack ItemStack this DatapackItem represents
     * @param id Identifier of the loot table to grab the ItemStack from. Must drop exactly 1 item.
     */
    public DatapackItem(ItemStack stack, Identifier id) {
        this.id = id;
        if(stack != ItemStack.EMPTY) {
            this.stack = stack;
        } else if(!id.getPath().equals("")) {
            this.stack = null;
        } else {
            throw new CodecException("DatapackItem missing 'item' or 'loot_table_id' field.");
        }
    }

    /**
     * Attempts to resolve the loot table of this DatapackItem.
     * Must happen after loot tables have been initialized.
     * Loot table must drop exactly 1 item to succeed.
     * @return ItemStack dropped by Loot table
     */
    private ItemStack resolveLootTable() {
        try {
            final MinecraftServer server = ServerEventUtils.getCurrentServer();
            final LootTable lootTable = server.getLootManager().getLootTable(id);
            if (lootTable == null)
                throw new NullPointerException("No loot table found at " + id);
            final List<ItemStack> loot = lootTable.generateLoot(new LootContextParameterSet(server.getOverworld(), Map.of(), Map.of(), 0f));
            if (loot.size() != 1)
                throw new CodecException("DatapackItem loot table " + id + " must return exactly 1 item, actually returned " + loot.size() + " items");
            return loot.get(0);
        } catch (Exception e) {
            LOGGER.warn("Failed to assign loot table " + id + " to DatapackItem. " + e.getLocalizedMessage());
            return ItemStack.EMPTY;
        }
    }

    /**
     * Grabs ItemStack the DatapackItem represents.
     * Attempts to resolve the loot table on first call, if needed.
     * @return ItemStack this item represents. Returns ItemStack.EMPTY if the loot table resolution failed.
     */
    public ItemStack stack() {
        if (this.stack == null)
            this.stack = resolveLootTable();
        return stack;
    }

    /**
     * Codec requires a getter for serializing this class (sending to the client).
     * However, only the ItemStack is needed at this point, so an empty Identifier is sent instead.
     * @return Empty Identifier
     */
    private Identifier id() {
        return new Identifier("");
    }

    /**
     * General interface to handle smithed datapack items in world
     */
    public interface DatapackItemHandler {

        ItemStack smithed$getDatapackItem(Identifier identifier);

        void smithed$registerItem(DatapackItem item);

        void smithed$dumpItems();

    }
}
