package dev.smithed.companion.utils;

import dev.smithed.companion.SmithedMain;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.impl.client.itemgroup.CreativeGuiExtensions;
import net.fabricmc.fabric.mixin.itemgroup.ItemGroupsMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import java.util.ArrayList;
import java.util.List;

public class ItemGroupHandler {

    /*
    This list holds itemgroups registered by smithed which will be used to delete them at a later time
     */
    private static final List<RegistryKey<ItemGroup>> registeredItemGroups = new ArrayList<>();

    /*
    When the player joins a world we want to fetch a reference to all synced itemgroups and load them onto the itemgroup registry.
    This method has three parameters and they are such:
    - CLientPlayNetworkHandler networkhandler: The standard client network handler, in case its needed.
    - PacketSender packetSender: additional context when needed
    - MinecraftClient client: the parameter of interest, from there we fetch world data and whatever else we need.
     */
    public static void loadGroups(ClientPlayNetworkHandler networkHandler, PacketSender packetSender, MinecraftClient client) {
        if(client.world != null) {
            var itemGroupRegistry = client.world.getRegistryManager().get(RegistryUtils.ITEMGROUP_REGISTRY);

            /*
            Iterate through each entry and write the corresponding data into an Itemgroup
            After that's done seal up the group behind us and re-collect the pages.
             */
            RegistryUtils.thawRegistry(Registries.ITEM_GROUP);
            itemGroupRegistry.forEach(itemGroupData -> {
                var Itemgroup = FabricItemGroup.builder()
                        .displayName(itemGroupData.getDisplayName())
                        .icon(itemGroupData::getIcon)
                        .texture(itemGroupData.getTexture())
                        .entries((displayContext, entries) -> {
                            itemGroupData.getEntries().forEach(entry -> entries.addAll(entry.getCollection(client.world)));
                        })
                        .build();
                Registry.register(Registries.ITEM_GROUP, itemGroupRegistry.getId(itemGroupData).toString(), Itemgroup);
                registeredItemGroups.add(RegistryKey.of(RegistryKeys.ITEM_GROUP, itemGroupRegistry.getId(itemGroupData)));
            });

            Registries.ITEM_GROUP.freeze();
            ItemGroups.collect();
        }
    }

    /*
    When the player leaves the world or  we no longer want our groups to stick, we have to remove them from the registry.
    After that's done we re-freeze the registry.
    This method has two parameters, and they are as follows:
    - ClientPlayNetworkHandler networkHandler: the network handler for the game
    - MinecraftClient client: the client which is running this code
     */
    public static void discardGroups(ClientPlayNetworkHandler networkHandler, MinecraftClient client) {
        RegistryUtils.thawRegistry(Registries.ITEM_GROUP);
        registeredItemGroups.forEach(itemgroup -> RegistryUtils.removeRegistryEntry(Registries.ITEM_GROUP, itemgroup));
        registeredItemGroups.clear();
        Registries.ITEM_GROUP.freeze();
        ItemGroups.collect();
    }

    /*
    When the player gets sent the reload event discard current packs and then refresh them
    TODO: Eventually create and attach a proper event to this, right now it runs on packets. Big NoNo when the ability to do this properly seemingly already exists.
     */
    public static void reloadGroups(MinecraftClient client) {
        discardGroups(null, client);
        loadGroups(null ,null, client);
    }
}
