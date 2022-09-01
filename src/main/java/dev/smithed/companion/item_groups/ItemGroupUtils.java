package dev.smithed.companion.item_groups;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.smithed.companion.SmithedItemGroupExtensions;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static dev.smithed.companion.SmithedMain.logger;

public class ItemGroupUtils {

    // TODO
    // Split this class into a client and a serverside version as to avoid implementation errors
    // And to prepare to make use of Fabric's new method of client and server builds

    // Literal bandaid solution to an issue of load priority
    // But hey, it just works...
    public static Map<String, List<SimplifiedItemGroupEntry>> groupEntries = new HashMap<>();

    // We use this to tell the client it needs to repack itemgroups
    // if we don't have a check like this in place itemgroups will be repeatedly stacked
    private static boolean dirty = false;

    // Never used outside mixin context however you can't modify things within mixins outside of mixins.
    // Stores a list of indexes which will later be used to extract smithed itemgroups.
    public static List<Integer> indexes = new ArrayList<>();

    // A raw map of display components for Itemgroups.
    // Display elements will not need to be modified regularly so they will stay in this mostly unchanging list.
    // If you're asking WHY this is this way, i just wanted to write more comments.
    public static Map<String, NbtCompound> groupDisplay = new HashMap<>();

    // A list of stuff being merged into item groups.
    // The string is the name of the group in question while the itemstack is the item in question.
    // At the conclusion of reload these are all merged into their respective itemgroups at once as NBT.
    public static Map<String, List<ItemStack>> queue = new HashMap<>();

    // The itemgroups in question.
    // They aren't loaded into a class but rather converted directly into NBT data which is stored instead.
    // Upon arrival to the client these are transformed into real itemgroups which then get processed.
    public static List<NbtCompound> itemGroups = new ArrayList<>();

    // Operations performed on entries.
    // These can be added to, to create more possible operations.
    // Each operation takes in an entry and the server in question.
    // IMPORTANT: these entries exist for the sole purpose of being used for group entries, not external stuff.
    public static void parseEntries(MinecraftServer server) {
        groupEntries.forEach((groupID, entrylist) -> {
            entrylist.forEach(entry -> {
                List<ItemStack> toAdd = queue.getOrDefault(groupID, new ArrayList<ItemStack>());
                switch (entry.getType()) {
                    // Generates item from item entry within itemgroup file.
                    case "minecraft:item" -> {
                        toAdd.add(tryParseEntryAsStack(entry));
                    }

                    // Generates items from loot table, only works with default context.
                    case "minecraft:loot_table" -> {
                        LootTable table = server.getLootManager().getTable(new Identifier(entry.getID()));
                        List<ItemStack> items = table.generateLoot(new LootContext.Builder(server.getOverworld()).build(LootContextType.create().build()));
                        toAdd.addAll(items);
                    }

                    // Iterates through every loot table directory matching for one that lines up with the desired path.
                    // Afterwards it stores the result within the queue
                    case "smithed:loot_directory" -> {
                        List<Identifier> lootDirectories = new ArrayList<>(server.getLootManager().getTableIds().stream().toList());
                        List<ItemStack> itemList = new ArrayList<>();

                        lootDirectories.sort(Comparator.comparing(Identifier::getPath));

                        lootDirectories.forEach(identifier -> {
                            logger.info("cur path: " + identifier.getPath() + ", target path: " + entry.getID());
                            if (identifier.toString().startsWith(entry.getID())) {
                                logger.info("match.");
                                LootTable table = server.getLootManager().getTable(identifier);
                                List<ItemStack> items = table.generateLoot(new LootContext.Builder(server.getOverworld()).build(LootContextType.create().build()));
                                itemList.addAll(items);
                            }
                        });
                        toAdd.addAll(itemList);
                        lootDirectories.clear();
                    }
                }
                queue.put(groupID, toAdd);
            });
        });
    }


    // Store display data for itemgroups.
    // This will eventually be merged with the itemgroups themselves.
    // If you're asking "why?", I find it makes it easier to keep track of things.
    public static void postGroupDisplay(String groupID, String texture, SimplifiedItemGroupEntry icon) {
        NbtCompound compound = new NbtCompound();
        ItemStack stack = tryParseEntryAsStack(icon);

        compound.putString("texture", (texture != null ? texture : "DEFAULT"));
        compound.put("icon", stack.writeNbt(new NbtCompound()));

        groupDisplay.put(groupID, compound);
    }


    // Pack Itemgroups into NBT to be shipped off in one big batch to the client.
    // Only needs to be called once after itemgroups are reloaded because itemgroups are already packed.
    public static void packItemGroups() {
        if(!dirty) return;
        queue.forEach((group, stacks) -> {
            NbtCompound compound = new NbtCompound();

            compound.putString("id", group);
            NbtList list = new NbtList();
            stacks.forEach(stack -> {
                NbtCompound item = stack.writeNbt(new NbtCompound());
                list.add(item);
            });

            compound.put("entries", list);
            compound.put("display", groupDisplay.getOrDefault(group, new NbtCompound()));

            itemGroups.add(compound);
        });
        dirty = false;
    }

    // Smithed specific method for building itemgroups.
    // Similar in implementation to fabric default method however this remembers smithed groups.
    // Why? So they can be de-registered of course.
    public static void buildGroup(@NotNull NbtCompound group) {
        Identifier identifier = new Identifier(group.getString("id"));

        DefaultedList<ItemStack> stacks = DefaultedList.of();
        for (NbtElement element : group.getList("entries", NbtElement.COMPOUND_TYPE)) {
            if (element instanceof NbtCompound compound)
                stacks.add(ItemStack.fromNbt(compound));
        }


        if(!identifier.getNamespace().equals("minecraft")) {
            ItemGroup igroup = FabricItemGroupBuilder.create(identifier).icon(
                            () -> ItemStack.fromNbt(group.getCompound("display").getCompound("icon")))
                    .appendItems(stacks1 -> stacks1.addAll(stacks))
                    .build();

            indexes.add(igroup.getIndex());
        }
        else {
            for (int i = 0; i < ItemGroup.GROUPS.length - 1; i++) {
                if(ItemGroup.GROUPS[i].getName().equals(identifier.getPath())) {
                    logger.info("match found.");
                    ItemGroup.MISC.appendStacks(stacks);
                    ItemGroup.GROUPS[i].appendStacks(stacks);
                }
            }
        }
    }

    // Cleanup function to prep for reloads
    // Null checks can suck my ass
    public static void cleanUp() {
        if(groupDisplay != null) groupDisplay.clear();
        if(queue != null) queue.clear();
        if(itemGroups != null) itemGroups.clear();
        if (groupEntries != null) groupEntries.clear();
    }

    // Tries to parse itemgroup entry as a stack,
    // If it fails it gives an NBT printout
    public static ItemStack tryParseEntryAsStack(SimplifiedItemGroupEntry entry) {
        ItemStack stack = new ItemStack(Registry.ITEM.get(Identifier.tryParse(entry.getID())));
        if(entry.getNbt() != null) try {
            stack.setNbt(StringNbtReader.parse(entry.getNbt()));
        } catch (CommandSyntaxException e) {
            logger.error("failure to parse stack with NBT: {}, could not be parsed due to NBT Exception", entry.getNbt());
        }
        return stack;
    }

    public static void markDirty() {
        ItemGroupUtils.dirty = true;
    }

    public static void destroyGroups() {
        ((SmithedItemGroupExtensions) ItemGroup.BUILDING_BLOCKS).declutterArray();

    }
}
