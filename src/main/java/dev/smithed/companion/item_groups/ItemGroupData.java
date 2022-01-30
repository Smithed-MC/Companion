package dev.smithed.companion.item_groups;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.smithed.companion.SmithedMain;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ItemGroupData {

    public ItemGroupData(String name, ItemStack icon, String texture, List<ItemStack> stacks) {
        this.name = name;
        this.icon = icon;
        this.texture = texture;
        this.itemStacks = stacks;
    }

    private final String name;
    private final ItemStack icon;
    private final String texture;
    private List<ItemStack> itemStacks;

    public String getName() {
        return name;
    }
    public ItemStack getIcon() {
        return icon;
    }
    public String getTexture() {
        return texture;
    }
    public List<ItemStack> getItemStacks() {
        return itemStacks;
    }

    public void toNbt(NbtCompound nbtCompound) {
        // create nbt compounds
        NbtList items = new NbtList();
        NbtCompound icon = new NbtCompound();

        //Write items to itemlist
        for (ItemStack stack : itemStacks) {
            NbtCompound stackCompound = new NbtCompound();
            stack.writeNbt(stackCompound);
            items.add(stackCompound);
        }

        // write item NBT to icon
        getIcon().writeNbt(icon);
        nbtCompound.put("icon", icon);
        // Write names and identifiers
        nbtCompound.putString("group_name",getName());
        // write items to nbt
        nbtCompound.put("item_group_items", items);
    }

    public static ItemGroupData fromNBT(NbtCompound compound) {
        List<ItemStack> stacks = new ArrayList<>();
        NbtList list = compound.getList("item_group_items", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < list.size(); i++) {
            NbtCompound stackCompound = list.getCompound(i);
            stacks.add(ItemStack.fromNbt(stackCompound));
        }

        return new ItemGroupData(
                compound.getString("group_name"),
                ItemStack.fromNbt(compound.getCompound("icon")),
                null,
                stacks
        );
    }

    // Converts ItemGroup data to actual ItemGroup
    // Textures not yet implemented
    public ItemGroup toItemGroup() {
        FabricItemGroupBuilder group = FabricItemGroupBuilder.create(new Identifier(this.name));
        group.icon(this::getIcon);
        group.appendItems(stacks -> stacks.addAll(this.getItemStacks()));
        return group.build();
    }

    // Adds items to pre-formed group, this will not work with registered groups
    // don't attempt to use this method to directly adjust initialized groups again, me
    public void addItems(ItemStack... stacks) {
        itemStacks.addAll(Arrays.stream(stacks).toList());
    }
    public void addItems(List<ItemStack> stacks) { itemStacks.addAll(stacks); }

    public static void loadItemGroupData(InputStream stream, Identifier id, Map<String,List<SimplifiedItemGroupEntry>> providedMap) {
        Gson gson = new Gson();

        SimplifiedItemGroup simplifiedGroup = gson.fromJson(new InputStreamReader(stream), SimplifiedItemGroup.class);
        if(simplifiedGroup != null) {

            // Create a temporary list for non-linear grouping
            List<SimplifiedItemGroupEntry> queue = new ArrayList<>();

            if (simplifiedGroup.getOperation().equals("smithed:create")) {
                ItemStack icon = new ItemStack(Items.AIR);
                List<ItemStack> stacks = new ArrayList<>();
                // check if icon is of type: item, if so treat it like one
                if (simplifiedGroup.getIcon().getType().equals("minecraft:item") && simplifiedGroup.getIcon() != null)
                    icon = tryParseEntryAsStack(simplifiedGroup.getIcon(), "Icon");

                // Read Entries
                for (SimplifiedItemGroupEntry entry : simplifiedGroup.getEntries()) {
                    if (entry.getType().equals("minecraft:item"))
                        stacks.add(tryParseEntryAsStack(entry,"Group addition"));
                    else if (entry.getType().equals("minecraft:loot_table") || entry.getType().equals("smithed:item_group") || entry.getType().equals("smithed:loot_directory"))
                        queue.add(entry);
                    else
                        SmithedMain.logger.error("Invalid entry type of: " + entry.getType() + ", are you sure you typed it correctly?");

                }
                // add to unregistered groups to be applied upon client request
                SmithedMain.unregisteredItemGroups.put(simplifiedGroup.getID(), new ItemGroupData(
                        simplifiedGroup.getID(),
                        icon,
                        simplifiedGroup.getTexture(),
                        stacks
                ));
                return;
            } else if (simplifiedGroup.getOperation().equals("smithed:append")) {

                return;
            }

            // put into list
            if (providedMap.get(simplifiedGroup.getName()).isEmpty()|| !providedMap.containsKey(simplifiedGroup.getName()))
                providedMap.put(simplifiedGroup.getName(), queue);
            providedMap.get(simplifiedGroup.getName()).addAll(queue);
        }
        SmithedMain.logger.warn("Invalid item group: {}, fix this group or remove it.", id.toString());
    }


    public static ItemStack tryParseEntryAsStack(SimplifiedItemGroupEntry entry, String errMsg) {
        ItemStack stack = new ItemStack(Registry.ITEM.get(Identifier.tryParse(entry.getID())));
        // try Icon NBT application
        if(entry.getNbt() != null) try {
            stack.setNbt(StringNbtReader.parse(entry.getNbt()));
        } catch (CommandSyntaxException e) {
            // could i have done this differently? yes. will i? no
            SmithedMain.logger.error(errMsg + " of item: " + stack + ", with NBT: " + entry.getNbt() + ", could not be parsed due to NBT Exception");
        }
        return stack;
    }
}
