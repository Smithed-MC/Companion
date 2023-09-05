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

@SuppressWarnings("ALL")
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

    /*
     * Note: server does not start in time for loot tables to be process on initialization. Delaying resoloution until
     * the first time stack() is called seems to work, but may create problems in rare cases.
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

    public ItemStack stack() {
        if (this.stack == null)
            this.stack = resolveLootTable();
        return stack;
    }

    private Identifier id() {
        return new Identifier("");
    }
}
